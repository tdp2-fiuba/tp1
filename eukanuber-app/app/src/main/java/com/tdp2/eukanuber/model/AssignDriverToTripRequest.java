package com.tdp2.eukanuber.model;

public class AssignDriverToTripRequest {
    private String driverId;

    public AssignDriverToTripRequest(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverId() {
        return this.driverId;
    }
}
