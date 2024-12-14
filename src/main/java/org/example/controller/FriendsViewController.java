package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.example.userManagement.entity.User;

import java.util.List;

public class FriendsViewController {
    @FXML
    public ListView<User> friendsListView;

    @FXML
    public Button backButton;

    @FXML
    public TextField searchBar;

    @FXML
    public Label profileUsernameLabel;


    private final ObservableList<User> friends = FXCollections.observableArrayList();
    private Runnable profileViewLoader;
    private MainController mainController;
    private User currentSelectedUser;


    @FXML
    public void initialize() {
        friendsListView.setItems(friends);

        friendsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Node circle;
                    if (user.getProfileImage() == null) {
                        Region circleReg = new Region();
                        circleReg.getStyleClass().add("friend-circle");
                        circle = circleReg;
                    } else {
                        ImageView circleReg = new ImageView(new Image(user.getProfileImage()));

                        circleReg.setFitWidth(40);
                        circleReg.setFitHeight(40);
                        circleReg.setPreserveRatio(false);
                        Circle clip = new Circle(20, 20, 20);
                        circleReg.setClip(clip);
                        circle = circleReg;
                    }


                    Label usernameLabel = new Label(user.getId());
                    usernameLabel.getStyleClass().add("friend-username");

                    Label fullNameLabel = new Label(user.getFirstName() + " " + user.getLastName());
                    fullNameLabel.getStyleClass().add("friend-full-name");

                    VBox textContainer = new VBox(usernameLabel, fullNameLabel);
                    textContainer.getStyleClass().add("friend-info");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button unfriendButton = new Button("Unfriend");
                    unfriendButton.getStyleClass().add("unfriend-button");
                    unfriendButton.setOnAction(event -> {
                        currentSelectedUser = user;
                        deleteFriend(null);
                    });

                    HBox cellContainer = new HBox(circle, textContainer, spacer, unfriendButton);
                    cellContainer.getStyleClass().add("friend-container");
                    cellContainer.setSpacing(10);

                    setText(null);
                    setGraphic(cellContainer);
                }
            }
        });

        friendsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentSelectedUser = newValue;
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUsername() {
        profileUsernameLabel.setText(mainController.getUsername());
    }

    public void setProfileViewLoader(Runnable profileViewLoader) {
        this.profileViewLoader = profileViewLoader;
    }

    @FXML
    private void onBackButtonClicked() {
        if (profileViewLoader != null) {
            profileViewLoader.run();
        }
    }

    public void loadFriends() {
        List<User> friends = mainController.getSocialNetwork().getAllFriendsForUserList(mainController.getUsername());

        this.friends.setAll(friends);
    }

    public void deleteFriend(ActionEvent actionEvent) {
        if (currentSelectedUser != null) {
            mainController.getSocialNetwork().removeFriendship(mainController.getUsername(), currentSelectedUser.getId());
            loadFriends();
        }
    }

    public void addFriend(ActionEvent actionEvent) {
        mainController.loadAddFriendView();
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchBar.getText().trim();
        if (!searchTerm.isEmpty()) {
            List<User> users = searchUsers(searchTerm);
            friends.setAll(users);
        } else {
            loadFriends();
        }
    }

    private List<User> searchUsers(String searchTerm) {
        String searchTermLower = searchTerm.toLowerCase();

        return friends.stream()
                .filter(user -> {
                    String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                    return fullName.contains(searchTermLower);
                })
                .toList();
    }
}
