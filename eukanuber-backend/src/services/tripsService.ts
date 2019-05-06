import Knex from "knex";
import db from "../db/db";
import { ICreateTripData, ITrip, TripStatus, UserStatus, UserTypes } from "../models";
import googleMapsService from "./googleMapsService";

async function getTrips(status: TripStatus) {
  const trips = await db
    .table("trips")
    .orderBy("createdDate", "desc")
    .modify((queryBuilder: Knex.QueryBuilder) => {
      if (status) {
        queryBuilder.where("status", status);
      }
    })
    .select();

  return trips.map((trip: any) => ({ ...trip, pets: trip.pets.split(",") }));
}

async function getTripById(id: string): Promise<ITrip> {
  const result = await db
    .table("trips")
    .where("id", id)
    .select()
    .first();

  if (!result) {
    return undefined;
  }

  return {
    ...result,
    pets: result.pets.split(",")
  };
}

async function createTrip(trip: ICreateTripData): Promise<ITrip> {
  // TODO: Move to controller
  const originCoordinates = await googleMapsService.getGeocode(trip.origin);
  const destinationCoordinates = await googleMapsService.getGeocode(trip.destination);
  const routes = await googleMapsService.getDirections(originCoordinates, destinationCoordinates);
  const routeData = await calculateTripData(routes, trip.pets);

  const newTrip = {
    ...trip,
    pets: trip.pets.join(",").replace(/\s/g, ""),
    originCoordinates,
    destinationCoordinates,
    clientId: trip.clientId,
    status: TripStatus.PENDING,
    createdDate: new Date().toISOString(),
    ...routeData,
    routes
  };

  const tripCreated = await db
    .insert(newTrip)
    .into("trips")
    .returning("*");

  return {
    ...tripCreated[0],
    pets: trip.pets
  };
}

async function updateTripStatus(id: string, status: TripStatus) {
  await db
    .table("trips")
    .where("id", id)
    .update({ status });

  return await this.getTripById(id);
}

async function assignDriverToTrip(id: string, driverId: string) {
  const tripToUpdate = await getTripById(id);

  if (tripToUpdate.driverId && tripToUpdate.driverId !== driverId) {
    throw new Error(`The trip '${id}' is already assigned to driver '${tripToUpdate.driverId}'`);
  }

  await db
    .table("trips")
    .where("id", id)
    .update({ driverId, status: TripStatus.DRIVER_GOING_ORIGIN });

  return await getTripById(id);
}

async function calculateTripData(routes: string, pets: string[]) {
  const driversAvailability = await getDriversAvailability();

  const route = JSON.parse(routes)[0];
  const price = await calculateTripCost(route.legs[0].distance.value, pets, driversAvailability);

  return {
    distance: route.legs[0].distance.text,
    duration: route.legs[0].duration.text,
    price: `$${price.toFixed(2)}`
  };
}

async function getDriversAvailability(): Promise<number> {
  const driversWithTrips = (await db
    .table("trips")
    .whereNotNull("driverId")
    .andWhereNot("status", TripStatus.COMPLETED)
    .count("id"))[0].count;
  const activeDrivers = (await db
    .table("users")
    .where("userType", UserTypes.Driver)
    .andWhere("state", UserStatus.USER_VALIDATED)
    .count("id"))[0].count;

  return activeDrivers / driversWithTrips;
}

async function calculateTripCost(distance: number, pets: string[], activeDrivers: number) {
  const getPetSizeExtraCost = (petSize: string) => (petSize === "S" ? 0 : petSize === "M" ? 20 : 40);
  const getUtcTimeExtraCost = (utcHour: number) => (utcHour >= 0 && utcHour < 6 ? 50 : 0);

  // Por cada mascota, obtenemos un recargo (si es S no hay recargo, M y L tienen recargo)
  const petsExtraCost = pets.reduce((acc, petSize) => (acc += getPetSizeExtraCost(petSize)), 0);

  // Dependiendo la hora, obtenemos un recargo (desde las 0 horas hasta las 6 hay un recargo)
  const timeExtraCost = getUtcTimeExtraCost(new Date().getUTCHours());

  // Si hay menos del 50% de los conductores disponibles, se agrega un recargo
  const driversExtraCost = activeDrivers > 0.5 ? 0 : 50;

  // Hay un costo por distancia (a mayor distancia, más caro)
  const distanceCost = distance * 0.1;

  return petsExtraCost + timeExtraCost + driversExtraCost + distanceCost;
}

async function getRoute(origin: string, destination: string) {
  const routes = await googleMapsService.getDirections(origin, destination);
  return JSON.parse(routes)[0] as any;
}

async function getUserLastTrip(userId: string) {
    const result = await db
    .table("trips")
    .where("clientId", userId)
    .orWhere("driverId", userId)
    .orderBy("createdDate", "desc")
    .select()
    .first();

    return {
        ...result,
        pets: result.pets.split(",")
    };
}

export default {
  getTrips,
  getTripById,
  createTrip,
  updateTripStatus,
  assignDriverToTrip,
  getRoute,
  getUserLastTrip
};
