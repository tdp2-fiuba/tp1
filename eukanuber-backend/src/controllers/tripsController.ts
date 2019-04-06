import Express from "express";
import { tripsService } from "../services";

async function getAll(req: Express.Request, res: Express.Response) {
  const trips = await tripsService.getTrips();
  res.json(trips);
}

async function getById(req: Express.Request, res: Express.Response) {
  const { id } = req.params; // This is object deconstruction, equivalent to "const id = req.params.id;"
  const trip = await tripsService.getTripById(id);
  res.json(trip);
}

async function createTrip(req: Express.Request, res: Express.Response) {
  const { body } = req; // This is object deconstruction, equivalent to "const body = req.body"
  const newTrip = await tripsService.createTrip(body);
  res.json(newTrip);
}

async function updateTrip(req: Express.Request, res: Express.Response) {
  const { body } = req; // This is object deconstruction, equivalent to "const body = req.body"

  const updatedTrip = await tripsService.createTrip(body);
  res.json(updatedTrip);
}

export default {
  getAll,
  getById,
  createTrip,
  updateTrip
};
