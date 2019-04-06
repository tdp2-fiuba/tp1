import Express from "express";
import { tripsService } from "../services";

async function getAll(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  const trips = await tripsService.getTrips();
  res.json(trips);
  next();
}

async function getById(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  const { id } = req.params; // This is object reconstruction, similar to "const id = req.params.id;"
  const trip = await tripsService.getTripById(id);
  res.json(trip);
  next();
}

async function createTrip(req: Express.Request, res: Express.Response, next: Express.NextFunction) {
  const { body } = req; // This is object reconstruction, similar to "const body = req.body"

  console.log("received", body);
  const newTrip = await tripsService.createTrip(body);
  res.json(newTrip);
  next();
}

export default {
  getAll,
  getById,
  createTrip
};
