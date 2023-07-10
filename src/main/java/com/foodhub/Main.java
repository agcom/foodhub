package com.foodhub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	
	public Main() {
		Global.instance().appInstance = this;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setScene(new Scene(FXMLLoader.load(Global.instance().url("layouts/splash.fxml"))));
		primaryStage.setTitle("Food Hub");
		primaryStage.getIcons().add(new Image(Global.instance().url("images/icons/app.png").openStream(), 0, 0, true, true));
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}