package com.foodhub.views;

import com.foodhub.Global;
import com.foodhub.controllers.CartController;
import com.foodhub.models.Order;
import com.foodhub.models.Price;
import com.foodhub.models.Rating;
import com.foodhub.models.Restaurant;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class FoodCard extends VBox implements Initializable {

    @FXML private Label name, type;
    @FXML private Pane image;
    @FXML private Label price, quantityLabel;
    @FXML private RatingView rating;
    @FXML private JFXButton add, increase;
    @FXML private NodeSwitcher addToCartSwitcher;
    @FXML private Text total;
    @FXML private FakeFocus fakeFocus;

    private ObjectProperty<Restaurant.Food> food = new SimpleObjectProperty<>(this, "food");

    public FoodCard(Restaurant.Food food) {

        getStyleClass().add("food-card");
        setFood(food);
        food.quantityProperty().addListener((observable, oldValue, newValue) -> {

            int index;
            if(newValue.intValue() < 1 && (index = CartController.instance().foods().indexOf(getFood())) >= 0) {

                CartController.instance().foods().remove(index);

            } else if(newValue.intValue() > 0 && !CartController.instance().foods().contains(getFood())) {

                CartController.instance().foods().add(getFood());
            }

        });

        loadFxml();

    }

    private void loadFxml() {

        FXMLLoader loader = new FXMLLoader(Global.instance().url("layouts/foodCard.fxml"));
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

        quantityLabel.textProperty().bind(Bindings.format("%d", getFood().quantityProperty()));

        addToCartSwitcher.currentNodeIndexProperty().bind(Bindings.when(getFood().quantityProperty().lessThan(1)).then(0).otherwise(1));

        total.textProperty().bind(Bindings.createStringBinding(() -> String.format("%.2f $", getFood().getPrice().multiply(getQuantity()).getValue()), getFood().quantityProperty()));

        updateViews();

        food.addListener(observable -> updateViews());

    }

    private void updateViews() {

        if(getFood().getName() != null) name.setText(getFood().getName());
        else name.setText("-");

        if(getFood().getType() != null) type.setText(getFood().getType());
        else type.setText("-");

        if(getFood().getImageUrl() != null) this.image.setBackground(new Background(new BackgroundImage(new Image(getFood().getImageUrl().toExternalForm(), this.image.getMaxWidth(), this.image.getMaxHeight(), true, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true))));
        else image.setStyle("-fx-background-image: url('/com/foodhub/images/noImage.jpg');");

        if(getFood().getPrice() != null) price.setText(getFood().getPrice().toString());
        else price.setText("-");

        if(getFood().getRating() != null) rating.setRating(getFood().getRating());
        else rating.setRating(Rating.ZERO);

    }

    public Restaurant.Food getFood() {
        return food.get();
    }

    public ObjectProperty<Restaurant.Food> foodProperty() {
        return food;
    }

    public void setFood(Restaurant.Food food) {
        this.food.set(food);
    }

    public void increaseQuantity() {

        fakeFocus.requestFocus();
        setQuantity(getQuantity() + 1);

    }

    public void decreaseQuantity() {

        if(getQuantity() <= 1) {

            setQuantity(0);
            fakeFocus.requestFocus();

        } else {

            setQuantity(getQuantity() - 1);

        }

    }

    public int getQuantity() {
        return getFood().getQuantity();
    }

    public void setQuantity(int quantity) {

        getFood().setQuantity(quantity);

    }

}
