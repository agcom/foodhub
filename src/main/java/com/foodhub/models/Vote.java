package com.foodhub.models;

import com.foodhub.Global;

public class Vote {
	
	private final String userName;
	private final int restaurantId;
	private int foodId = -1;
	private final int rating;
	
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
		if (foodId != null) this.foodId = (int) foodId;
		rating = row.getInt(Global.FoodHub.VOTES.RATING);
		
	}
	
	public int rating() {
		return rating;
	}
}
