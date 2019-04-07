import db from "../db/db";
import { ICreateTripData, ITrip, TripStatus } from "../models";
import googleMapsService from "./googleMapsService";

async function getTrips() {
  return await db.table("trips").select();
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
  const newTrip = {
    ...trip,
    pets: trip.pets.join(",").replace(/\s/g, ""),
    originCoordinates,
    destinationCoordinates,
    routes,
    // TODO: remove
    clientId: "dummyClientId"
  };

  const tripId = ((await db
    .table("trips")
    .returning("id")
    .insert(newTrip)) as string[])[0];

  return {
    ...newTrip,
    id: tripId,
    pets: trip.pets,
    // TODO: remove
    driverId: "dummyDriverId",
    status: TripStatus.PENDING,
    price: "100 USD"
  };
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

export default {
  getTrips,
  getTripById,
  createTrip,
  updateTrip
};
