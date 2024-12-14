package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.example.utils.Event;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.userManagement.entity.User;
import org.example.utils.Events;
import org.example.utils.FriendshipRequestStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class AddFriendController {

    @FXML
    public Button backButton;

    @FXML
    public TextField searchField;

    @FXML
    private ListView<User> resultsListView;

    @FXML
    private Button addFriendButton;

    private final ObservableList<User> searchResults = FXCollections.observableArrayList();
    private MainController mainController;
    private User selectedUser;
    @FXML
    public void initialize() {

        resultsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Node circle;
                    if (user.getProfileImage() == null || user.getProfileImage().isEmpty()) {
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

                    Button sendRequest = new Button("Send Request");
                    sendRequest.getStyleClass().add("request-button");
                    sendRequest.setOnAction(event -> {
                        sendRequest.setText("Requested");
                        sendRequest.setDisable(true);
                        handleSendFriendRequest(user);
                    });

                    HBox cellContainer = new HBox(circle, textContainer, spacer, sendRequest);
                    cellContainer.getStyleClass().add("friend-container");
                    cellContainer.setSpacing(10);

                    setText(null);
                    setGraphic(cellContainer);
                }
            }
        });

        resultsListView.setItems(searchResults);
        resultsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedUser = newValue;
            if (selectedUser != null) {
                System.out.println("Selected User: " + selectedUser.getId());
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            List<User> users = searchUsers(searchTerm);
            searchResults.setAll(users);
        } else {
            loadUsers();
        }
    }

    @FXML
    private void handleSendFriendRequest(User selectedUser) {
        if (selectedUser != null) {
            boolean hasSent = sendFriendSequest(selectedUser);
            if (hasSent) {
                mainController.propagateEvent(new Event(Events.New_Friend_Request, new ArrayList<>(Arrays.asList(selectedUser.getId(), mainController.getUsername()))));
            }
        }
    }

    private List<User> searchUsers(String searchTerm) {
        loadUsers();
        String searchTermLower = searchTerm.toLowerCase();
        return StreamSupport.stream(((Iterable<User>) searchResults).spliterator(), false)
                .filter(user -> (user.getFirstName().toLowerCase().contains(searchTermLower) || user.getLastName().toLowerCase().contains(searchTermLower)) && !Objects.equals(user.getId(), mainController.getUsername()))
                .toList();
    }

    public void loadUsers() {

        Map<String, Boolean> friendships = mainController.getSocialNetwork().getAllFriendsForUserMap(mainController.getUsername());

        Map<String, Boolean> friendRequests = mainController.getSocialNetwork().getAllFriendRequestsForUserMap(mainController.getUsername());

        List<User> nonFriends = StreamSupport.stream(mainController.getSocialNetwork().getUsers().spliterator(), false).filter(
                user -> !Objects.equals(user.getId(), mainController.getUsername()) && !friendships.containsKey(user.getId()) && !friendRequests.containsKey(user.getId())
        ).toList();
        searchResults.setAll(nonFriends);
    }

    private boolean sendFriendSequest(User user) {
        Optional<FriendRequest> friendRequest = mainController.getSocialNetwork().addFriendshipRequest(new FriendRequest(mainController.getUsername(), user.getId(), FriendshipRequestStatus.IN_PROGRESS.toString(), LocalDateTime.now()));
        return friendRequest.isPresent();
    }

}

