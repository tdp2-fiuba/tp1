import Express from "express";
import { ICreateTripData, TripStatus } from "../models";
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
  const newTripData: ICreateTripData = req.body;
  const newTrip = await tripsService.createTrip(newTripData);
  res.json(newTrip);
}

async function updateTrip(req: Express.Request, res: Express.Response) {
  const { body } = req; // This is object deconstruction, equivalent to "const body = req.body"

  const updatedTrip = await tripsService.updateTrip(body);
  res.json(updatedTrip);
}

async function confirmTrip(req: Express.Request, res: Express.Response) {
  // Expects { accepted : boolean }
  const { id } = req.params;
  const { confirmation } = req.body; 
  const trip = await tripsService.getTripById(id);
  //TODO: verify driver credentials
  if (trip && confirmation.accepted) {
    trip.status = TripStatus.IN_TRAVEL;
    //TODO: notify client
    return tripsService.updateTrip(trip);
  }
  //TODO: look for a new driver and assign trip.
}

export default {
  getAll,
  getById,
  createTrip,
  updateTrip,
  confirmTrip
};
