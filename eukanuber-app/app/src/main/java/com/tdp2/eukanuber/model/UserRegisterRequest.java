package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserRegisterRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String userType;
    private String position;
    private String fbId;
    private String fbAccessToken;
    private List<UserImage> images;

    public UserRegisterRequest() {
        this.images = new ArrayList<>();
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public List<UserImage> getImages() {
        return images;
    }

    public void setImages(List<UserImage> images) {
        this.images = images;
    }

    public void addImage(UserImage image){
        this.images.add(image);
    }
}

