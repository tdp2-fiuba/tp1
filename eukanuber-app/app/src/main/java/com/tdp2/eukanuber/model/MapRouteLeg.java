package com.tdp2.eukanuber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collection;

public class MapRouteLeg implements Serializable {
    private Collection<MapRouteStep> steps;
    private MapRouteTextValue distance;
    private MapRouteTextValue duration;
    @SerializedName("end_address")
    private String endAddress;
    @SerializedName("end_location")
    private MapRouteLocation endLocation;
    @SerializedName("start_address")
    private String startAddress;
    @SerializedName("start_location")
    private MapRouteLocation startLocation;

    public Collection<MapRouteStep> getSteps() {
        return steps;
    }

    public void setSteps(Collection<MapRouteStep> steps) {
        this.steps = steps;
    }

    public MapRouteTextValue getDistance() {
        return distance;
    }

    public void setDistance(MapRouteTextValue distance) {
        this.distance = distance;
    }

    public MapRouteTextValue getDuration() {
        return duration;
    }

    public void setDuration(MapRouteTextValue duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public MapRouteLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(MapRouteLocation endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public MapRouteLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(MapRouteLocation startLocation) {
        this.startLocation = startLocation;
    }
}
