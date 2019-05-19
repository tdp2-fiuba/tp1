package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Review implements Serializable {
    private Integer stars;
    private String comment;

    public Review(Integer stars, String comment) {
        this.stars = stars;
        this.comment = comment;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

