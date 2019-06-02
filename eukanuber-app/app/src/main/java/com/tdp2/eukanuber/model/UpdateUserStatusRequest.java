package com.tdp2.eukanuber.model;

public class UpdateUserStatusRequest {
    private String state;

    public UpdateUserStatusRequest(String state) {
        this.state = state;

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
