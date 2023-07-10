package com.foodo.mvc;

import com.foodo.GlobalData;
import com.foodo.models.Address;
import com.foodo.models.Query;
import com.foodo.utils.MapResourceBundle;
import com.foodo.utils.Utils;
import com.foodo.views.AddressListCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class StartController implements Initializable {
	
	@FXML
	private StackPane dialogContainerSupport;
	@FXML
	private HBox addressFieldBackground;
	@FXML
	private Label beautifulPhrase;
	@FXML
	private JFXTextField addressField;
	@FXML
	private ListView<Address> addressList;
	@FXML
	private Label loginPhrase;
	@FXML
	private JFXButton loginButton;
	@FXML
	private Label aboutUsLabel;
	@FXML
	private ImageView aboutUsArrow1, aboutUsArrow2, arrowButton;
	private JFXDialog loginDialog;
	
	private final Service<Void> addressSearcher = new Service<>() {
		
		@Override
		protected Task<Void> createTask() {
			
			return new Task<>() {
				
				@Override
				protected Void call() {
					
					try {
						
						if (addressList.getItems().size() >= 1)
							Platform.runLater(() -> addressList.getItems().remove(1, addressList.getItems().size()));
						if (!addressList.getItems().get(0).equals(AddressListCell.loading))
							Platform.runLater(() -> addressList.getItems().set(0, AddressListCell.loading));
						
						Query query = GlobalData.Foodo.DB.executeQuery(String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%'", GlobalData.Foodo.COVERED_ADDRESSES.T.NAME, GlobalData.Foodo.COVERED_ADDRESSES.ADDRESS.NAME, addressField.getText().trim()));
						
						Address[] addresses = query.getRows().stream().map(row -> {
							try {
								
								return new Address((String) row.getObjectHolder(GlobalData.Foodo.COVERED_ADDRESSES.ADDRESS.INDEX).obj);
								
							} catch (SQLException e) {
								
								Utils.Logger.log(e, false);
								
							}
							
							return null;
							
						}).toArray(Address[]::new);
						
						if (addresses.length == 0) {
							
							Platform.runLater(() -> addressList.getItems().set(0, AddressListCell.nothing));
							
						} else {
							
							Platform.runLater(() -> {
								
								addressList.getItems().remove(0);
								addressList.getItems().addAll(addresses);
								
							});
							
						}
						
					} catch (Exception e) {
						
						Utils.Logger.log(e, false);
						
					}
					
					return null;
					
				}
				
			};
			
		}
		
	};
	
	{
		addressSearcher.setExecutor(GlobalData.globalDaemonExecutor);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {
			
			beautifulPhrase.setText(GlobalData.getInstance().loadPhrase("beautifulPhrase").phrase);
			loginPhrase.setText(GlobalData.getInstance().loadPhrase("loginPhrase").phrase);
			
		} catch (Exception e) {
			
			Utils.Logger.log(e);
			
		}
		
		loginButton.prefWidthProperty().bind(loginPhrase.widthProperty());
		
		addressField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			
			if (newValue) addressFieldBackground.getStyleClass().add("focused");
			else addressFieldBackground.getStyleClass().remove("focused");
			
		});
		
		arrowButton.effectProperty().bind(Bindings.when(arrowButton.hoverProperty()).then((Effect) null).otherwise(new ColorAdjust(0, -1, -0.6, 0)));
		
		addressList.setCellFactory(list -> new AddressListCell());
		addressList.setItems(FXCollections.observableList(new LinkedList<>()));
		addressList.getItems().add(AddressListCell.loading);
		addressList.prefHeightProperty().bind(Bindings.size(addressList.getItems()).multiply(addressList.fixedCellSizeProperty()).add(2));
		
		addressField.textProperty().addListener((observable, oldValue, newValue) -> {
			
			if (Utils.isBlank(newValue)) {
				
				addressList.setVisible(false);
				addressSearcher.cancel();
				
			} else {
				
				addressList.setVisible(true);
				addressSearcher.restart();
				
			}
			
		});
		
		addressList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		addressList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			if (newValue == null) return;
			
			if (newValue.equals(AddressListCell.nothing) || newValue.equals(AddressListCell.loading)) return;
			
			loadMain(newValue);
			
		});
		
		loginDialog = new JFXDialog();
		try {
			
			MapResourceBundle dialogBundled = new MapResourceBundle();
			dialogBundled.getMap().put("loginDialog", loginDialog);
			
			loginDialog.setContent(FXMLLoader.load(getClass().getResource("/com/foodo/layouts/dialogs/login.fxml"), dialogBundled));
		} catch (IOException e) {
			Utils.Logger.log(e);
		}
		loginDialog.setOverlayClose(true);
		loginDialog.setDialogContainer(dialogContainerSupport);
		
	}
	
	private void loadMain(Address address) {
		
		MapResourceBundle resourceBundle = new MapResourceBundle();
		
		resourceBundle.getMap().put("selectedAddress", address);
		
		GlobalData.globalDaemonExecutor.submit(() -> {
			
			try {
				
				Parent root = FXMLLoader.load(getClass().getResource("/com/foodo/layouts/main.fxml"), resourceBundle);
				Platform.runLater(() -> addressField.getScene().setRoot(root));
				
			} catch (IOException e) {
				
				Utils.Logger.log(e);
				
			}
			
		});
		
	}
	
	public void clickedArrowButton() {
		
		addressList.getSelectionModel().selectFirst();
		
	}
	
	
	public void loginButtonAction(ActionEvent e) {
		
		loginDialog.show();
		
	}
	
	public void aboutUsAction(MouseEvent mouseEvent) {
		
		//About us layout
		
	}
	
}
