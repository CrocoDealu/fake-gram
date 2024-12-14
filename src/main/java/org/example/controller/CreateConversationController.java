package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.example.messagesManagement.entity.Message;
import org.example.userManagement.entity.User;

import java.io.IOException;
import java.util.*;

public class CreateConversationController {

    @FXML
    public Button backButton;

    @FXML
    public TextField searchBar;

    @FXML
    public ListView<User> friendsListView;

    @FXML
    private Button addFriendButton;

    private User currentSelectedUser;
    private final ObservableList<User> friends = FXCollections.observableArrayList();
    private MainController mainController;

    @FXML
    public void initialize() {
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

                    Button message = new Button("Message");
                    message.getStyleClass().add("unfriend-button");
                    message.setOnAction(event -> {
                        currentSelectedUser = user;
                        addConversation(currentSelectedUser);
                    });

                    HBox cellContainer = new HBox(circle, textContainer, spacer, message);
                    cellContainer.getStyleClass().add("friend-container");
                    cellContainer.setSpacing(10);

                    setText(null);
                    setGraphic(cellContainer);
                }
            }
        });

        friendsListView.setItems(friends);
        friendsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentSelectedUser = newValue;
        });
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchBar.getText().trim();
        if (!searchTerm.isEmpty()) {
            List<User> users = searchUsers(searchTerm);
            friends.setAll(users);
        } else {
            loadUsers();
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

    public void loadUsers() {
        Map<String, Vector<Message>> groupedMessages = mainController.getSocialNetwork()
                .getMessagesGroupedByUser(mainController.getUsername());
        groupedMessages.remove(mainController.getUsername());
        List<User> friendList = mainController.getSocialNetwork().getAllFriendsForUserList(mainController.getUsername());
        List<User> notMessagedFriends = friendList.stream()
                .filter(user -> !groupedMessages.containsKey(user.getId()))
                .toList();

        friends.setAll(notMessagedFriends);
    }

    private void addConversation(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/conversation_view.fxml"));
            Parent root = loader.load();

            ConversationController controller = loader.getController();

            controller.setUsername(user.getId());
            controller.setMessages(new Vector<>());
            controller.setMainController(mainController);
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/conversation_view_style.css")).toExternalForm());
            mainController.loadConversation(root);
        } catch (IOException e) {
            e.printStackTrace();
        };
    }

    public void handleBack(ActionEvent actionEvent) {
        mainController.loadConversations(null);
    }

}
