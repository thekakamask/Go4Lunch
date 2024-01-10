package com.dcac.go4lunch.models;

public class User {

    private String uid;
    private String userName;
    private String urlPicture;
    private String email;

    public User(String uid, String userName, String urlPicture, String email) {
        this.uid=uid;
        this.userName=userName;
        this.urlPicture=urlPicture;
        this.email=email;
    }

    public User() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
