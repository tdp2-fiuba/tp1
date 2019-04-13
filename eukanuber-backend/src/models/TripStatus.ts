export default enum TripStatus {
  PENDING = 0, // driver hasn't accepted yet
  ACCEPTED = 1, // driver accepted the trip ("En Camino")
  IN_TRAVEL = 2,
  ARRIVED_DESTINATION = 3,
  COMPLETED = 4
}
