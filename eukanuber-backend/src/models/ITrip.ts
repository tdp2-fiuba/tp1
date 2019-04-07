export enum TripStatus {
  PENDING = 0, // driver hasn't accepted yet
  ACCEPTED = 1, // driver accepted the trip ("En Camino")
  IN_TRAVEL = 2,
  ARRIVED_DESTINATION = 3,
  COMPLETED = 4
}

export default interface ITrip {
  id: string;
  origin: string;
  destination: string;
  originCoordinates: string;
  destinationCoordinates: string;
  clientId: string;
  driverId: string;
  pets: string[];
  escort: boolean;
  status: TripStatus;
  payment: string;
  price: string;
}
