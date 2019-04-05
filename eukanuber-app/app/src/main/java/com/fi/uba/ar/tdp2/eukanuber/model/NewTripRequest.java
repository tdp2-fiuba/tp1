package com.fi.uba.ar.tdp2.eukanuber.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NewTripRequest {
    private String origin;
    private String destination;
    private Collection<String> pets;
    private Boolean escort;
    private String payment;

    public NewTripRequest() {
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
}
