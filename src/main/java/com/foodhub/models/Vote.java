package com.foodhub.models;

import com.foodhub.Global;

import java.util.Collection;

public class Vote {

    private String userName;
    private int restaurantId, foodId = -1;
    private int rating;

    public Vote(String userName, int restaurantId, int foodId, int rating) {
        this.userName = userName;
        this.restaurantId = restaurantId;
        this.foodId = foodId;
        this.rating = rating;
    }

    public Vote(Query.Row row) {

        userName = row.getString(Global.FoodHub.VOTES.USER_NAME);
        restaurantId = row.getInt(Global.FoodHub.VOTES.RESTAURANT_ID);
        Object foodId = row.getObject(Global.FoodHub.VOTES.FOOD_ID);
        if(foodId != null) this.foodId = (int) foodId;
        rating = row.getInt(Global.FoodHub.VOTES.RATING);

    }

    public int rating() {
        return rating;
    }
}
