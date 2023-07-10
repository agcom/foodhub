package com.foodhub.views;

import com.foodhub.Global;
import com.foodhub.models.Order;
import com.foodhub.models.Price;
import com.foodhub.models.Restaurant;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OrderTreeItemView extends HBox implements Initializable {

    @FXML private Label first, second, third;

    private Order parent;

    public OrderTreeItemView(Order parent) {

        this.parent = parent;
        loadFxml();

    }

    private void loadFxml() {

        FXMLLoader loader = new FXMLLoader(Global.instance().url("layouts/OrderTreeItemView.fxml"));
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



    }

    public void loadOrderItem(Order.OrderItem order) {

        first.setText(order.getCount() + " x ");
        second.setText(order.getFood().getName());
        third.setText(String.format("%.2f $", order.getFood().getPrice().multiply(order.getCount()).getValue()));

    }

    public void loadRestaurant(Restaurant restaurant) {

        first.setText(restaurant.getName());
        second.setText(parent.getItems().stream().filter(item -> item.getFood().getRestaurant().equals(restaurant)).map(Order.OrderItem::getCount).reduce(Integer::sum).orElse(0) + " items");
        third.setText(String.format("%.2f $", parent.getItems().stream().map(item -> item.getFood().getPrice().multiply(item.getCount())).reduce(Price::sum).orElse(new Price(0)).getValue()));

    }

    public void loadOrder() {

        first.setText("#" + parent.getId());
        second.setText(parent.getDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd'/'MM'/'uuuu'-'HH':'mm")));
        third.setText(String.format("%.2f $", parent.total().getValue()));

    }

    public Order getOrder() {
        return parent;
    }

    public void setOrder(Order order) {
        this.parent = order;
    }
}
