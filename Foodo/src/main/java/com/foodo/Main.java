package com.foodo;

import com.foodo.models.Address;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent splashRoot = FXMLLoader.load(getClass().getResource("/com/foodo/layouts/splash.fxml"));

        Scene splashScene = new Scene(splashRoot);

        Image appIcon = new Image(getClass().getResourceAsStream("/com/foodo/images/icons/app_icon.png"), 0, 0, true, false);

        primaryStage.setScene(splashScene);
        primaryStage.setTitle("Foodo");
        primaryStage.getIcons().add(appIcon);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}
