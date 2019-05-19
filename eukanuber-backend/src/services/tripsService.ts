import Knex from 'knex';
import db from '../db/db';
import { ICreateTripData, ITrip, TripStatus, UserValidationStatus, UserTypes } from '../models';
import googleMapsService from './googleMapsService';
import UserState from '../models/UserState';
import userService from './userService';

require('dotenv').config();

async function getTrips(status: TripStatus) {
  const trips = await db
    .table('trips')
    .orderBy('createdDate', 'desc')
    .modify((queryBuilder: Knex.QueryBuilder) => {
      if (status) {
        queryBuilder.where('status', status);
      }
    })
    .select();

  return trips.map((trip: any) => ({ ...trip, pets: trip.pets.split(',') }));
}

async function getTripById(id: string): Promise<ITrip> {
  const result = await db
    .table('trips')
    .where('id', id)
    .select()
    .first();

  if (!result) {
    return undefined;
  }

  return {
    ...result,
    pets: result.pets.split(','),
  };
}

async function createTrip(trip: ICreateTripData): Promise<ITrip> {
  // TODO: Move to controller
  const originCoordinates = await googleMapsService.getGeocode(trip.origin);
  const destinationCoordinates = await googleMapsService.getGeocode(trip.destination);
  const routes = await googleMapsService.getDirections(originCoordinates, destinationCoordinates);
  const routeData = await calculateTripData(routes, trip.pets, trip.escort);

  const newTrip = {
    ...trip,
    pets: trip.pets.join(',').replace(/\s/g, ''),
    originCoordinates,
    destinationCoordinates,
    clientId: trip.clientId,
    status: TripStatus.PENDING,
    createdDate: new Date().toISOString(),
    ...routeData,
    routes,
  };

  const tripCreated = await db
    .insert(newTrip)
    .into('trips')
    .returning('*');

  return {
    ...tripCreated[0],
    pets: trip.pets,
  };
}

async function driverHasTripWithStatus(tripId: string, tripStatus: TripStatus) {
  try {
    const status = await db
      .table('trips')
      .where('id', tripId)
      .select('status');
    return status[0].status === tripStatus;
  } catch (e) {
    return false;
  }
}

const updateTransaction = () => new Promise(resolve => db.transaction(resolve));

async function driverAcceptTrip(tripId: string, driverId: string) {
  return await changeDriverTripStatus(tripId, driverId, TripStatus.DRIVER_GOING_ORIGIN, UserState.TRAVELLING);
}

async function driverRejectTrip(tripId: string, driverId: string) {
  await userService.penalizeDriverRejectTrip(driverId, tripId);
  return await changeDriverTripStatus(tripId, driverId, TripStatus.REJECTED_BY_DRIVER, UserState.IDLE);
}

async function changeDriverTripStatus(tripId: string, driverId: string, tripState: TripStatus, driverState: UserState) {
  const transaction = (await updateTransaction()) as Knex.Transaction;

  try {
    //trip state changes to confirmed.
    const trip = await transaction('trips')
      .where('id', tripId)
      .update({ driverId: driverId, status: tripState });

    //driver status changes to busy/travelling.
    await transaction('users')
      .update({ state: driverState })
      .where('id', driverId);

    await transaction.commit();

    return getTripById(tripId);
  } catch (err) {
    transaction.rollback();
    console.error(`Assign driver to trip aborted! "${err}"`);
    return {};
  }
}

async function updateTripStatus(id: string, status: TripStatus) {
  await db
    .table('trips')
    .where('id', id)
    .update({ status: status });

  return await this.getTripById(id);
}

const assignTransaction = () => new Promise(resolve => db.transaction(resolve));

async function assignDriverToTrip(tripId: string, driverId: string) {
  const transaction = (await assignTransaction()) as Knex.Transaction;

  try {
    const bookedDriver = await transaction('users')
      .select('*')
      .where('id', driverId)
      .first();

    if (bookedDriver.state != UserState.IDLE) {
      throw new Error('Driver unavailable!');
    }

    //book driver (cannot be selected for other trips while handling this one).
    await transaction('users')
      .update({ state: UserState.WAITING_TRIP_CONFIRM })
      .where('id', driverId);

    //temporarily assign driver to trip
    let trip = await transaction('trips')
      .update({ driverId: driverId, status: TripStatus.DRIVER_CONFIRM_PENDING })
      .where('id', tripId);

    await transaction.commit();
    return trip as any;
  } catch (err) {
    transaction.rollback();
    console.error(`User creation transaction aborted!"${err}"`);
    return {};
  }
}

