package com.foodhub.views;

import com.foodhub.models.Rating;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//TODO: show votes count on hover
//TODO: past rating view for edit and adding votes
public class RatingView extends Label implements Initializable {
	
	@FXML
	private ImageView iconView;
	@FXML
	private Label ratingLabel;
	private final ObjectProperty<Image> icon = new SimpleObjectProperty<>(this, "icon");
	private final ObjectProperty<Rating> rating = new SimpleObjectProperty<>(this, "rating");
	
	public RatingView(@NamedArg(value = "icon") Image icon,
	                  @NamedArg(value = "rating") Rating rating) {
		
		//defaults
		super.getStyleClass().add("rating-view");
		
		setIcon(icon);
		setRating(rating);
		loadFxml();
		
	}
	
	public RatingView(@NamedArg(value = "icon") Image icon) {
		
		this(icon, new Rating(0));
		
	}
	
	private void loadFxml() {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/foodhub/layouts/ratingView.fxml"));
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
		
		//bind things
		ratingLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			
			if (Double.isNaN(getRating().rating())) return "0.0";
			
			return String.format("%.1f", getRating().rating());
			
		}, rating));
		
		iconView.imageProperty().bind(icon);
		
	}
	
	public Rating getRating() {
		return rating.get();
	}
	
	public ObjectProperty<Rating> ratingProperty() {
		return rating;
	}
	
	public void setRating(Rating rating) {
		this.rating.set(rating);
	}
	
	public Image getIcon() {
		return icon.get();
	}
	
	public ObjectProperty<Image> iconProperty() {
		return icon;
	}
	
	public void setIcon(Image icon) {
		this.icon.set(icon);
	}
	
}
