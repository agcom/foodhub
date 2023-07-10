package com.foodhub.controllers;

import com.foodhub.Global;
import com.foodhub.models.Address;
import com.foodhub.models.Order;
import com.foodhub.models.Restaurant;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class FinalizeOrderDialogController implements Initializable {
	
	@FXML
	public JFXTextField address;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		RequiredFieldValidator addressRequired = new RequiredFieldValidator("Can't be empty");
		addressRequired.setIcon(new ImageView(new Image(Global.instance().urlStream("images/icons/warning.png"), 15, 15, true, true)));
		address.getValidators().add(addressRequired);
		address.textProperty().addListener(observable -> address.resetValidation());
		
	}
	
	public void done() {
		
		if (address.validate()) {
			
			Order order = new Order(-1, ProfileController.instance().getUser().getEmail(), null, null, new Address(address.getText()));
			
			for (Restaurant.Food food : CartController.instance().foods()) {
				
				order.new OrderItem(food.getQuantity(), food);
				
			}
			
			Global.FoodHub.ORDERS.insertOrder(order);
			
			ProfileController.instance().updateOrders();
			
			CartController.instance().closeFinalizeDialog();
			
			CartController.instance().clear();
			
		} else address.requestFocus();
		
	}
	
}
