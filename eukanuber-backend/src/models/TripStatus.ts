enum TripStatus {
  PENDING, // Client hasn't accepted/cancelled yet
  CANCELLED, // Client cancelled the trip
  CLIENT_ACCEPTED, // Client hasn't accepted yet
  DRIVER_CONFIRM_PENDING, //Waiting for driver to confirm he accepts trip.
  DRIVER_GOING_ORIGIN, // "En camino al origen"
  IN_TRAVEL, // "En viaje"
  ARRIVED_DESTINATION, // "Llegamos a destino"
  COMPLETED, // "Viaje completado"
  REJECTED_BY_DRIVER, //Internal state, used to mark trip when rejected by prospective driver
  TRIP_CANCELLED, //Used to notify client that trip was cancelled because no drivers are available
}

export default TripStatus;
