import Express from "express";
import { ICreateTripData, ITrip, TripStatus } from "../models";
import { tripsService } from "../services";

async function getAll(req: Express.Request, res: Express.Response) {
  const status: TripStatus = req.query.status;
  const trips = await tripsService.getTrips(status);
  res.json(trips);
}

async function getById(req: Express.Request, res: Express.Response) {
  const tripId = req.params.id;
  const trip = await tripsService.getTripById(tripId);
  res.json(trip);
}

async function createTrip(req: Express.Request, res: Express.Response) {
  const newTripData: ICreateTripData = req.body;
  const newTrip = await tripsService.createTrip(newTripData);
  res.json(newTrip);
}

async function updateTrip(req: Express.Request, res: Express.Response) {
  const tripId = req.params.id;
  const trip: Partial<ITrip> = req.body;

  if (trip.driverId) {
    const updatedTrip = await tripsService.assignDriverToTrip(tripId, trip.driverId);
    return res.json(updatedTrip);
  }

  if (trip.status) {
    const updatedTrip = await tripsService.updateTripStatus(tripId, trip.status);
    return res.json(updatedTrip);
  }

  // Send a Bad request
  res.sendStatus(400);
}

export default {
  getAll,
  getById,
  createTrip,
  updateTrip
};
