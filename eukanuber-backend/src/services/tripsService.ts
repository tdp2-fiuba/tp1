import db from "../db/db";
import { ITrip } from "../models";

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

async function createTrip(trip: ITrip) {
  // TODO: remove
  trip.clientId = "dummyClient";
  trip.driverId = "dummyDriver";
  trip.price = "5.99 USD";

  const newTrip = {
    ...trip,
    pets: trip.pets.join(",").replace(/\s/g, "")
  };

  trip.id = ((await db
    .table("trips")
    .returning("id")
    .insert(newTrip)) as string[])[0];

  return trip;
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
