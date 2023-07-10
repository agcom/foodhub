package com.foodhub.controllers;

import com.foodhub.Global;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class SplashController implements Initializable {

    //TODO: use image switcher with fadein-fadeout for background

    @FXML
    private VBox centerContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initAnimations();

        Task<Parent> primaryLoaderTask = new Task<>() {

            @Override
            protected Parent call() throws IOException {

                try {

                    FXMLLoader loader = new FXMLLoader(Global.instance().url("layouts/primary.fxml"));
                    loader.setController(PrimaryController.instance());

                    return loader.load();

                } catch (Exception e) {

                    e.printStackTrace();

                }

                return null;

            }

        };

        Global.daemonExecutor.submit(primaryLoaderTask);

        primaryLoaderTask.setOnSucceeded(event -> Global.scheduledDaemonExecutor.schedule(() -> Platform.runLater(() -> centerContainer.getScene().setRoot(primaryLoaderTask.getValue())), 2, TimeUnit.SECONDS));

    }

    public void initAnimations() {

        FadeTransition centerFadeIn = new FadeTransition(Duration.seconds(1.3), centerContainer);
        centerFadeIn.setFromValue(0);
        centerFadeIn.setToValue(1);

        TranslateTransition centerTranslateUp = new TranslateTransition(Duration.seconds(1.3), centerContainer);
        centerTranslateUp.setFromY(40);
        centerTranslateUp.setToY(0);

        centerFadeIn.playFromStart();
        centerTranslateUp.playFromStart();

        centerFadeIn.setOnFinished(event -> {

            FadeTransition centerLoadingFade = new FadeTransition(Duration.seconds(1), centerContainer);
            centerLoadingFade.setFromValue(0.9);
            centerLoadingFade.setToValue(0.7);
            centerLoadingFade.setAutoReverse(true);
            centerLoadingFade.setCycleCount(Animation.INDEFINITE);
            centerLoadingFade.play();

        });

    }

}
