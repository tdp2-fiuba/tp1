import Knex from "knex";
import db from "../db/db";
import { ICreateTripData, ITrip, TripStatus } from "../models";
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
    clientId: "dummyClientId", // TODO: remove
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
  const route = JSON.parse(routes)[0];
  const price = await computeTripCost(route.legs[0].distance.value, pets);

  return {
    distance: route.legs[0].distance.text,
    duration: route.legs[0].duration.text,
    price: `$${price.toFixed(2)}`
  };
}

async function computeTripCost(distance: number, pets: string[]) {
  const distanceMultiplier = 0.2;
  const defaultTimeSlotPrice = 20;
  const petSizeMultiplier: { [name: string]: number } = { S: 0.1, M: 0.3, L: 0.5 };

  const now = Date.now();

  const minutes = 1000 * 60;
  const hours = minutes * 60;

  let timeSlotCost: number = 1; /*await db
    .table('timeSlots')
    .select('price')
    .where('hourStart', '<=', Math.round(now / hours))
    .andWhere('hourEnd', '>', Math.round(now / hours))
    .orWhere('hourStart', '=', Math.round(now / hours))
    .andWhere('hourEnd', '=', Math.round(now / hours))
    .andWhere('minStart', '<=', Math.round(now / minutes))
    .andWhere('minEnd', '>=', Math.round(now / minutes));*/
  if (timeSlotCost == undefined) {
    timeSlotCost = defaultTimeSlotPrice;
  }

  const costPets: number = pets.map(petSize => petSizeMultiplier[petSize] * distance).reduce((p1, p2) => p1 + p2);

  return distance * distanceMultiplier; // + costPets + timeSlotCost;
}

async function getRoute(origin: string, destination: string) {
  const routes = await googleMapsService.getDirections(origin, destination);

  return JSON.parse(routes)[0] as any;
}

async function getTripByUserAndStatus(userId: string, tripSatus: number) {
  const trip = await db
    .table("trips")
    .where("clientId", userId)
    .orWhere("driverId", userId)
    .andWhere("status", tripSatus)
    .select()
    .first();

  if (!trip) {
    return undefined;
  }

  return trip.id;
}

export default {
  getTrips,
  getTripById,
  createTrip,
  updateTripStatus,
  assignDriverToTrip,
  getRoute,
  getTripByUserAndStatus
};
