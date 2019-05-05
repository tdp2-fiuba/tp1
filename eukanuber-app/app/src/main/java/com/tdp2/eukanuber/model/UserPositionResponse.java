package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.List;

public class UserPositionResponse implements Serializable {
    private String position;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
