package com.foodhub.models;

import com.foodhub.Global;
import com.foodhub.utils.Graphical;
import com.foodhub.views.FoodCard;
import com.foodhub.views.RestaurantCard;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.util.Duration;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Restaurant implements Graphical {
	
	private final int id; //unique
	private final String name;
	private final Address address;
	private final ArrayList<Food> foods = new ArrayList<>();
	private URL logo, image;
	private final Rating rating;
	private final Duration deliveryTime;
	private final Range<Time>[] openHours; //TODO: make a class model for open hours for ease of use with database
	
	public Restaurant(int id, String name, Address address, Rating rating,
	                  Duration deliveryTime, URL logo, URL image, Range<Time>[] openHours) {
		
		this.name = name;
		this.address = address;
		this.rating = rating;
		this.deliveryTime = deliveryTime;
		this.logo = logo;
		this.openHours = openHours;
		this.image = image;
		this.id = id;
		
	}
	
	public Restaurant(Query.Row row) {
		
		id = row.getInt(Global.FoodHub.RESTAURANTS.ID);
		name = row.getString(Global.FoodHub.RESTAURANTS.NAME);
		address = new Address(row.getString(Global.FoodHub.RESTAURANTS.ADDRESS));
		Global.FoodHub.FOODS.loadRestaurantFoods(this);
		rating = Global.FoodHub.VOTES.restaurantRating(id);
		
		final String rawLogoUrlStr = row.getString(Global.FoodHub.RESTAURANTS.LOGO_URL);
		if (rawLogoUrlStr != null) {
			final Path rawLogoUrlPath = Path.of(rawLogoUrlStr);
			try {
				if (rawLogoUrlPath.isAbsolute()) logo = rawLogoUrlPath.toUri().toURL();
				else
					logo = Global.FoodHub.relativeImagesDir.resolve(rawLogoUrlPath).toUri().toURL();
			} catch (Exception e) {
				e.printStackTrace();
				logo = null;
			}
		}
		
		final String rawImageUrlStr = row.getString(Global.FoodHub.RESTAURANTS.IMAGE_URL);
		if (rawImageUrlStr != null) {
			try {
				final Path rawImageUrlPath = Path.of(rawImageUrlStr);
				if (rawImageUrlPath.isAbsolute()) image = rawImageUrlPath.toUri().toURL();
				else
					image = Global.FoodHub.relativeImagesDir.resolve(rawImageUrlPath).toUri().toURL();
			} catch (Exception e) {
				e.printStackTrace();
				image = null;
			}
		}
		
		deliveryTime = Duration.millis(row.getTime(Global.FoodHub.RESTAURANTS.DELIVERY_TIME).getTime());
		openHours = openHoursFactory(row.getString(Global.FoodHub.RESTAURANTS.OPEN_HOURS));
	}
	
	/**
	 * extracts type of foodsList from the foodsList array
	 *
	 * @return type of foodsList this restaurant serves separated by commas
	 */
	public String getTypes() {
		
		if (foods.isEmpty()) return null;
		return foods.stream().map(food -> food.type).distinct().map(", "::concat).reduce(String::concat).orElse("").substring(2);
		
	}
	
	public String getName() {
		return name;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public URL getImageUrl() {
		return image;
	}
	
	public Rating getRating() {
		return rating;
	}
	
	public Duration getDeliveryTime() {
		return deliveryTime;
	}
	
	private final List<Food> unmodifiableFoods = Collections.unmodifiableList(foods);
	
	public List<Food> getFoods() {
		
		return unmodifiableFoods;
		
	}
	
	public static Range<Time>[] openHoursFactory(String o) {
		
		String[] sections = o.split("/");
		Range<Time>[] openHours = new Range[sections.length];
		
		for (int i = 0; i < sections.length; i++) { //TODO: use format and regex to read hours
			
			String[] edges = sections[i].split("-");
			
			String[] leftEdge = edges[0].split(":");
			String[] rightEdge = edges[1].split(":");
			
			Time from = new Time(Integer.parseInt(leftEdge[0]), Integer.parseInt(leftEdge[1]), 0), to = new Time(Integer.parseInt(rightEdge[0]), Integer.parseInt(rightEdge[1]), 0);
			
			openHours[i] = new Range<>(from, to);
			
		}
		
		return openHours;
		
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public Node graphic() {
		
		return new RestaurantCard(this);
		
	}
	
	private final static Restaurant RAW = new Restaurant(-1, null, null, null, null, null, null, null);
	
	/**
	 * inner class of restaurant
	 * will be added to restaurant foodsList on construction
	 */
	public class Food implements Graphical {
		
		private final int id; //unique
		private final String name;
		private final String type;
		private final Price price;
		private URL image;
		private final Rating rating;
		private transient IntegerProperty quantity;
		
		public Food(int id, String name, String type, Price price, URL image, Rating rating) {
			
			this.id = id;
			this.name = name;
			this.type = type;
			this.price = price;
			this.image = image;
			this.rating = rating;
			Restaurant.this.foods.add(this);
			
		}
		
		public Food(Query.Row row) {
			
			id = row.getInt(Global.FoodHub.FOODS.FOOD_ID);
			name = row.getString(Global.FoodHub.FOODS.NAME);
			type = row.getString(Global.FoodHub.FOODS.TYPE);
			price = new Price((float) row.getDouble(Global.FoodHub.FOODS.PRICE));
			
			final String rawImageUrlStr = row.getString(Global.FoodHub.FOODS.IMAGE_URL);
			if (rawImageUrlStr != null) {
				final Path rawImageUrlPath = Path.of(rawImageUrlStr);
				try {
					if (rawImageUrlPath.isAbsolute()) image = rawImageUrlPath.toUri().toURL();
					else
						image = Global.FoodHub.relativeImagesDir.resolve(rawImageUrlPath).toUri().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					image = null;
				}
			}
			
			rating = Global.FoodHub.VOTES.foodRating(getRestaurant().id, id);
			Restaurant.this.foods.add(this);
			
		}
		
		public Restaurant getRestaurant() {
			
			return Restaurant.this;
			
		}
		
		public String getName() {
			return name;
		}
		
		public String getType() {
			return type;
		}
		
		public Price getPrice() {
			return price;
		}
		
		public URL getImageUrl() {
			return image;
		}
		
		public Rating getRating() {
			return rating;
		}
		
		public int getQuantity() {
			return quantity == null ? 0 : quantity.get();
		}
		
		public IntegerProperty quantityProperty() {
			
			if (quantity == null) quantity = new SimpleIntegerProperty(this, "quantity", 0);
			
			return quantity;
		}
		
		public void setQuantity(int quantity) {
			quantityProperty().set(quantity);
		}
		
		public int getId() {
			return id;
		}
		
		@Override
		public Node graphic() {
			
			return new FoodCard(this);
		}
		
	}
	
}
