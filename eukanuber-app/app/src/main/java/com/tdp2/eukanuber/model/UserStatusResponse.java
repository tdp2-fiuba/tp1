package com.tdp2.eukanuber.model;

import java.io.Serializable;

public class UserStatusResponse implements Serializable {
    private Integer state;

    public UserStatusResponse(Integer state) {
        this.state = state;

    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

