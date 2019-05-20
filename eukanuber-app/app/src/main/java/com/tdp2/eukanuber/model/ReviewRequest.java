package com.tdp2.eukanuber.model;

import java.io.Serializable;

public class ReviewRequest implements Serializable {
    private Integer stars;
    private String comment;

    public ReviewRequest(Integer stars, String comment) {
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

