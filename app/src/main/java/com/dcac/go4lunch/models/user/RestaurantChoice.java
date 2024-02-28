package com.dcac.go4lunch.models.user;

public class RestaurantChoice {

    private String restaurantId;
    private String choiceDate;


    public RestaurantChoice(String restaurantId, String choiceDate) {
        this.restaurantId = restaurantId;
        this.choiceDate = choiceDate;
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
}
