package com.example.gamecarddetectionapp;

public class Upload {


    public Upload(String imageName, String imageUri, String username) {
        this.imageName = imageName;
        this.imageUri = imageUri;
        this.username = username;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    private String imageName;
    private String imageUri;
    private String username;

    public Upload() {
    }

    public String getUsername() {
        return username;
    }

}
