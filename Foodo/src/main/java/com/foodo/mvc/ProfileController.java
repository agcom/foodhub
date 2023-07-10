package com.foodo.mvc;

import com.foodo.models.Address;
import com.foodo.models.Profile;
import com.foodo.models.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ProfileController implements Initializable {

    @FXML private Pane root;
    @FXML private ImageView profileImage;
    @FXML private JFXTextField firstNameField, lastNameField, emailField, phoneField;
    @FXML private JFXPasswordField passwordField;
    @FXML private Label fullNameLabel, emailLabel, phoneLabel;
    @FXML private VBox container, addresses;
    private Profile profile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        profileImage.setEffect(new ColorAdjust(0, -1, -0.7, 0));

        Set<Node> editIcons = root.lookupAll("#editIcon");

        editIcons.forEach(editIcon -> editIcon.effectProperty().bind(Bindings.when(editIcon.hoverProperty()).then((Effect) null).otherwise(new ColorAdjust(0, -1, -0.6, 0))));

        //setProfile((Profile) resources.getObject("profile"));

        setProfile(new Profile(new User("Alireza", "Ghasemi", "09178168103", "agcombest@gmail.com", "abcd1234"), null, null));


    }

    public void editFieldAction(MouseEvent e) {

        TextField caller = (TextField) e.getSource();

        //not supported yet

    }

    private void setProfile(Profile profile) {

        this.profile = profile;

        if(profile.getImageUrl() != null) profileImage.setImage(new Image(profile.getImageUrl(), 0, 0, true, true));
        if(profile.getFullName() != null) fullNameLabel.setText(profile.getFirstName() + " " + profile.getLastName());
        if(profile.getEmail() != null) emailLabel.setText(profile.getEmail());
        if(profile.getPhone() != null) phoneLabel.setText(profile.getPhone());

        if(profile.getFirstName() != null) firstNameField.setText(profile.getFirstName());
        if(profile.getLastName() != null) lastNameField.setText(profile.getLastName());
        if(profile.getEmail() != null) emailField.setText(profile.getEmail());
        if(profile.getPhone() != null) phoneField.setText(profile.getPhone());
        if(profile.getPassword() != null) passwordField.setText(profile.getPassword());

    }

}
