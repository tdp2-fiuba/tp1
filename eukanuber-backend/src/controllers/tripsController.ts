import Express from 'express';
import { ICreateTripData, ILocation, ITrip, IUser, TripStatus } from '../models';
import { tripsService, userService } from '../services';
import usersController from './usersController';
import UserState from '../models/UserState';

const MIN_REVIEW_COUNT = 10;

async function getAll(req: Express.Request, res: Express.Response) {
  const status: TripStatus = req.query.status;
  const trips = await tripsService.getTrips(status);
  res.json(trips);
}
async function getFullById(req: Express.Request, res: Express.Response) {
  const tripId = req.params.id;
  const trip = await tripsService.getTripById(tripId);
  if (trip.status === TripStatus.COMPLETED) {
    trip.clientDetail = await userService.getUserById(trip.clientId);
    trip.driverDetail = await userService.getUserById(trip.driverId);
  }
  res.json(trip);
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
        let driversD1: Array<any> = await userService.getProspectiveDrivers(trip.originCoordinates, '0', '1.24274');
        let driversD2: Array<any> = await userService.getProspectiveDrivers(trip.originCoordinates, '1.24274', '2.48548');
        let driversD3: Array<any> = await userService.getProspectiveDrivers(trip.originCoordinates, '2.48548', '');
        let allDrivers = [driversD1, driversD2, driversD3];
        allDrivers.forEach(async function(drivers) {
          if (drivers.length <= 0) {
            updatedTrip = await tripsService.updateTripStatus(trip.id, TripStatus.TRIP_CANCELLED);
          }

          res
            .status(200)
            .json(updatedTrip)
            .send();

          let groupA = drivers.filter(driver => driver.count < MIN_REVIEW_COUNT); //group A consists of drivers with lesser amount of reviews.
          let groupB = drivers.filter(driver => driver.count >= MIN_REVIEW_COUNT); //group B consists of drivers with more reviews.

          //trigger algorithm to assign driver.
          let assigned = await assignDriverToTrip(trip, groupA);
          if (assigned) {
            return;
          }

          //If driver not assigned in group A, continue with group B...
          assigned = await assignDriverToTrip(trip, groupB);
          if (assigned) {
            return;
          }
        });

        console.log(`TRIP CANCELLED '${trip.id}'`);
        //driver was not found so trip state changes to TRIP_CANCELLED.
        return await tripsService.updateTripStatus(trip.id, TripStatus.TRIP_CANCELLED);
      } else {
        /* Lo agrego para el cambio de estado del viaje por el chofer
                (En viaje, Terminado)*/
        updatedTrip = await tripsService.updateTripStatus(tripId, trip.status);
        if (updatedTrip.status === TripStatus.COMPLETED) {
          await userService.updateUserState(updatedTrip.driverId, UserState.IDLE);
        }
        return res
          .status(200)
          .json(updatedTrip)
          .send();
      }
    }
    console.log('No trip status received');
    return res
      .status(400)
      .json({ message: 'No se recibió status de viaje!' })
      .send();
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

async function assignDriverToTrip(trip: ITrip, drivers: Array<any>) {
  try {
    while (drivers.length > 0) {
      const driver = drivers.shift();
      const driverId: string = driver.id;
      console.log(`New candidate driver: '${driverId}'`);
      try {
        //assign driver to a trip:
        await tripsService.assignDriverToTrip(trip.id, driverId);
      } catch (e) {
        continue;
      }
      let timeoutReached = false;
      let timeout = setTimeout(function() {
        timeoutReached = true;
        console.log('Driver timeout reached!');
      }, 20000);

      let accepted = false;
      let rejected = false;
      while (!accepted && !rejected && !timeoutReached) {
        console.log('Wating on driver...');
        accepted = await tripsService.driverHasTripWithStatus(trip.id, TripStatus.DRIVER_GOING_ORIGIN);
        rejected = await tripsService.driverHasTripWithStatus(trip.id, TripStatus.REJECTED_BY_DRIVER);
      }

      clearTimeout(timeout);

      if (accepted) {
        console.log('Driver accepted trip!');
        return true;
      }

      if (timeoutReached) {
        //penalize driver for timeout.
        await userService.penalizeDriverIgnoredTrip(driverId, trip.id);
      }

      //remove driver from prospective driver's list and continue with algorithm:
      await userService.updateUserState(driverId, UserState.IDLE);
    }

    return false;
  } catch (e) {
    console.log('Error assigning driver to trip: ' + e);
    return false;
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
    const trip = await tripsService.driverAcceptTrip(tripId, driverId);
    return res.json(trip);
  } catch (e) {
    res.status(500).send();
  }
}

async function rejectTrip(req: Express.Request, res: Express.Response) {
  try {
    const tripId = req.params.id;
    const driverId = await usersController.getUserIdIfLoggedWithValidCredentials(req, res);
    await tripsService.driverRejectTrip(tripId, driverId);
    res.status(200).send();
  } catch (e) {
    res.status(500).send();
  }
}

async function getFinishedTripsByUserId(req: Express.Request, res: Express.Response) {
  try {
    const userId = await usersController.getUserIdIfLoggedWithValidCredentials(req, res);
    const userIsDriver = await usersController.userIsDriver(userId);
    var tripList = [];
    if (userIsDriver) {
      tripList = await tripsService.getDriverFinishedTrips(userId);
    } else {
      tripList = await tripsService.getPassengerFinishedTrips(userId);
    }

    res.status(200).json(tripList);
  } catch (e) {
    res
      .status(500)
      .json({ message: e.message })
      .send();
  }
}

export default {
  getAll,
  getById,
  getFullById,
  createTrip,
  updateTrip,
  getRoute,
  acceptTrip,
  rejectTrip,
  getFinishedTripsByUserId,
};
