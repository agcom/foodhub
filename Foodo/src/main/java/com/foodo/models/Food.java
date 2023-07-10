package com.foodo.models;

import java.io.Serializable;

public class Food implements Serializable {
	
	private String name, type, imageUrl;
	private int price, cookTimeMin;
	
	public Food(String name, String type, int price, int cookTimeMin, String imageUrl) {
		this.name = name;
		this.type = type;
		this.imageUrl = imageUrl;
		this.price = price;
		this.cookTimeMin = cookTimeMin;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getCookTimeMin() {
		return cookTimeMin;
	}
	
	public void setCookTimeMin(int cookTimeMin) {
		this.cookTimeMin = cookTimeMin;
	}
}
