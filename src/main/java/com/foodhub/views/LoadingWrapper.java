package com.foodhub.views;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

@DefaultProperty("content")
public class LoadingWrapper extends BorderPane {
	
	private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
	private final BooleanProperty loading = new SimpleBooleanProperty(this, "loading", false);
	private final FadeTransition fader = new FadeTransition(Duration.seconds(1));
	
	public LoadingWrapper(@NamedArg(value = "content") Node content, @NamedArg(value = "min", defaultValue = "0.7") double min, @NamedArg(value = "max", defaultValue = "0.9") double max) {
		
		super.getStyleClass().add("loading-wrapper");
		
		setContent(content);
		super.centerProperty().bind(this.content);
		this.content.addListener(observable -> super.getChildren().set(0, getContent()));
		
		fader.nodeProperty().bind(this.content);
		fader.setCycleCount(Animation.INDEFINITE);
		fader.setAutoReverse(true);
		fader.setFromValue(max);
		fader.setToValue(min);
		
		loading.addListener((observable, oldValue, newValue) -> {
			
			if (newValue) {
				
				getContent().setMouseTransparent(true);
				fader.play();
				
			} else {
				
				getContent().setMouseTransparent(false);
				fader.stop();
				getContent().setOpacity(1);
				
			}
			
		});
		
	}
	
	public void setLoading(boolean loading) {
		
		this.loading.set(loading);
		
	}
	
	public Node getContent() {
		return content.get();
	}
	
	public ObjectProperty<Node> contentProperty() {
		return content;
	}
	
	public void setContent(Node content) {
		this.content.set(content);
	}
	
	public boolean isLoading() {
		return loading.get();
	}
	
	public BooleanProperty loadingProperty() {
		return loading;
	}
}
