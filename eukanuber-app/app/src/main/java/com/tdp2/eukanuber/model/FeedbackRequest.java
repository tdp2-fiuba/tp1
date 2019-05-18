package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FeedbackRequest implements Serializable {
    private String userId;
    private String tripId;
    private Review review;

    public FeedbackRequest(String userId, String tripId, Review review) {
        this.userId = userId;
        this.tripId = tripId;
        this.review = review;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}

