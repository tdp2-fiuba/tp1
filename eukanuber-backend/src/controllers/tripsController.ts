import Express from "express";
import { ICreateTripData, ITrip, TripStatus } from "../models";
import { tripsService } from "../services";

async function getAll(req: Express.Request, res: Express.Response) {
  const status: TripStatus = req.query.status;
  const trips = await tripsService.getTrips(status);
  res.json(trips);
}

async function getById(req: Express.Request, res: Express.Response) {
  const { id } = req.params; // This is object deconstruction, equivalent to "const id = req.params.id;"
  const trip = await tripsService.getTripById(id);
  res.json(trip);
}

async function createTrip(req: Express.Request, res: Express.Response) {
  const newTripData: ICreateTripData = req.body;
  const newTrip = await tripsService.createTrip(newTripData);
  res.json(newTrip);
}

async function updateTrip(req: Express.Request, res: Express.Response) {
  const trip: ITrip = req.body;
  const updatedTrip = await tripsService.updateTrip(trip);
  res.json(updatedTrip);
}

export default {
  getAll,
  getById,
  createTrip,
  updateTrip
};
