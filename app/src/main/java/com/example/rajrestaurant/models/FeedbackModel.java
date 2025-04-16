package com.example.rajrestaurant.models;

public class FeedbackModel {
    private String comments;
    private String date;
    private float rating;
    private String time;
    private String userEmail;
    private String userID;

    public FeedbackModel() {
    }

    public FeedbackModel(String comments, String date, float rating, String time, String userEmail, String userID) {
        this.comments = comments;
        this.date = date;
        this.rating = rating;
        this.time = time;
        this.userEmail = userEmail;
        this.userID = userID;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
