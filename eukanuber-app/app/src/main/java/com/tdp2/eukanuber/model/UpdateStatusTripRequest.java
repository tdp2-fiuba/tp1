package com.tdp2.eukanuber.model;

public class UpdateStatusTripRequest {
    private Integer status;

    public UpdateStatusTripRequest(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}
