package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public static final String PROFILE_IMAGE_NAME = "profileImage";
    public static final String CAR_IMAGE_NAME = "carPicture";
    public static final String LICENSE_IMAGE_NAME = "licensePicture";
    public static final String INSURANCE_IMAGE_NAME = "insurancePicture";
    public static final String USER_TYPE_CLIENT = "Client";
    public static final String USER_TYPE_DRIVER = "Driver";

    private String id;
    private String userType;
    private String firstName;
    private String lastName;
    private String position;
    private List<UserImage> images;
    private UserCar car;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<UserImage> getImages() {
        return images;
    }

    public void setImages(List<UserImage> images) {
        this.images = images;
    }

    public UserCar getCar() {
        return car;
    }

    public void setCar(UserCar car) {
        this.car = car;
    }
}
