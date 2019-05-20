import TripStatus from "./TripStatus";
import IUser from "./IUser";

export default interface ITrip {
  id: string;
  clientId: string;
  driverId?: string;
  pets: string[];
  origin: string;
  destination: string;
  originCoordinates: string;
  destinationCoordinates: string;
  routes: string;
  distance: string;
  duration: string;
  price: string;
  escort: boolean;
  status: TripStatus;
  payment: string;
  clientDetail: any;
  driverDetail: any;
  reviewToClient: any;
  reviewToDriver: any;
}
