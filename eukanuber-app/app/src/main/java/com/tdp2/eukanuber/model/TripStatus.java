package com.tdp2.eukanuber.model;

public enum TripStatus {
    PENDING,                // Driver hasn't accepted yet
    ACCEPTED,               // Driver accepted the trip ("En Camino")
    IN_TRAVEL,              // "En viaje"
    ARRIVED_DESTINATION,    // "Llegamos a destino"
    COMPLETED,              // "Viaje completado"
}