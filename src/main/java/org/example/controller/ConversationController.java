package org.example.controller;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.messagesManagement.entity.Message;
import org.example.utils.Event;
import org.example.utils.Events;

import java.time.LocalDateTime;
import java.util.*;

public class ConversationController {

    @FXML
    private Label usernameLabel;

    @FXML
    private ListView<Message> messageListView;

    @FXML
    private HBox selectedMessageBox;

    @FXML
    private Label selectedMessageLabel;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textArea;

    private Message selectedMessage;

    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private MainController mainController;
    private String toUsername;



    @FXML
    public void initialize() {
        messages = FXCollections.observableArrayList();
        messageListView.setItems(messages);
        messageListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox();

                    VBox messageBox = new VBox(5);

                    HBox repliedMessageHBox;
                    Label repliedMessageLabel = null;
                    if (message.getReply() != null) {
                        repliedMessageHBox = new HBox();
                        repliedMessageLabel = new Label(message.getReply().getMessage());

                        if (Objects.equals(message.getReply().getFromUsername(), mainController.getUsername())) {
                            repliedMessageHBox.getStyleClass().add("replied-message-sent");
                        } else {
                            repliedMessageHBox.getStyleClass().add("replied-message-received");
                        }
                        repliedMessageHBox.getChildren().add(repliedMessageLabel);
                        messageBox.getChildren().add(repliedMessageHBox);
                    }

                    HBox mainMessageHBox = new HBox();
                    Label messageLabel = new Label(message.getMessage());
                    messageLabel.setWrapText(true);
                    mainMessageHBox.getChildren().add(messageLabel);

                    messageBox.getChildren().add(mainMessageHBox);

                    if (Objects.equals(message.getFromUsername(), mainController.getUsername())) {
                        mainMessageHBox.getStyleClass().add("message-sent");
                        container.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        mainMessageHBox.getStyleClass().add("message-received");
                        container.setAlignment(Pos.CENTER_LEFT);
                    }

                    messageBox.setMaxWidth(getListView().getWidth() * 0.8);
                    messageLabel.maxWidthProperty().bind(getListView().widthProperty().multiply(0.8));

                    getListView().widthProperty().addListener((obs, oldWidth, newWidth) -> {
                        messageBox.setMaxWidth(newWidth.doubleValue() * 0.8);
                    });

                    container.getChildren().add(messageBox);
                    setGraphic(container);

                    pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
                    pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
                }
            }
        });

        messageListView.getSelectionModel().selectedItemProperty().addListener((obs, oldMessage, newMessage) -> {
            if (newMessage != null) {
                selectedMessage = newMessage;
                showSelectedMessage(selectedMessage);
            }
        });

        selectedMessageBox.setManaged(false);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
        toUsername = username;
    }

    public void addMessage(Message message) {
        messageListView.getItems().add(message);
        messageListView.scrollTo(messageListView.getItems().size() - 1);
    }

    public void handleButtonClicked(ActionEvent actionEvent) {
        String message = textArea.getText();
        textArea.setText("");
        Message m = new Message(mainController.getUsername(), toUsername, message, LocalDateTime.now());
        String repliedMessage = "";
        if (selectedMessage != null) {
            m.setReply(selectedMessage);
            repliedMessage = m.getReply().getMessage();
        }
        mainController.getSocialNetwork().addMessage(m);
        addMessage(m);
        mainController.propagateEvent(new Event(Events.Message_Sent, List.of(toUsername, mainController.getUsername(), m.getDateSend().toString(), repliedMessage)));
        selectedMessage = null;
        if (selectedMessageBox.isVisible()) {
            hideSelectedMessage();
        }
    }

    public void setMessages(Vector<Message> messages) {
        this.messages.setAll(messages);
        messageListView.scrollTo(messageListView.getItems().size() - 1);
    }

    public void handleBackButtonClicked(ActionEvent actionEvent) {
        mainController.loadConversations(null);
    }

    public void showSelectedMessage(Message message) {
        selectedMessageBox.setManaged(true);
        selectedMessageBox.setVisible(true);
        selectedMessageBox.setTranslateY(50);
        selectedMessageLabel.setVisible(true);
        selectedMessageLabel.setText(message.getMessage());
        if (Objects.equals(message.getFromUsername(), mainController.getUsername())) {
            selectedMessageLabel.getStyleClass().add("replied-message-sent");
        } else {
            selectedMessageLabel.getStyleClass().add("replied-message-received");
        }

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), selectedMessageBox);
        slideUp.setFromY(50);
        slideUp.setToY(0);
        slideUp.play();
    }

    public void hideSelectedMessage() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), selectedMessageBox);
        slideDown.setFromY(0);
        slideDown.setToY(50);
        slideDown.setOnFinished(event -> selectedMessageBox.setVisible(false));
        slideDown.play();
        selectedMessage = null;
        selectedMessageBox.setManaged(false);

        selectedMessageLabel.getStyleClass().remove("replied-message-sent");
        selectedMessageLabel.getStyleClass().remove("replied-message-received");
    }

}