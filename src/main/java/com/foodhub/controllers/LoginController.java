package com.foodhub.controllers;

import com.foodhub.Global;
import com.foodhub.models.UserData;
import com.foodhub.utils.EmailValidator;
import com.foodhub.utils.Utils;
import com.foodhub.views.FakeFocus;
import com.foodhub.views.LoadingWrapper;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private FakeFocus fakeFocus;
    @FXML private LoadingWrapper loginLoading;
    @FXML private Label error;
    @FXML private JFXTextField emailField;
    @FXML private JFXPasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Image errorIcon = new Image(Global.instance().urlStream("images/icons/warning.png"), 15, 15, true, true);

        RequiredFieldValidator emailRequired = new RequiredFieldValidator("Can't be empty");
        emailRequired.setIcon(new ImageView(errorIcon));
        EmailValidator emailValidator = new EmailValidator("Invalid email");
        emailValidator.setIcon(new ImageView(errorIcon));
        emailField.getValidators().addAll(emailRequired, emailValidator);

        RequiredFieldValidator passwordRequired = new RequiredFieldValidator("Can't be empty");
        passwordRequired.setIcon(new ImageView(errorIcon));
        passwordField.getValidators().add(passwordRequired);

        emailField.textProperty().addListener(observable -> emailField.resetValidation());
        passwordField.textProperty().addListener(observable -> passwordField.resetValidation());

        Global.daemonExecutor.submit(() -> {

            String savedEmail = Global.USER.PREFERENCES.getSavedEmail();

            if (savedEmail != null) Platform.runLater(() -> emailField.setText(savedEmail));

        });

        emailField.disableProperty().bind(loginLoading.loadingProperty());
        passwordField.disableProperty().bind(loginLoading.loadingProperty());

    }

    public void google() {

        //

    }

    public void facebook() {

        //

    }

    public void login() {

        fakeFocus.requestFocus();

        if (validateFields()) {

            loginLoading.setLoading(true);

            Global.daemonExecutor.submit(() -> {

                try {

                    Global.BPServer.BPServerResponse response = Global.BPServer.instance().loginUser(emailField.getText(), passwordField.getText());

                    if(response.getStatus()) {

                        UserData userData = response.getUserData();

                        Platform.runLater(() -> ProfileController.instance().loadUser(userData));

                    } else {

                        Platform.runLater(() -> {

                            error.setText(response.getError());
                            Utils.setDeploy(error, false);

                            FadeTransition fader = new FadeTransition(Duration.seconds(1), error);
                            fader.setToValue(0);
                            fader.setDelay(Duration.seconds(3));
                            fader.setOnFinished(event -> {

                                Utils.setDeploy(error, true);
                                error.setOpacity(1);

                            });
                            fader.play();

                        });

                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

                loginLoading.setLoading(false);

            });

        } else firstInvalidField().requestFocus();

    }

    public void signUp() {

        ProfileController.instance().loadSignUpPage();

    }

    private boolean validateFields() {

        boolean[] validations = {emailField.validate(), passwordField.validate()};

        return Utils.stream(validations).allMatch(Boolean::booleanValue);

    }

    private TextField firstInvalidField() {

        return emailField.getActiveValidator() != null ? emailField : passwordField.getActiveValidator() != null ? passwordField : null;

    }

    public void reset() {

        emailField.setText(null);
        passwordField.setText(null);

        Global.daemonExecutor.submit(() -> {

            String savedEmail = Global.USER.PREFERENCES.getSavedEmail();

            if (savedEmail != null) Platform.runLater(() -> emailField.setText(savedEmail));

        });

    }

}
