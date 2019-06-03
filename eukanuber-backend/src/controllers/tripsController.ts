import Express from 'express';
import {ICreateTripData, ILocation, ITrip, TripStatus} from '../models';
import {tripsService, userService} from '../services';
import usersController from './usersController';
import UserState from '../models/UserState';

const MIN_REVIEW_COUNT = 0;
const DISTANCES = ['0', '1.24274', '2.48548'];

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
        // Borro imagenes que no son del perfil para que
        // cargue mas rapido pantalla de feedback
        trip.driverDetail.images = [trip.driverDetail.images[0]];
        trip.reviewToDriver = await tripsService.getUserTripReview(trip.id, trip.clientId);
        trip.reviewToClient = await tripsService.getUserTripReview(trip.id, trip.driverId);
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
        const idDrivers:any = [];
        if (trip.status) {
            let updatedTrip = await tripsService.updateTripStatus(tripId, trip.status);
            if (trip.status == TripStatus.CLIENT_ACCEPTED) {
                let trip = await tripsService.getTripById(tripId);
                let candidates = 0;
                let allDrivers: Array<Array<any>> = [];
                for (let j = 0; j < DISTANCES.length; j++) {
                    let d1 = DISTANCES[j];
                    let d2 = j < DISTANCES.length - 1 ? DISTANCES[j + 1] : '';
                    let driversDJ = await userService.getProspectiveDrivers(trip.originCoordinates, d1, d2);
                    driversDJ = driversDJ.filter(user => {
                        if (idDrivers.indexOf(user.id) === -1){
                           idDrivers.push(user.id);
                           return true;
                        };
                    });
                    allDrivers.push(driversDJ);
                    candidates += driversDJ.length;
                }

                if (candidates <= 0) {
                    updatedTrip = await tripsService.updateTripStatus(trip.id, TripStatus.TRIP_CANCELLED);
                }

                res
                    .status(200)
                    .json(updatedTrip)
                    .send();

                console.log('allDrivers ');
                console.log(allDrivers);
                for (let i = 0; i < allDrivers.length; i++) {
                    let drivers = allDrivers[i];
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
                }

                console.log(`TRIP CANCELLED '${trip.id}'`);
                //driver was not found so trip state changes to TRIP_CANCELLED.
                return await tripsService.updateTripStatus(trip.id, TripStatus.TRIP_CANCELLED);
            } else {
                updatedTrip = await tripsService.updateTripStatus(tripId, trip.status);
                if (updatedTrip.status === TripStatus.COMPLETED) {
                    await userService.updateUserState(updatedTrip.driverId, UserState.IDLE);
                    updatedTrip.clientDetail = await userService.getUserById(updatedTrip.clientId);
                    updatedTrip.driverDetail = await userService.getUserById(updatedTrip.driverId);
                    updatedTrip.driverDetail.images = [updatedTrip.driverDetail.images[0]];
                }
                if (updatedTrip.status === TripStatus.CANCELLED){
                    await userService.updateUserState(updatedTrip.driverId, UserState.IDLE);
                }
                res
                    .status(200)
                    .json(updatedTrip)
                    .send();
                return;
            }
        }
        console.log('No trip status received');
        return res
            .status(400)
            .json({message: 'No se recibió status de viaje!'})
            .send();
    } catch (e) {
        res
            .status(500)
            .json({message: e.message})
            .send();
    }
}

async function assignDriverToTrip(trip: ITrip, drivers: Array<any>) {
    try {
        let tripCancelled = false;
        while (!tripCancelled && drivers.length > 0) {
            console.log(drivers);
            const driver = drivers.shift();
            const driverId: string = driver.id;
            const clientId: string = trip.clientId;
            const clientData = await userService.getUserById(clientId);



            console.log(`New candidate driver: '${driverId}'`);
            try {
                await tripsService.assignDriverToTrip(trip.id, driverId);
                const userRating = await userService.getUserRating(clientId);
                const notificationData = {
                    title: 'Tienes un nuevo viaje disponible!',
                    type: 'new_trip',
                    driverName: clientData.firstName + ' ' + clientData.lastName,
                    driverScore: (parseFloat(userRating.sum) / parseFloat(userRating.count)).toFixed(2).toString(),
                    pets: trip.pets.length.toString(),
                    distance: trip.distance,
                    duration: trip.duration,
                    price: trip.price,
                    tripId: trip.id,
                };
                userService.notifyUser(driverId, notificationData);
            } catch (e) {
                console.log(`Failed assigning driver to trip: '${e}'`);
                continue;
            }
            let timeoutReached = false;
            let timeout = setTimeout(function () {
                timeoutReached = true;
                console.log('Driver timeout reached!');
            }, 30000);

            let accepted = false;
            let rejected = false;
            while (!accepted && !rejected && !timeoutReached && !tripCancelled) {
                console.log('Wating on driver...');
                accepted = await tripsService.hasTripWithStatus(trip.id, TripStatus.DRIVER_GOING_ORIGIN);
                rejected = await tripsService.hasTripWithStatus(trip.id, TripStatus.REJECTED_BY_DRIVER);
                tripCancelled = await tripsService.hasTripWithStatus(trip.id, TripStatus.CANCELLED);
            }

            clearTimeout(timeout);

            if (accepted && !tripCancelled) {
                return true;
            }

            if (timeoutReached && !tripCancelled) {
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
        console.log('Error accept trip: ' + e);
        res.status(500).send();
    }
}

async function rejectTrip(req: Express.Request, res: Express.Response) {
    try {
        const tripId = req.params.id;
        const userId = await usersController.getUserIdIfLoggedWithValidCredentials(req, res);
        const userRole = await userService.getUserRole(userId);
        if (userRole.userType == 'driver') {
            await tripsService.driverRejectTrip(tripId, userId);
        } else {
            await tripsService.cancelTrip(tripId, userId);
        }
        res.status(200).send();
    } catch (e) {
        console.log('Error reject/cancel trip: ' + e);
        res.status(500).send();
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
};
