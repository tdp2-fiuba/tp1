package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Trip implements Serializable {

    private String id;
    private String origin;
    private String destination;
    private List<String> pets;
    private Boolean escort;
    private String payment;
    private String duration;
    private String price;
    private String routes;
    private List<MapRoute> routesList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trip() {
        pets = new ArrayList<>();
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getPets() {
        return pets;
    }

    public void setPets(List<String> pets) {
        this.pets = pets;
    }

    public Boolean getEscort() {
        return escort;
    }

    public void setEscort(Boolean escort) {
        this.escort = escort;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public List<MapRoute> getRoutesList() {
        return routesList;
    }

    public void setRoutesList(List<MapRoute> routesList) {
        this.routesList = routesList;
    }
}
