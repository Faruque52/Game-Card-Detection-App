package com.example.gamecarddetectionapp;

public class RatingDetails {


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    String userId;
    float rating;

    public RatingDetails(String userId, float rating) {
        this.userId = userId;
        this.rating = rating;
    }

}