async function calculateTripData(routes: string, pets: string[], escort: boolean) {
  const driversAvailability = await getDriversAvailability();

  const route = JSON.parse(routes)[0];
  const price = await calculateTripCost(route.legs[0].distance.value, pets, route.legs[0].duration.value, escort);

  return {
    distance: route.legs[0].distance.text,
    duration: route.legs[0].duration.text,
    price: `$${price.toFixed(2)}`,
  };
}

async function getDriversAvailability(): Promise<number> {
  const driversWithTrips = (await db
    .table('trips')
    .whereNotNull('driverId')
    .andWhereNot('status', TripStatus.COMPLETED)
    .count('id'))[0].count;
  const activeDrivers = (await db
    .table('users')
    .where('userType', UserTypes.Driver)
    .andWhere('access', UserValidationStatus.USER_VALIDATED)
    .count('id'))[0].count;

  return activeDrivers / driversWithTrips;
}

async function calculateTripCost(distance: number, pets: string[], duration: number, escort: boolean) {
  //const getPetSizeExtraCost = (petSize: string) => (petSize === 'S' ? 0 : petSize === 'M' ? 20 : 40);

  //Costo por mascota: 50 pesos por cada una, sin importar tamaño
  const petSizeCost = pets.length * 50;

  //si hay acompañante son 100 pe mas
  var escortCost = 0;
  if (escort) {
    escortCost = 100;
  } else {
    escortCost = 0;
  }

  // Hay un costo por distancia: 25 pesos por km
  const distanceCost = (distance / 1000) * 25;

  //const getUtcTimeExtraCost = (utcHour: number) => (utcHour >= 0 && utcHour < 6 ? 50 : 0);

  //Si es horario nocturno 50% + sobre el total. Horario nocturno es de 6pm a 6 am.
  const utcHour = new Date().getUTCHours();
  var isNocturneTime = false;
  const getUtcTimeExtraCost = (utcHour: number) =>
    (utcHour >= 18 && utcHour <= 23) || (utcHour >= 0 && utcHour < 6) ? (isNocturneTime = true) : (isNocturneTime = false);

  // Por cada mascota, obtenemos un recargo (si es S no hay recargo, M y L tienen recargo)
  //const petsExtraCost = pets.reduce((acc, petSize) => (acc += getPetSizeExtraCost(petSize)), 0);

  //5 pesos por minuto
  const durationCost = (duration / 60) * 5;

  //Deprecado por Alejandro
  // Si hay menos del 50% de los conductores disponibles, se agrega un recargo
  //const driversExtraCost = activeDrivers > 0.5 ? 0 : 50;

  var totalCost = petSizeCost + escortCost + distanceCost + durationCost;

  if (isNocturneTime) {
    totalCost = totalCost + totalCost * 0.5;
  }

  return totalCost;
}

async function getRoute(origin: string, destination: string) {
  const routes = await googleMapsService.getDirections(origin, destination);
  return JSON.parse(routes)[0] as any;
}

async function getUserLastTrip(userId: string) {
  const result = await db
    .table('trips')
    .where('clientId', userId)
    .orWhere('driverId', userId)
    .orderBy('createdDate', 'desc')
    .select()
    .first();
  if (result === undefined) {
    return {};
  }
  return {
    ...result,
    pets: result.pets.split(','),
  };
}

async function getDriverPendingTrips(driverId: string) {
  try {
    const pendingTrip = await db
      .table('trips')
      .where('driverId', driverId)
      .andWhere('status', TripStatus.DRIVER_CONFIRM_PENDING)
      .first();
    if (pendingTrip === undefined) {
      return {};
    }
    return {
      ...pendingTrip,
      pets: pendingTrip.pets.split(','),
    };
  } catch (e) {
    console.log(e);
    return [];
  }
}

async function getDriverFinishedTrips(driverId: string) {
  try {
    const finishedTrips = await db
      .table('trips')
      .where('driverId', driverId)
      .andWhere('status', TripStatus.COMPLETED)
      .orderBy('createdDate', 'desc')
      .select();
    if (finishedTrips === undefined) {
      return [];
    }
    return finishedTrips;
  } catch (e) {
    console.log(e);
    return [];
  }
}

async function getPassengerFinishedTrips(passengerId: string) {
  try {
    const finishedTrips = await db
      .table('trips')
      .where('clientId', passengerId)
      .andWhere('status', TripStatus.COMPLETED)
      .orderBy('createdDate', 'desc')
      .select();
    if (finishedTrips === undefined) {
      return [];
    }
    return finishedTrips;
  } catch (e) {
    console.log(e);
    return [];
  }
}

export default {
  getTrips,
  getTripById,
  createTrip,
  updateTripStatus,
  assignDriverToTrip,
  getRoute,
  getUserLastTrip,
  driverHasTripWithStatus,
  driverAcceptTrip,
  driverRejectTrip,
  getDriverPendingTrips,
  getDriverFinishedTrips,
  getPassengerFinishedTrips,
};
