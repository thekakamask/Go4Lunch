package com.dcac.go4lunch.models.user;

import java.util.List;

public class User {

    private String uid;
    private String userName;
    private String urlPicture;
    private String email;
    private List<String> restaurantsLike;
    private RestaurantChoice restaurantChoice;

    public User(String uid, String userName, String urlPicture, String email, List<String> restaurantsLike, RestaurantChoice restaurantChoice) {
        this.uid=uid;
        this.userName=userName;
        this.urlPicture=urlPicture;
        this.email=email;
        this.restaurantsLike = restaurantsLike;
        this.restaurantChoice= restaurantChoice;
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

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public List<String> getRestaurantsLike() {
        return restaurantsLike;
    }

    public RestaurantChoice getRestaurantChoice() {
        return restaurantChoice;
    }

}
