package com.foodo.models;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Restaurant {
	
	private final String name;
	private final Address address;
	private final List<Food> foods;
	private final Image logo;
	private final Score rating;
	private final Score priceRange;
	private final long minDeliveryTime;
	private final long maxDeliveryTime;
	
	public Restaurant(String name, Address address, Collection<Food> foods, Image logo, Score rating, Score priceRange, long minDeliveryTime, long maxDeliveryTime) {
		this.name = name;
		this.address = address;
		this.foods = new ArrayList<>(foods);
		this.logo = logo;
		this.rating = rating;
		this.priceRange = priceRange;
		this.minDeliveryTime = minDeliveryTime;
		this.maxDeliveryTime = maxDeliveryTime;
	}
	
}