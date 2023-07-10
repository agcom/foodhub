package com.foodhub;

import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.security.AuthProvider;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

public class Main extends Application {

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