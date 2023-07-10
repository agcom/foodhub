package com.foodo.mvc;

import com.foodo.GlobalData;
import com.foodo.models.User;
import com.foodo.utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class SignUpController implements Initializable {
	
	@FXML
	private Label errorLabel;
	@FXML
	private JFXTextField firstNameField, lastNameField, emailField, phoneField;
	@FXML
	private JFXPasswordField passwordField;
	@FXML
	private JFXButton backButton, doneButton;
	
	private Region backPage;
	private JFXDialog loginDialog;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		backPage = ((Region) resources.getObject("backPage"));
		loginDialog = ((JFXDialog) resources.getObject("loginDialog"));
		
		backButton.getGraphic().setEffect(new ColorAdjust(0, 0, 1, 0));
		
		Image invalidImage = new Image(getClass().getResourceAsStream("/com/foodo/images/icons/warning_red.png"), 15, 15, true, true);
		
		firstNameField.getValidators().add(getNewRequiredFieldValidator("Can't be empty", invalidImage));
		lastNameField.getValidators().add(getNewRequiredFieldValidator("Can't be empty", invalidImage));
		passwordField.getValidators().add(getNewRequiredFieldValidator("Can't be empty", invalidImage));
		
		ValidatorBase emailValidator = new ValidatorBase() {
			
			@Override
			protected void eval() {
				
				hasErrors.set(!EmailValidator.getInstance().isValid(emailField.getText()));
				
			}
			
		};
		
		emailValidator.setIcon(new ImageView(invalidImage));
		emailValidator.setMessage("Invalid");
		
		emailField.getValidators().addAll(getNewRequiredFieldValidator("Can't be empty", invalidImage), emailValidator);
		
		RegexValidator phoneValidator = new RegexValidator("Invalid");
		phoneValidator.setRegexPattern("\\d+");
		
		phoneValidator.setIcon(new ImageView(invalidImage));
		
		phoneField.getValidators().addAll(getNewRequiredFieldValidator("Can't be empty", invalidImage), phoneValidator);
		
	}
	
	private RequiredFieldValidator getNewRequiredFieldValidator(String msg, Image icon) {
		
		RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator(msg);
		requiredFieldValidator.setIcon(new ImageView(icon));
		
		return requiredFieldValidator;
		
	}
	
	public void focusField() {
		
		firstNameField.requestFocus();
		
	}
	
	public void setEmail(String email) {
		
		emailField.setText(email);
		
	}
	
	public void backButtonAction() {
		
		loginDialog.setContent(backPage);
		
	}
	
	private User user;
	
	public void doneButtonAction() {
		
		if (validateAllFields()) {
			
			loginDialog.setDisable(true);
			loginDialog.setOverlayClose(false);
			
			GlobalData.globalNonDaemonExecutor.submit(() -> {
				
				if (user == null) user = new User();
				
				user.setFirstName(firstNameField.getText());
				user.setLastName(lastNameField.getText());
				user.setEmail(emailField.getText());
				user.setPhone(phoneField.getText());
				user.setPassword(passwordField.getText());
				
				try {
					
					GlobalData.BPServerMock.Response response = GlobalData.BPServerMock.getServer().signUpUser(user);
					
					if (response.getStatus()) {
						
						//load profile page
						
					} else {
						
						Platform.runLater(() -> {
							
							errorLabel.setText(response.getError());
							errorLabel.setVisible(true);
							loginDialog.setDisable(false);
							loginDialog.setOverlayClose(true);
							
							GlobalData.globalScheduledDaemonExecutor.schedule(() -> Platform.runLater(() -> errorLabel.setVisible(false)), 3, TimeUnit.SECONDS);
							
						});
						
					}
					
				} catch (IOException e) {
					
					Utils.Logger.log(e);
					
				}
				
			});
			
		}
		
	}
	
	private boolean validateAllFields() {
		
		return Utils.stream(fieldsValidation()).allMatch(isValid -> isValid);
		
	}
	
	private boolean[] fieldsValidation() {
		
		boolean[] areValid = new boolean[5];
		
		areValid[0] = firstNameField.validate();
		areValid[1] = lastNameField.validate();
		areValid[2] = emailField.validate();
		areValid[3] = phoneField.validate();
		areValid[4] = passwordField.validate();
		
		return areValid;
		
	}
	
}
