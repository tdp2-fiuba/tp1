import Knex from "knex";
import db from "../db/db";
import { ICreateTripData, ITrip, TripStatus } from "../models";
import googleMapsService from "./googleMapsService";

async function getTrips(status: TripStatus) {
  const trips = await db
    .table("trips")
    .modify((queryBuilder: Knex.QueryBuilder) => {
      if (status) {
        queryBuilder.where("status", status);
      }
    })
    .select();
    return trips.map((o:any) => ({ ...o, pets: o.pets.split(",") }));
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

  return await db
    .table("trips")
    .where("id", id)
    .select();
}

async function assignDriverToTrip(id: string, driverId: string) {
  await db
    .table("trips")
    .where("id", id)
    .update({ driverId, status: TripStatus.IN_TRAVEL });

  return await db
    .table("trips")
    .where("id", id)
    .select();
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

async function getRoute(origin: string, destination: string) {
  const originCoordinates = await googleMapsService.getGeocode(origin);
  const destinationCoordinates = await googleMapsService.getGeocode(destination);
  try {
    const routes = await googleMapsService.getDirections(originCoordinates, destinationCoordinates);
    return routes as any;
  } catch (e) {
    return {};
  }
}

export default {
  getTrips,
  getTripById,
  createTrip,
  updateTripStatus,
  assignDriverToTrip,
  getRoute
};
