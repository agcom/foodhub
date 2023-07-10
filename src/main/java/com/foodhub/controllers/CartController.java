package com.foodhub.controllers;

import com.foodhub.Global;
import com.foodhub.models.Restaurant;
import com.foodhub.views.FoodCard;
import com.foodhub.views.NodeSwitcher;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {
	
	private static final CartController controller = new CartController();
	
	public static CartController instance() {
		
		return controller;
		
	}
	
	private CartController() {
	
	}
	
	@FXML
	private NodeSwitcher switcher;
	@FXML
	private TilePane foodsPane;
	@FXML
	private StackPane dialogSupport;
	private final ObservableList<Restaurant.Food> foods = FXCollections.observableArrayList(new LinkedList<>());
	private FinalizeOrderDialogController finalizeController;
	private JFXDialog finalizeDialog;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		switcher.currentNodeIndexProperty().bind(Bindings.when(Bindings.isEmpty(foods)).then(0).otherwise(1));
		
	}
	
	public List<Restaurant.Food> foods() {
		
		return foods;
		
	}
	
	public void update() {
		
		Global.daemonExecutor.submit(() -> {
			
			FoodCard[] cards = foods.stream().map(FoodCard::new).toArray(FoodCard[]::new);
			
			Platform.runLater(() -> {
				
				if (foodsPane.getChildren().size() > 1)
					foodsPane.getChildren().remove(0, foodsPane.getChildren().size() - 1);
				
				if (cards.length != 0) {
					
					foodsPane.getChildren().addAll(foodsPane.getChildren().size() - 1, Arrays.asList(cards));
					
				}
				
			});
			
		});
		
	}
	
	public void finalizeOrder() {
		
		if (ProfileController.instance().getUser() == null) ProfileController.instance().loadLoginPage();
		else {
			
			if (finalizeDialog == null) {
				
				Global.daemonExecutor.submit(() -> {
					
					try {
						
						FXMLLoader loader = new FXMLLoader(Global.instance().url("layouts/finalizeOrderDialog.fxml"));
						
						finalizeDialog = new JFXDialog(dialogSupport, loader.load(), JFXDialog.DialogTransition.RIGHT, true);
						
						finalizeController = loader.getController();
						
						Platform.runLater(this::finalizeOrder);
						
					} catch (IOException e) {
						
						e.printStackTrace();
						
					}
					
				});
				
			} else {
				
				finalizeDialog.show();
				finalizeController.address.requestFocus();
				
			}
			
		}
		
	}
	
	public void closeFinalizeDialog() {
		
		if (finalizeDialog != null) finalizeDialog.close();
		
	}
	
	public void clear() {
		final int foodsSize = foods.size();
		for (int i = 0; i < foodsSize; i++) {
			foods.stream().findAny().get().setQuantity(0);
		}
	}
	
}
