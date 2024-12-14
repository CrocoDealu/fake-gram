package org.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.messagesManagement.entity.Message;
import org.example.utils.Event;
import org.example.networkManagement.SocialNetwork;
import org.example.userManagement.entity.User;
import org.example.utils.Events;
import org.example.utils.Pair;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class MainController {


    @FXML
    public HBox bottomPanel;

    @FXML
    public Button searchButton;

    @FXML
    private StackPane contentPane;

    @FXML
    private Button newsFeedButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button messagesButton;

    @FXML
    private HBox topPanel;

    @FXML
    public Button friendRequestsButton;

    private Button selectedButton;

    @FXML
    private BorderPane mainContainer;

    private FXMLLoader profileLoader;
    private FXMLLoader messagesLoader;
    private FXMLLoader newsLoader;
    private FXMLLoader friendSearchLoader;
    private FXMLLoader friendRequestLoader;
    private FXMLLoader friendConversationLoader;
    private FXMLLoader changeProfileLoader;
    private Parent profileView;
    private Node messagesView;
    private Node newsView;
    private Node friendSearchView;
    private Node friendRequestView;
    private Node friendConversationView;
    private Node changeProfileView;

    private CreateConversationController createConversationController;
    private SocialNetwork socialNetwork;
    private User user;
    private String username;
    private EventController eventController;
    private static final String IMAGES_DIR = "/images/";

    @FXML
    public void initialize() throws IOException {
        profileLoader = new FXMLLoader(getClass().getResource("/org/example/profile_view.fxml"));
        profileView = profileLoader.load();

        messagesLoader = new FXMLLoader(getClass().getResource("/org/example/conversations_view.fxml"));
        messagesView = messagesLoader.load();
        ConversationsController conversationsController = messagesLoader.getController();
        conversationsController.setMainController(this);

        friendSearchLoader = new FXMLLoader(getClass().getResource("/org/example/add_friend_view.fxml"));
        friendSearchView = friendSearchLoader.load();

        friendRequestLoader = new FXMLLoader(getClass().getResource("/org/example/friend_request_view.fxml"));
        friendRequestView = friendRequestLoader.load();

        friendConversationLoader = new FXMLLoader(getClass().getResource("/org/example/create_conversation_view.fxml"));
        friendConversationView = friendConversationLoader.load();
        createConversationController = friendConversationLoader.getController();

        changeProfileLoader = new FXMLLoader(getClass().getResource("/org/example/change_profile_view.fxml"));
        changeProfileView = changeProfileLoader.load();

        startPeriodicTask();

        showFriendRequestPopup(new Stage(), "username");
    }

    public void initializeResources() {
        friendRequestsButton.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main_view_style.css")).toExternalForm());

        addIconToButton(friendRequestsButton, "/icons/heart.png");
        addIconToButton(messagesButton, "/icons/messages.png");
        addIconToButton(profileButton, "/icons/user.png");
        addIconToButton(searchButton, "/icons/loupe.png");
    }

    private void addIconToButton(Button button, String iconPath) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(25);
        iconView.setFitHeight(25);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(-1);
        colorAdjust.setBrightness(1);

        iconView.setEffect(colorAdjust);

        button.setGraphic(iconView);
    }

    public void loadConversations(ActionEvent actionEvent) {
        showMenuBar();
        contentPane.getChildren().setAll(messagesView);
        changeSelectedState();
        messagesButton.getStyleClass().add("icon-button-active");
        selectedButton = messagesButton;
        ConversationsController conversationsController = messagesLoader.getController();
        conversationsController.setMainController(this);
        conversationsController.loadConversations();
    }

    public void loadProfile(ActionEvent actionEvent) {
        loadProfile();
    }

    public void loadProfile() {
        try {
            showMenuBar();
            contentPane.getChildren().setAll(profileView);

            ProfileController profileController = profileLoader.getController();
            profileController.setFriendsViewLoader(this::loadFriendsView);
            profileController.setMainController(this);
            profileController.setProfilePicture(user.getProfileImage());
            profileController.setBio(user.getBio());
            changeSelectedState();
            profileButton.getStyleClass().add("icon-button-active");
            profileController.setUsername(user.getFirstName() + " " + user.getLastName());
            selectedButton = profileButton;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConversation(Parent root) {
        contentPane.getChildren().setAll(root);
        hideMenuBar();
    }

    public void loadFriendsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/friends_view.fxml"));
            Parent friendsView = loader.load();

            FriendsViewController friendsController = loader.getController();
            friendsController.setProfileViewLoader(this::loadProfile);
            friendsController.setMainController(this);
            contentPane.getChildren().setAll(friendsView);
            friendsController.setMainController(this);
            friendsController.loadFriends();
            friendsController.setUsername();
            hideMenuBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFriendRequestView() {
        contentPane.getChildren().setAll(friendRequestView);
        FriendRequestsController friendRequestsController = friendRequestLoader.getController();
        friendRequestsController.setMainController(this);
        friendRequestsController.loadFriendRequests();
        changeSelectedState();
        friendRequestsButton.getStyleClass().add("icon-button-active");
        selectedButton = friendRequestsButton;
    }

    public void loadAddFriendView() {
        try {
            changeSelectedState();

            contentPane.getChildren().setAll(friendSearchView);
            searchButton.getStyleClass().add("icon-button-active");
            selectedButton = searchButton;
            AddFriendController addFriendController = friendSearchLoader.getController();
            addFriendController.setMainController(this);
            addFriendController.loadUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadChangeProfile() {
        try {

            ChangeProfileController controller = changeProfileLoader.getController();
            controller.setMainController(this);
            controller.initializeResources();
            changeProfileView.setTranslateY(mainContainer.getHeight());

            mainContainer.getChildren().add(changeProfileView);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), changeProfileView);
            slideIn.setFromY(mainContainer.getHeight());
            slideIn.setToY(0);

            slideIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCreateNewConversation() {
        contentPane.getChildren().setAll(friendConversationView);
        createConversationController.setMainController(this);
        createConversationController.loadUsers();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SocialNetwork getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void setUser(String username) {
        this.username = username;
        socialNetwork.findUser(username).ifPresent(u -> user = u);

    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void startPeriodicTask() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(15), event -> {
                    performTask();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void performTask() {
        if (socialNetwork.friendshipRequestCount() > 0) {
            socialNetwork.removeAnsweredRequests();
            System.out.println("Task executed at: " + System.currentTimeMillis());
        }
    }

    public void showFriendRequestPopup(Stage ownerStage, String username) {
        Popup popup = new Popup();

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 5;");

        Label message = new Label(username + " sent you a friend request!");
//        Button acceptButton = new Button("Accept");
//        Button rejectButton = new Button("Reject");
//
//        acceptButton.setOnAction(e -> {
//            System.out.println("Friend request accepted!");
//            popup.hide(); // Close the popup
//        });
//        rejectButton.setOnAction(e -> {
//            System.out.println("Friend request rejected!");
//            popup.hide(); // Close the popup
//        });

        content.getChildren().addAll(message);

        popup.getContent().add(content);

        popup.setAutoHide(true);

        popup.show(ownerStage,
                ownerStage.getX() + ownerStage.getWidth() / 2,
                ownerStage.getY() + ownerStage.getHeight() / 3
        );
    }


    public void handleEvent(Event event) {
        switch (event.getEventName()) {
            case Events.New_Friend_Request -> {
                Stage activeStage = (Stage) profileButton.getScene().getWindow();
                showFriendRequestPopup(
                        activeStage,
                        event.getParameters().getFirst()
                );
            }
            case Events.Message_Sent -> {
                ConversationsController conversationsController = messagesLoader.getController();
                ConversationController conversationController = conversationsController.getConversationController(event.getParameters().get(1));
                if (conversationController != null) {
                    conversationController.setMainController(this);
                    Pair<String, Pair<String, LocalDateTime>> id = new Pair<>(event.getParameters().get(1), new Pair<>(event.getParameters().getFirst(), LocalDateTime.parse(event.getParameters().get(2))));
                    Optional<Message> optionalMessage = socialNetwork.getMessageById(id);
                    Message message = optionalMessage.orElse(null);
                    if (message != null && message.getReply() != null) {
                        Message reply = message.getReply();
                        reply.setMessage(event.getParameters().get(3));
                        message.setReply(reply);
                    }
                    conversationController.addMessage(message);
                }
            }
            default -> {
                System.out.println("Unknown event: ");
            }
        }
    }

    public void propagateEvent(Event event) {
        eventController.handleEvent(event);
    }

    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }

    public void closeScene() {
        Stage stage = (Stage) profileButton.getScene().getWindow();
        stage.close();
    }

    private void changeSelectedState() {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("icon-button-active");
        }
    }

    private void hideMenuBar() {
        if (bottomPanel.isVisible()) {
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }
    }

    private void showMenuBar() {
        if (!bottomPanel.isVisible()) {
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }
    }

    public void closeChangeProfile() {

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), changeProfileView);
        slideOut.setFromY(0);
        slideOut.setToY(mainContainer.getHeight());

        slideOut.setOnFinished(e -> mainContainer.getChildren().remove(changeProfileView));

        slideOut.play();
    }

    public void logout() {
        eventController.removeController(this);
        Stage stage = (Stage) contentPane.getScene().getWindow();
        stage.close();
    }
}
