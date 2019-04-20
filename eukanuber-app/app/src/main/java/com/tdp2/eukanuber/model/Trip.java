package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {

    private String id;
    private String origin;
    private String destination;
    private String originCoordinates;
    private String destinationCoordinates;
    private List<String> pets;
    private Boolean escort;
    private String payment;
    private String duration;
    private String distance;
    private String price;
    private String driverId;
    private Integer status;
    private List<MapRoute> routes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trip() {
        //pets = new ArrayList<>();
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public List<MapRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<MapRoute> routes) {
        this.routes = routes;
    }

    public List<String> getPets() {
        return pets;
    }

    public void setPets(List<String> pets) {
        this.pets = pets;
    }

    public String getOriginCoordinates() {
        return originCoordinates;
    }

    public void setOriginCoordinates(String originCoordinates) {
        this.originCoordinates = originCoordinates;
    }

    public String getDestinationCoordinates() {
        return destinationCoordinates;
    }

    public void setDestinationCoordinates(String destinationCoordinates) {
        this.destinationCoordinates = destinationCoordinates;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
