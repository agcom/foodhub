package com.foodo.mvc;

import com.foodo.GlobalData;
import com.foodo.models.User;
import com.foodo.utils.MapResourceBundle;
import com.foodo.utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Label emailValidatorLabel;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Label orEmailPhrase;
    @FXML
    private VBox emailFieldBackground;
    @FXML
    private JFXTextField emailField;
    @FXML
    private AnchorPane emailValidatorContainer;
    @FXML
    private JFXButton loginButton, newUserButton;
    private JFXDialog loginDialog;
    @FXML private VBox root;
    private BooleanBinding emailValidator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.loginDialog = (JFXDialog) resources.getObject("loginDialog");

        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue && !emailFieldBackground.getStyleClass().contains("focusedEmail"))
                emailFieldBackground.getStyleClass().add("focusedEmail");
            else emailFieldBackground.getStyleClass().remove("focusedEmail");

        });

        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue && !emailFieldBackground.getStyleClass().contains("focusedPassword"))
                emailFieldBackground.getStyleClass().add("focusedPassword");
            else emailFieldBackground.getStyleClass().remove("focusedPassword");

        });

        BooleanProperty isValidatorDeprecated = new SimpleBooleanProperty(true);

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            isValidatorDeprecated.set(true);
            emailValidatorContainer.setManaged(false);
            emailValidatorContainer.setVisible(false);
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            isValidatorDeprecated.set(true);
            emailValidatorContainer.setManaged(false);
            emailValidatorContainer.setVisible(false);
        });

        emailValidator = new BooleanBinding() {

            {
                super.bind(emailField.textProperty());
            }

            @Override
            protected boolean computeValue() {

                return EmailValidator.getInstance().isValid(emailField.getText());

            }

        };

    }

    private Service<Void> loginService = new Service<>() {

        @Override
        protected Task<Void> createTask() {

            return new Task<>() {

                @Override
                protected Void call() {

                    user.setEmail(passwordField.getPromptText());
                    user.setPassword(passwordField.getText());

                    try {

                        GlobalData.BPServerMock.Response response = GlobalData.BPServerMock.getServer().loginUser(user);

                        if (response.getStatus()) {

                            //load main page

                        } else {

                            Platform.runLater(() -> {

                                loginDialog.setOverlayClose(true);
                                loginDialog.setDisable(false);
                                emailValidatorLabel.setText(response.getError());
                                emailValidatorContainer.setVisible(true);
                                emailValidatorContainer.setManaged(true);

                                // FIXME: 6/8/2019 next line if runs makes bug why? check it
                                //passwordField.requestFocus();


                            });

                        }

                    } catch (IOException e) {

                        Utils.Logger.log(e);

                    }

                    return null;

                }

            };

        }

    };

    {
        loginService.setExecutor(GlobalData.globalDaemonExecutor);
    }

    private User user = new User();

    public void loginAction() {

        switch ((int) loginButton.getUserData()) {

            case 1: {//level 1; email input

                if (emailValidator.get()) {

                    user.setEmail(emailField.getText());

                    passwordField.clear();
                    emailField.setManaged(false);
                    passwordField.setManaged(true);
                    emailField.setVisible(false);
                    passwordField.setVisible(true);
                    passwordField.setPromptText(user.getEmail());

                    orEmailPhrase.setText("Enter password");
                    newUserButton.setText("Get back");

                    loginButton.setUserData(2);
                    newUserButton.setUserData(2);

                    passwordField.requestFocus();

                } else {

                    if(!emailValidatorLabel.getText().equals("Invalid")) emailValidatorLabel.setText("Invalid");
                    emailValidatorContainer.setManaged(true);
                    emailValidatorContainer.setVisible(true);

                }

                break;

            }

            case 2: {//level 2; password input

                if (Utils.isBlank(passwordField.getText())) {

                    if(!emailValidatorLabel.getText().equals("Invalid")) emailValidatorLabel.setText("Invalid");
                    emailValidatorContainer.setManaged(true);
                    emailValidatorContainer.setVisible(true);

                } else {

                    loginDialog.setOverlayClose(false);
                    loginDialog.setDisable(true);
                    loginService.restart();

                }

            }

        }

    }

    private MapResourceBundle signUpBundle;
    private Region signUpPage;
    private SignUpController signupController;

    public void newUserAction() {

        switch ((int) newUserButton.getUserData()) {

            case 1: {//level 1; new user button

                if(!emailValidator.get()) {

                    emailValidatorContainer.setManaged(true);
                    emailValidatorContainer.setVisible(true);

                    return;

                }

                if(signUpBundle == null) {

                    GlobalData.globalDaemonExecutor.submit(() -> {

                        signUpBundle = new MapResourceBundle();
                        signUpBundle.getMap().put("backPage", root);
                        signUpBundle.getMap().put("loginDialog", loginDialog);
                        Platform.runLater(this::newUserAction);

                    });

                    return;

                }

                if(signUpPage == null) {

                    GlobalData.globalDaemonExecutor.submit(() -> {

                        FXMLLoader signUpLoader = new FXMLLoader(getClass().getResource("/com/foodo/layouts/dialogs/signUp.fxml"), signUpBundle);

                        try {

                            signUpPage = signUpLoader.load();
                            signupController = signUpLoader.getController();
                            Platform.runLater(this::newUserAction);

                        } catch (IOException e) {

                            Utils.Logger.log(e);

                        }

                    });

                    return;

                }

                loginDialog.setContent(signUpPage);
                signupController.setEmail(emailField.getText());
                signupController.focusField();

                break;
            }

            case 2: {//level 2; back button

                passwordField.setManaged(false);
                emailField.setManaged(true);
                passwordField.setVisible(false);
                emailField.setVisible(true);

                orEmailPhrase.setText("Or use your email address");
                newUserButton.setText("New account");

                loginButton.setUserData(1);
                newUserButton.setUserData(1);

                emailValidatorContainer.setManaged(false);
                emailValidatorContainer.setVisible(false);

                emailField.requestFocus();

            }

        }

    }

}
