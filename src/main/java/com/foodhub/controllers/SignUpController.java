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
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML private FakeFocus fakeFocus;
    @FXML private LoadingWrapper signUpLoading;
    @FXML private JFXTextField firstName, lastName, email, phone;
    @FXML private JFXPasswordField password;
    @FXML private Label error;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Image warning = new Image(Global.instance().urlStream("images/icons/warning.png"), 15, 15, true, true);

        RequiredFieldValidator firstNameRequired = new RequiredFieldValidator("Can't be empty");
        firstNameRequired.setIcon(new ImageView(warning));
        firstName.getValidators().add(firstNameRequired);
        firstName.textProperty().addListener(observable -> firstName.resetValidation());

        RequiredFieldValidator lastNameRequired = new RequiredFieldValidator("Can't be empty");
        lastNameRequired.setIcon(new ImageView(warning));
        lastName.getValidators().add(lastNameRequired);
        lastName.textProperty().addListener(observable -> lastName.resetValidation());

        RequiredFieldValidator emailRequired = new RequiredFieldValidator("Can't be empty");
        emailRequired.setIcon(new ImageView(warning));
        email.getValidators().addAll(emailRequired, new EmailValidator("Invalid email", new ImageView(warning)));
        email.textProperty().addListener(observable -> email.resetValidation());

        RequiredFieldValidator phoneRequired = new RequiredFieldValidator("Can't be empty");
        phoneRequired.setIcon(new ImageView(warning));
        phone.getValidators().add(phoneRequired);
        phone.textProperty().addListener(observable -> phone.resetValidation());

        RequiredFieldValidator passwordRequired = new RequiredFieldValidator("Can't be empty");
        passwordRequired.setIcon(new ImageView(warning));
        password.getValidators().add(passwordRequired);
        password.textProperty().addListener(observable -> password.resetValidation());

        phone.setTextFormatter(new TextFormatter<>(change -> {

            if (change.getText().matches("[0-9]*")) return change;
            else return null;

        }));

        firstName.disableProperty().bind(signUpLoading.loadingProperty());
        lastName.disableProperty().bind(signUpLoading.loadingProperty());
        email.disableProperty().bind(signUpLoading.loadingProperty());
        phone.disableProperty().bind(signUpLoading.loadingProperty());
        password.disableProperty().bind(signUpLoading.loadingProperty());

    }

    public void done() {

        fakeFocus.requestFocus();

        if(validateFields()) {

            signUpLoading.setLoading(true);

            Global.daemonExecutor.submit(() -> {

                try {

                    Global.BPServerMock.Response response = Global.BPServerMock.instance().signUpUser(new UserData(firstName.getText(), lastName.getText(), email.getText(), phone.getText(), password.getText()));

                    if(response.getStatus()) {

                        Platform.runLater(() -> ProfileController.instance().loadUser(response.getUserData()));

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

                signUpLoading.setLoading(false);

            });

        } else firstInvalidField().requestFocus();

    }

    private TextField firstInvalidField() {

        return !firstName.validate() ? firstName : !lastName.validate() ? lastName : !email.validate() ? email : !phone.validate() ? phone : !password.validate() ? password : null;

    }

    private boolean validateFields() {

        boolean[] validations = {firstName.validate(), lastName.validate(), email.validate(), phone.validate(), password.validate()};

        return Utils.stream(validations).allMatch(Boolean::booleanValue);

    }

}
