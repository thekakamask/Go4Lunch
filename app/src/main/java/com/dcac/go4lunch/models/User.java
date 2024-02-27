package com.dcac.go4lunch.models;

import java.util.List;

public class User {

    private String uid;
    private String userName;
    private String urlPicture;
    private String email;
    private List<String> restaurantsLike;

    public User(String uid, String userName, String urlPicture, String email, List<String> restaurantsLike) {
        this.uid=uid;
        this.userName=userName;
        this.urlPicture=urlPicture;
        this.email=email;
        this.restaurantsLike = restaurantsLike;
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

    public List<String> getRestaurantsLike() {
        return restaurantsLike;
    }

    public void setRestaurantsLike(List<String> restaurantsLike) {
        this.restaurantsLike = restaurantsLike;
    }
}
