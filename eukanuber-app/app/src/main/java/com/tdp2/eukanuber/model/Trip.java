package com.tdp2.eukanuber.model;

import java.util.ArrayList;
import java.util.Collection;

public class Trip {

    private String id;
    private String origin;
    private String destination;
    private Collection<String> pets;
    private Boolean escort;
    private String payment;
    private String duration;
    private String price;

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

    public Collection<String> getPets() {
        return pets;
    }

    public void setPets(Collection<String> pets) {
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
}
