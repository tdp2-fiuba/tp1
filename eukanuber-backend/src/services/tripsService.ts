import Knex from "knex";
import db from "../db/db";
import { ICreateTripData, ITrip, TripStatus } from "../models";
import googleMapsService from "./googleMapsService";

async function getTrips(status: TripStatus) {
  return await db
    .table("trips")
    .modify((queryBuilder: Knex.QueryBuilder) => {
      if (status) {
        queryBuilder.where("status", status);
      }
    })
    .select();
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
  // Move to controller
  const originCoordinates = await googleMapsService.getGeocode(trip.origin);
  const destinationCoordinates = await googleMapsService.getGeocode(trip.destination);
  const routes = await googleMapsService.getDirections(originCoordinates, destinationCoordinates);
  const routeData = calculateRouteData(routes);

  const newTrip = {
    ...trip,
    pets: trip.pets.join(",").replace(/\s/g, ""),
    originCoordinates,
    destinationCoordinates,
    clientId: "dummyClientId", // TODO: remove
    status: TripStatus.PENDING,
    ...routeData,
    routes
  };

  const tripId = ((await db
    .table("trips")
    .returning("id")
    .insert(newTrip)) as string[])[0];

  return {
    ...newTrip,
    id: tripId,
    pets: trip.pets
  } as any;
}

async function updateTrip(trip: ITrip) {
  const updatedTrip = {
    ...trip,
    pets: trip.pets.join(",").replace(/\s/g, "")
  };

  await db
    .table("trips")
    .where("id", trip.id)
    .update(updatedTrip);

  return trip;
}

function calculateRouteData(routes: string) {
  const distanceMultiplier = 0.2;
  const route = JSON.parse(routes)[0];
  const price = route.legs[0].duration.value * distanceMultiplier;

  return {
    distance: route.legs[0].distance.text,
    duration: route.legs[0].duration.text,
    price: `$${price.toFixed(2)}`
  };
}

export default {
  getTrips,
  getTripById,
  createTrip,
  updateTrip
};
