package com.foodo.models;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Restaurant {

    private String name;
    private Address address;
    private List<Food> foods;
    private Image logo;
    private Score rating, priceRange;
    private long minDeliveryTime, maxDeliveryTime;

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