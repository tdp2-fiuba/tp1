package com.tdp2.eukanuber.model;

public enum TripStatus {
    PENDING,                // Client hasn't accepted/cancelled yet
    CLIENT_CANCELLED,       // Client cancelled the trip
    CLIENT_ACCEPTED,        // Client hasn't accepted yet
    IN_TRAVEL,              // "En viaje"
    ARRIVED_DESTINATION,    // "Llegamos a destino"
    COMPLETED,              // "Viaje completado"
}