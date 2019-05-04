package com.tdp2.eukanuber.model;

public class LoginResponse {
    private User user;
    private String token;

    public LoginResponse() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
