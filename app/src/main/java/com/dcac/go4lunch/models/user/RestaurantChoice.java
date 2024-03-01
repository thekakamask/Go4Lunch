package com.dcac.go4lunch.models.user;

public class RestaurantChoice {

    private String restaurantId;
    private String choiceDate;
    private String restaurantName;


    public RestaurantChoice(String restaurantId, String choiceDate, String restaurantName) {
        this.restaurantId = restaurantId;
        this.choiceDate = choiceDate;
        this.restaurantName = restaurantName;
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
}
