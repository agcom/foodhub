package com.foodhub.controllers;

import com.foodhub.views.BottomMenu;
import com.foodhub.views.BottomMenuItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

//TODO: include controller with reflection
public class BottomMenuController implements Initializable {
	
	@FXML
	public BorderPane back;
	@FXML
	public BottomMenuItem home;
	@FXML
	public BottomMenuItem cart;
	@FXML
	public BottomMenuItem search;
	@FXML
	public BottomMenuItem profile;
	@FXML
	public BottomMenu menu;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	
	}
	
}
