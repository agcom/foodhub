package com.foodhub.controllers;

import com.foodhub.Global;
import com.foodhub.utils.Navigator;
import com.foodhub.views.FoodCard;
import com.foodhub.views.LoadingWrapper;
import com.foodhub.views.NodeSwitcher;
import com.foodhub.views.RestaurantCard;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

//TODO: use svg icons and svgloader

public class HomeController implements Initializable {

    @FXML private LoadingWrapper restaurantPageLoading;
    @FXML private ScrollPane restaurantPageScroll;
    @FXML private ScrollPane restaurantsScroll;
    @FXML private NodeSwitcher homeSwitcher;
    @FXML private VBox restaurants;
    @FXML private TilePane foods;
    @FXML private RestaurantCard restaurantCard;
    private Navigator navigator;
    private Navigator rpNavigator = new Navigator();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        navigator = (Navigator) resources.getObject("navigator");

        restaurants.getChildren().addListener((ListChangeListener<Node>) c -> {

            while (c.next()) {

                if (c.wasAdded()) {

                    for (Node node : c.getAddedSubList()) {

                        node.setOnMouseClicked(HomeController.this::restaurantClicked);

                    }

                }

            }

        });

        restaurants.getChildren().addAll(Global.FoodHub.RESTAURANTS.all().stream().map(RestaurantCard::new).toArray(RestaurantCard[]::new));

        navigator.addNavi(() -> {

            homeSwitcher.setCurrentNodeIndex(0);
            return true;

        }, null);

        navigator.addFutureNavi(new Navigator.Navi(() -> {

            homeSwitcher.setCurrentNodeIndex(1);
            return true;

        }, rpNavigator));

        restaurantPageScroll.hvalueProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

    }

    private Service<Void> restaurantPageUpdater = new Service<>() {

        @Override
        protected Task<Void> createTask() {

            return new Task<>() {

                @Override
                protected Void call() {

                    FoodCard[] cards = restaurantCard.getRestaurant().getFoods().stream().map(FoodCard::new).toArray(FoodCard[]::new);
                    Platform.runLater(() -> {

                        foods.getChildren().setAll(cards);
                        restaurantPageLoading.setLoading(false);

                    });

                    return null;

                }

            };

        }

    };

    {

        restaurantPageUpdater.setExecutor(Global.daemonExecutor);

    }

    private void restaurantClicked(MouseEvent e) {

        RestaurantCard clickedCard = (RestaurantCard) e.getSource();

        if (!(restaurantCard.getRestaurant() != null && restaurantCard.getRestaurant().equals(clickedCard.getRestaurant()))) {

            restaurantCard.setRestaurant(clickedCard.getRestaurant());
            restaurantPageLoading.setLoading(true);
            restaurantPageUpdater.restart();

        }

        navigator.forward();

        rpNavigator.addNavi(() -> {

            if (restaurantPageScroll.getVvalue() == restaurantPageScroll.getVmin()) return false;
            else {

                restaurantPageScroll.setVvalue(restaurantPageScroll.getVmin());
                return true;

            }

        }, null);

    }

}
