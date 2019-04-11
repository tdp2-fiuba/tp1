package com.tdp2.eukanuber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MapRouteStep implements Serializable {
    private MapRouteTextValue distance;
    private MapRouteTextValue duration;
    private MapRoutePolyline polyline;
    @SerializedName("travel_mode")
    private String travelMode;
    @SerializedName("end_location")
    private MapRouteLocation endLocation;
    @SerializedName("start_location")
    private MapRouteLocation startLocation;

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

    public MapRoutePolyline getPolyline() {
        return polyline;
    }

    public void setPolyline(MapRoutePolyline polyline) {
        this.polyline = polyline;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public MapRouteLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(MapRouteLocation endLocation) {
        this.endLocation = endLocation;
    }

    public MapRouteLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(MapRouteLocation startLocation) {
        this.startLocation = startLocation;
    }
}
