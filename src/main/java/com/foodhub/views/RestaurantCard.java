package com.foodhub.views;

import com.foodhub.models.Rating;
import com.foodhub.models.Restaurant;
import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

//TODO: add logo (shows over image on hover); closed effect and check for open hours
public class RestaurantCard extends VBox implements Initializable {

    @FXML private Pane image;
    @FXML private Label name, types, address, deliveryTime;
    @FXML private RatingView rating;
    @FXML private Label votes;
    private ObjectProperty<Restaurant> restaurant = new SimpleObjectProperty<>(this, "restaurant");

    public RestaurantCard(@NamedArg(value = "restaurant") Restaurant restaurant) {

        getStyleClass().add("restaurant-card");

        setRestaurant(restaurant);
        loadFxml();

    }

    public RestaurantCard() {

        this(null);

    }

    private void loadFxml() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/foodhub/layouts/restaurantCard.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {

            loader.load();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateViews();
        restaurant.addListener((observable, oldValue, newValue) -> updateViews());

    }

    private void updateViews() {

        if(getRestaurant() == null) return;

        if(getRestaurant().getImageUrl() != null) this.image.setBackground(new Background(new BackgroundImage(new Image(getRestaurant().getImageUrl().toExternalForm(), this.image.getWidth(), 0, true, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true))));

        else image.setStyle("-fx-background-image: url('" + "/com/foodhub/images/noImage.jpg" + "');");

        if(getRestaurant().getName() != null) name.setText(getRestaurant().getName());
        else name.setText("-");

        if(getRestaurant().getTypes() != null) types.setText(getRestaurant().getTypes());
        else types.setText("-");

        if(getRestaurant().getAddress() != null) address.setText(getRestaurant().getAddress().getAddress());
        else address.setText("-");

        if(getRestaurant().getDeliveryTime() != null) deliveryTime.setText(String.format("%.0f Mins", getRestaurant().getDeliveryTime().toMinutes()));
        else deliveryTime.setText("-");

        if(getRestaurant().getRating() != null) rating.setRating(getRestaurant().getRating());
        else rating.setRating(Rating.ZERO);

        if(getRestaurant().getRating().votes() > 0) votes.setText(String.valueOf(getRestaurant().getRating().votes()));
        else votes.setText("-");

    }

    public Restaurant getRestaurant() {
        return restaurant.get();
    }

    public ObjectProperty<Restaurant> restaurantProperty() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant.set(restaurant);
    }

}