import db from "../db/db";
import { ITrip } from "../models";

async function getTrips() {
  return await db.table("trips").select();
}

async function getTripById(id: string) {
  return await db
    .table("trips")
    .where("id", id)
    .select()
    .first();
}

async function createTrip(trip: ITrip) {
  return Promise.resolve(trip);
}

export default { getTrips, getTripById, createTrip };
