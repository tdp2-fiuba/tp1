import Express from 'express';
import { ICreateTripData, ILocation, ITrip, TripStatus, IUser } from '../models';
import { tripsService, userService } from '../services';
import usersController from './usersController';
import UserState from '../models/UserState';

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
  try {
    const tripId = req.params.id;
    const trip: Partial<ITrip> = req.body;

    if (trip.status) {
      let updatedTrip = await tripsService.updateTripStatus(tripId, trip.status);
      if (trip.status == TripStatus.CLIENT_ACCEPTED) {
        let trip = await tripsService.getTripById(tripId);
        let drivers: Array<IUser> = await userService.getProspectiveDrivers(trip.originCoordinates);

        //TODO: separar en subgrupos y ordenarlos por nota
        if (!(drivers.length > 0)) {
          //trip remains in state CLIENT_ACCEPTED if
          //no drivers are found in the area.
          return res
            .status(200)
            .json(updatedTrip)
            .send();
        }

        //change trip status to DRIVER_CONFIRM_PENDING so client knows driver is being assigned...
        updatedTrip = await tripsService.updateTripStatus(tripId, TripStatus.DRIVER_CONFIRM_PENDING);
        res
          .status(200)
          .json(updatedTrip)
          .send();

        //trigger async algorithm to assign driver.
        return await assignDriverToTrip(trip, drivers);
      }
    }

    // Send a Bad request
    return res.sendStatus(400);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function assignDriverToTrip(trip: ITrip, drivers: Array<IUser>) {
  let driverPicked = false;
  try {
    while (!driverPicked && drivers.length > 0) {
      //assign driver to a trip:
      await tripsService.assignDriverToTrip(trip.id, drivers[0].id);

      let timeoutReached = false;
      let timeout = setTimeout(function() {
        timeoutReached = true;
        console.log('Driver timeout reached');
      }, 60000);

      let accepted = false;
      while (!accepted && !timeoutReached) {
        console.log('Wating on driver...');
        accepted = await tripsService.driverAcceptedTrip(trip.id);
      }

      if (!accepted) {
        //penalize driver if status is idle.
        //TODO:if unavailable, driver perhaps signed off or is out of workhours, check with client whether they should
        //be penalized in this case.
        //TODO: check if rejected by driver.
        //remove driver from prospective driver's list and continue with algorithm:
        await userService.updateUserState(drivers[0].id, UserState.IDLE);
        drivers.pop();
      } else {
        clearTimeout(timeout);
        console.log('Driver accepted trip!');
        return;
      }
    }
    //driver was not found so trip goes back to state CLIENT_ACCEPTED.
    return await tripsService.updateTripStatus(trip.id, TripStatus.CLIENT_ACCEPTED);
  } catch (e) {
    console.log('Error assigning driver to trip: ' + e);
    return {};
  }
}

async function getRoute(req: Express.Request, res: Express.Response) {
  const loc: ILocation = req.body;
  const origin: string = loc.origin;
  const destination: string = loc.destination;
  const route = await tripsService.getRoute(origin, destination);
  return res.json(route);
}

async function acceptTrip(req: Express.Request, res: Express.Response) {
  try {
    const tripId = req.params.id;
    const driverId = await usersController.getUserIdIfLoggedWithValidCredentials(req, res);
    await tripsService.driverAcceptTrip(tripId, driverId);
    res.status(200).send();
  } catch (e) {
    res.status(500).send();
  }
}

async function rejectTrip(req: Express.Request, res: Express.Response) {
  try {
    res.status(200).send();
  } catch (e) {
    res.status(500).send();
  }
}

export default {
  getAll,
  getById,
  createTrip,
  updateTrip,
  getRoute,
  acceptTrip,
  rejectTrip,
};
