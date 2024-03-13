package com.dcac.go4lunch.models.user;

public class RestaurantChoice {

    private String restaurantId;
    private String choiceDate;
    private String restaurantName;
    private String restaurantAddress;


    public RestaurantChoice(String restaurantId, String choiceDate, String restaurantName, String restaurantAddress) {
        this.restaurantId = restaurantId;
        this.choiceDate = choiceDate;
        this.restaurantName = restaurantName;
        this.restaurantAddress=restaurantAddress;
    }

    public RestaurantChoice() {
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getChoiceDate() {
        return choiceDate;
    }

    public void setChoiceDate(String choiceDate) {
        this.choiceDate = choiceDate;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress=restaurantAddress;
    }
}
