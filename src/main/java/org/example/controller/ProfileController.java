package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class ProfileController {
    @FXML public ImageView profilePicture;
    @FXML public Circle profilePictureCircle;
    @FXML public Button logoutButton;
    @FXML private Button changeProfileButton;
    @FXML private Label bioLabel;
    @FXML private Circle profilePictureBackground;
    @FXML private Label usernameLabel;
    @FXML private Button friendsButton;
    private Runnable friendsViewLoader;
    private MainController mainController;


    @FXML
    public void initialize() {
        friendsButton.setText("Friends");
        usernameLabel.setText("Username");
        applyCircularClip();
    }

    public void setFriendsViewLoader(Runnable friendsViewLoader) {
        this.friendsViewLoader = friendsViewLoader;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setProfilePicture(String pathToImage) {
        if (mainController.getUser().getProfileImage() != null && !Objects.equals(mainController.getUser().getProfileImage(), "")) {
            profilePicture.setManaged(true);
            profilePicture.setVisible(true);
            Image image = new Image(pathToImage);
            profilePicture.setImage(image);
            profilePictureCircle.setVisible(false);
            profilePictureCircle.setManaged(false);
        } else {
            profilePictureCircle.setVisible(true);
            profilePictureCircle.setManaged(true);
            profilePicture.setManaged(false);
            profilePicture.setVisible(false);
        }
    }

    @FXML
    private void onFriendsButtonClicked() {
        if (friendsViewLoader != null) {
            friendsViewLoader.run();
        }
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public void onChangeProfile(ActionEvent actionEvent) {
        mainController.loadChangeProfile();
    }

    private void applyCircularClip() {
        Circle clip = new Circle(profilePicture.getFitWidth() / 2, profilePicture.getFitHeight() / 2, profilePicture.getFitWidth() / 2);
        profilePicture.setClip(clip);
    }

    public void setBio(String bio) {
        bioLabel.setText(bio);
    }

    public void onLogout(ActionEvent actionEvent) {
        mainController.logout();
    }
}
