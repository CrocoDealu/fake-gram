package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.example.utils.Pair;
import org.example.messagesManagement.entity.Message;
import org.example.userManagement.entity.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class ConversationsController {

    @FXML
    private VBox conversationContainer;

    @FXML
    private Label messageLabel;

    @FXML
    private ScrollPane scrollPane;

    private MainController mainController;
    private HashMap<String, ConversationController> conversationsControllers = new HashMap<>();

    @FXML
    public void initialize() {
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void loadConversations() {
        conversationContainer.getChildren().clear();

        Map<String, Vector<Message>> groupedMessages = mainController.getSocialNetwork()
                .getMessagesGroupedByUser(mainController.getUsername());

        Map<Pair<String, Pair<String, LocalDateTime>>, Message> allMessages = StreamSupport.stream(
                        mainController.getSocialNetwork().getMessages().spliterator(), false)
                .collect(Collectors.toMap(
                        message -> new Pair<>(message.getFromUsername(), new Pair<>(message.getToUsername(), message.getDateSend())),
                        message -> message
                ));

        groupedMessages.remove(mainController.getUsername());

        groupedMessages.forEach((username, messages) -> {
            messages.sort(Comparator.comparing(Message::getDateSend));
            addConversation(
                    username,
                    messages.getLast().getMessage(),
                    () -> openConversation(username, messages, allMessages)
            );
        });
    }

    private void addConversation(String username, String lastMessage, Runnable onClickAction) {
        HBox conversationBox = new HBox();
        conversationBox.setSpacing(10);
        conversationBox.getStyleClass().add("conversation-item");

        Optional<User> optionalUser = mainController.getSocialNetwork().findUser(username);
        User user = optionalUser.get();
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
        conversationBox.getChildren().add(circle);

        VBox textContainer = new VBox();
        textContainer.setSpacing(5);

        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("conversation-username");

        Label lastMessageLabel = new Label(lastMessage);
        lastMessageLabel.getStyleClass().add("conversation-message");

        textContainer.getChildren().addAll(nameLabel, lastMessageLabel);
        conversationBox.getChildren().add(textContainer);

        conversationBox.setOnMouseClicked(event -> onClickAction.run());

        conversationContainer.getChildren().add(conversationBox);
    }

    private void openConversation(String username, Vector<Message> messages, Map<Pair<String, Pair<String, LocalDateTime>>, Message> allMessages) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/conversation_view.fxml"));
            Parent root = loader.load();

            ConversationController controller = loader.getController();
            conversationsControllers.put(username, controller);
            controller.setUsername(username);
            messages.forEach(message -> {
                if (message.getReply() != null) {
                    Pair<String, Pair<String, LocalDateTime>> id = new Pair<>(
                            message.getReply().getFromUsername(),
                            new Pair<>(message.getReply().getToUsername(), message.getReply().getDateSend())
                    );
                    Message reply = allMessages.get(id);
                    if (reply != null) {
                        message.getReply().setMessage(reply.getMessage());
                    }
                }
            });
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/conversation_view_style.css")).toExternalForm());
            controller.setMessages(messages);
            controller.setMainController(mainController);


            mainController.loadConversation(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNewConversation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/create_conversation_view.fxml"));
            Parent root = loader.load();

            CreateConversationController controller = loader.getController();
            controller.setMainController(mainController);
            mainController.loadCreateNewConversation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConversationController getConversationController(String username) {
        return conversationsControllers.get(username);
    }

}