package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.example.userManagement.entity.User;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ChangeProfileController {
    public TextField firstNameField;
    public TextField lastNameField;
    public TextArea bioField;
    public ImageView profilePicture;
    public Button changePhotoButton;
    public Button saveButton;
    public Button cancelButton;
    public VBox rootPane;
    public Circle profilePictureCircle;
    private MainController mainController;

    private static final String IMAGES_DIR = "/images/";
    @FXML
    public void initialize() {
        applyCircularClip();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initializeResources() {
        firstNameField.setText(mainController.getUser().getFirstName());
        lastNameField.setText(mainController.getUser().getLastName());
        bioField.setText(mainController.getUser().getBio());
        if (mainController.getUser().getProfileImage() != null) {
            Image image = new Image(mainController.getUser().getProfileImage());
            profilePicture.setImage(image);
            profilePictureCircle.setManaged(false);
            profilePictureCircle.setVisible(false);
        } else {
            profilePictureCircle.setManaged(true);
            profilePictureCircle.setVisible(true);
            profilePicture.setManaged(false);
            profilePicture.setVisible(false);
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        mainController.closeChangeProfile();
    }

    @FXML
    private void handleChangePhotoButtonAction() {
        profilePictureCircle.setManaged(false);
        profilePictureCircle.setVisible(false);
        profilePicture.setManaged(true);
        profilePicture.setVisible(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(profilePicture.getScene().getWindow());
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            profilePicture.setImage(selectedImage);
        }
    }


    private void applyCircularClip() {
        Circle clip = new Circle(profilePicture.getFitWidth() / 2, profilePicture.getFitHeight() / 2, profilePicture.getFitWidth() / 2);
        profilePicture.setClip(clip);
    }

    public void handleSave(ActionEvent actionEvent) {
        String decodedPath = null;
        if (profilePicture != null && profilePicture.getImage() != null) {
            decodedPath = URLDecoder.decode(profilePicture.getImage().getUrl(), StandardCharsets.UTF_8);
        }
        User u = mainController.getUser();
        User newU = new User(firstNameField.getText(), lastNameField.getText(), u.getId(), decodedPath, bioField.getText());
        mainController.getSocialNetwork().updateUser(newU);
        mainController.setUser(newU.getId());
        mainController.closeChangeProfile();
        mainController.loadProfile();
    }
}
