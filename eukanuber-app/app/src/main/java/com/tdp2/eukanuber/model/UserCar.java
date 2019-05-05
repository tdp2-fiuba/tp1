package com.tdp2.eukanuber.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserCar implements Serializable {
    private String plateNumber;
    private String brand;
    private String model;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
