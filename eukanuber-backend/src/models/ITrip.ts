import TripStatus from "./TripStatus";

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
}
