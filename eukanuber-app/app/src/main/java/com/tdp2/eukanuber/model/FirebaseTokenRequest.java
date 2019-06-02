package com.tdp2.eukanuber.model;

import java.io.Serializable;

public class FirebaseTokenRequest implements Serializable {
    private String token;

    public FirebaseTokenRequest(String token) {
        this.token = token;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

