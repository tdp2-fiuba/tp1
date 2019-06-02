enum UserState {
    IDLE, //User is available and not currently in a trip.
    UNAVAILABLE, //User currently unavailable.
    WAITING_TRIP_CONFIRM, // User waiting for trip confirmation.
    TRAVELLING, //User is currently engaged in a trip
}

export default UserState;
