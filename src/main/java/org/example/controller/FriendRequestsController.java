package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.friendshipManagement.entity.Friendship;
import org.example.utils.FriendshipRequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class FriendRequestsController {

    @FXML
    public VBox friendRequestsBox;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private final ObservableList<FriendRequest> friendRequests = FXCollections.observableArrayList();
    private MainController mainController;

    @FXML
    public void initialize() {}


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void loadFriendRequests() {
        List<FriendRequest> requests = StreamSupport.stream(mainController.getSocialNetwork().getFriendRequests().spliterator(), false)
                .filter(friendRequest -> (Objects.equals(friendRequest.getToUsername(), mainController.getUsername()))
                        && Objects.equals(friendRequest.getStatus(), FriendshipRequestStatus.IN_PROGRESS.toString()))
                .toList();
        setFriendRequests(requests);
    }


    public void setFriendRequests(List<FriendRequest> requests) {
        friendRequestsBox.getChildren().clear();

        for (FriendRequest request : requests) {
            HBox requestItem = new HBox(10);
            requestItem.getStyleClass().add("request-item");
            String username = request.getFromUsername();

            Text message = new Text(username + " has requested to befriend you");

            message.getStyleClass().add("request-text");

            Button acceptButton = new Button("Accept");
            acceptButton.setOnAction(event -> handleAcceptRequest(request, username));

            Button rejectButton = new Button("Reject");
            rejectButton.setOnAction(event -> handleRejectRequest(request, username));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox buttonsContainer = new HBox(10);
            buttonsContainer.getChildren().addAll(acceptButton, rejectButton);
            acceptButton.getStyleClass().add("accept-button");
            rejectButton.getStyleClass().add("reject-button");

            requestItem.getChildren().addAll(message, spacer, buttonsContainer);

            friendRequestsBox.getChildren().add(requestItem);
        }
    }

    private void handleAcceptRequest(FriendRequest request, String username) {
        mainController.getSocialNetwork().addFriendship(new Friendship(request.getFromUsername(), request.getToUsername(), LocalDateTime.now()));
        mainController.getSocialNetwork().setStatusForRequest(request, FriendshipRequestStatus.ACCEPTED.toString());
        removeRequest(request, username);
    }

    private void handleRejectRequest(FriendRequest request, String username) {
        mainController.getSocialNetwork().setStatusForRequest(request, FriendshipRequestStatus.DECLINED.toString());
        removeRequest(request, username);
    }

    private void removeRequest(FriendRequest request, String username) {
        friendRequestsBox.getChildren().removeIf(node ->
                ((HBox) node).getChildren().getFirst().toString().contains(username));
    }

    public void handleBackAction(ActionEvent actionEvent) {
        mainController.loadProfile(null);
    }
}
