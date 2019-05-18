package com.tdp2.eukanuber.model;

public class RefuseDriverTripRequest {
    private Integer seconds;

    public RefuseDriverTripRequest(Integer seconds) {
        this.seconds = seconds;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }
}
