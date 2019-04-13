enum TripStatus {
  PENDING = 0, // driver hasn't accepted yet
  ACCEPTED = 1, // driver accepted the trip ("En Camino")
  IN_TRAVEL = 2, // "En viaje"
  ARRIVED_DESTINATION = 3, // "Llegamos a destino"
  COMPLETED = 4 // "Viaje completado"
}

export default TripStatus;
