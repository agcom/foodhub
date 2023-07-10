package com.foodo.mvc;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MyAddressController implements Initializable {
	
	@FXML
	private TextField textField;
	@FXML
	private JFXButton editButton, removeButton;
	@FXML
	private ImageView editIcon, removeIcon;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		textField.setText(resources.getString("text"));
		
	}
	
}
