package com.example.gamecarddetectionapp;

public class ModelClass {

    String username;
    String userimage;
    String comment;

    public ModelClass() {

    }

    String date;
    String time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public ModelClass(String username, String userimage, String comment, String date, String time) {
        this.username = username;
        this.userimage = userimage;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }


}
