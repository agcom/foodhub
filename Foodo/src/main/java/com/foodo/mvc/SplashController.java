package com.foodo.mvc;

import com.foodo.GlobalData;
import com.foodo.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class SplashController implements Initializable {
	
	@FXML
	private ProgressBar loadingBar;
	@FXML
	private Label loadingDetailsLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		loadEarlyData();
		
	}
	
	private void loadEarlyData() {
		
		loadingDetailsLabel.setText("Loading early data");
		
		// TODO: 6/6/2019 SmartExecutor class for executing several tasks and looking up for overall progress, on succeed of all tasks method
		
		Service<Void> heavyStaticsLoader = new Service<>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<>() {
					@Override
					protected Void call() throws Exception {
						
						updateProgress(0, 1);
						Class.forName("com.foodo.GlobalData");
						updateProgress(1, 1);
						
						return null;
					}
				};
			}
		};
		
		loadingBar.progressProperty().bind(heavyStaticsLoader.progressProperty());
		heavyStaticsLoader.start();
		
		//on succeed all tasks
		GlobalData.globalScheduledDaemonExecutor.schedule(() -> {
			
			try {
				
				Parent startLayout = FXMLLoader.load(getClass().getResource("/com/foodo/layouts/start.fxml"));
				Platform.runLater(() -> loadingBar.getScene().setRoot(startLayout));
				
			} catch (IOException e) {
				
				Utils.Logger.log(e);
				
			}
			
			
		}, 3, TimeUnit.SECONDS);
		
	}
	
}
