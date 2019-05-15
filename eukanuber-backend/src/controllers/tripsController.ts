import Express from 'express';
import { ICreateTripData, ILocation, ITrip, TripStatus, IUser } from '../models';
import { tripsService, userService } from '../services';

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
        // trigger async algorithm to assign driver.
        updatedTrip = await assignDriverToTrip(tripId);
      }

      return res
        .status(200)
        .json(updatedTrip)
        .send();
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

async function assignDriverToTrip(tripId: string) {
  let driverPicked = false;
  try {
    let trip = await tripsService.getTripById(tripId);
    let drivers: Array<IUser> = await userService.getProspectiveDrivers(trip.origin);

    //TODO: separar en subgrupos y ordenarlos por nota

    //assign driver to a trip:
    await tripsService.assignDriverToTrip(trip.id, drivers[0].id);

    while (!driverPicked && drivers.length > 0) {
      let timeout = new Promise(function(resolve, reject) {
        //timeout for driver to confirm/reject trip.
        setTimeout(resolve, 60, 'Driver timeout reached!');
      });

      let tripAccepted = new Promise(async function(resolve, reject) {
        //wait for driver to confirm or reject trip.
        let accepted = false;
        while (!accepted) {
          console.log('Wating on driver...');
          accepted = await tripsService.driverAcceptedTrip(trip.id);
        }
        console.log('Driver successfully assigned!');
      });

      Promise.race([tripAccepted, timeout]).then(function(assigned) {
        driverPicked = assigned as boolean;
        if (!assigned) {
          //penalize driver if status is idle.
          //TODO:if unavailable, driver perhaps signed off or is out of workhours, check with client whether they should
          //be penalized in this case.
          //remove driver from prospective driver's list and continue with algorithm:
          drivers.pop();
        } else {
          //permanently assign driver to trip
          return true; //return trip
        }
      });
    }
    return false; //return {}
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

export default {
  getAll,
  getById,
  createTrip,
  updateTrip,
  getRoute,
};
