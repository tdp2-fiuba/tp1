import db from "../db/db";

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

export default { getTrips, getTripById };
