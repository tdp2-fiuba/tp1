package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public static final String PROFILE_IMAGE_NAME = "profileImage";
    private String id;
    private String userType;
    private String firstName;
    private String lastName;
    private String position;
    private List<UserImage> images;

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
}
