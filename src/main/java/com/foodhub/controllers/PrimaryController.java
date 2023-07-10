package com.foodhub.controllers;

import com.foodhub.Global;
import com.foodhub.utils.Navigator;
import com.foodhub.utils.SingleResourceBundle;
import com.foodhub.utils.Utils;
import com.foodhub.views.Slider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
	
	@FXML
	private Slider slider;
	@FXML
	private BottomMenuController bottomMenuController;
	@FXML
	private VBox root;
	private final Navigator navigator = new Navigator();
	private final Navigator[] childs = new Navigator[4];
	
	{
		
		Utils.fill(childs, Navigator::new);
		
	}
	
	private boolean isNavigatorCall = false;
	
	public static PrimaryController primary = new PrimaryController();
	
	private PrimaryController() {
	
	
	}
	
	public static PrimaryController instance() {
		
		return primary;
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		slider.currentSlideIndexProperty().addListener((observable, oldValue, newValue) -> {
			
			if (isNavigatorCall || newValue.equals(oldValue)) return;
			
			navigator.addNavi(() -> {
				
				isNavigatorCall = true;
				slider.setCurrentSlideIndex(newValue.intValue());
				isNavigatorCall = false;
				return true;
				
			}, childs[newValue.intValue()]);
			
		});
		
		initSliderNodes();
		
		bottomMenuController.back.setOnMouseClicked(event -> navigator.back());
		bottomMenuController.home.setOnAction(() -> slider.setCurrentSlideIndex(1));
		bottomMenuController.cart.setOnAction(() -> {
			
			CartController.instance().update();
			slider.setCurrentSlideIndex(0);
			
		});
		bottomMenuController.menu.selectedItemIndexProperty().bindBidirectional(slider.currentSlideIndexProperty());
		
	}
	
	private void initSliderNodes() {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(Global.instance().url("layouts/cart.fxml"));
			loader.setController(CartController.instance());
			loader.setResources(new SingleResourceBundle("navigator", childs[0]));
			slider.getNodes().add(loader.load());
			
			slider.getNodes().add(FXMLLoader.load(Global.instance().url("layouts/home.fxml"), new SingleResourceBundle("navigator", childs[1])));
			
			slider.getNodes().add(FXMLLoader.load(Global.instance().url("layouts/search.fxml"), new SingleResourceBundle("navigator", childs[2])));
			
			loader = new FXMLLoader(Global.instance().url("layouts/profile.fxml"));
			loader.setController(ProfileController.instance());
			loader.setResources(new SingleResourceBundle("navigator", childs[3]));
			slider.getNodes().add(loader.load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		slider.setCurrentSlideIndex(1);
		
	}
	
	public void loadProfilePage() {
		
		slider.setCurrentSlideIndex(3);
		
	}
	
}