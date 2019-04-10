package com.tdp2.eukanuber.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.List;

public class MapRoute {
    private List<MapRouteLeg> legs;
    @SerializedName("overview_polyline")
    private MapRoutePolyline overviewPolyline;


    public List<MapRouteLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<MapRouteLeg> legs) {
        this.legs = legs;
    }

    public MapRoutePolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(MapRoutePolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }
}

