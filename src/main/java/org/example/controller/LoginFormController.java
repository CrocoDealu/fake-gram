package org.example.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.utils.Pair;
import org.example.authentificationManagement.entity.UserCredential;
import org.example.authentificationManagement.entity.validator.CredentialsValidator;
import org.example.authentificationManagement.repository.UserCredentialsDBRepository;
import org.example.authentificationManagement.service.UserAuthentificationService;
import org.example.friendRequestManagement.entity.validator.FriendRequestValidator;
import org.example.friendRequestManagement.repository.FriendRequestDBRepository;
import org.example.friendRequestManagement.service.FriendRequestService;
import org.example.friendshipManagement.entity.validator.FriendshipValidator;
import org.example.friendshipManagement.repository.FriendshipDBRepository;
import org.example.friendshipManagement.service.FriendshipService;
import org.example.messagesManagement.entity.validator.MessageValidator;
import org.example.messagesManagement.repository.MessageDBRepository;
import org.example.messagesManagement.service.MessagesService;
import org.example.networkManagement.SocialNetwork;
import org.example.userManagement.entity.User;
import org.example.userManagement.entity.validator.UserValidator;
import org.example.userManagement.service.UserService;
import org.example.userManagement.repository.UserDBRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class LoginFormController {

    private SocialNetwork socialNetwork;



    @FXML
    private VBox createAccountPane;

    @FXML
    public Label appNameLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private Label usernameFieldError;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordFieldError;

    @FXML
    private Button loginButton;


    @FXML
    private Button closeButton;


    @FXML
    private Button createAccountButton;

    //create account
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField createusernameField;

    @FXML
    private PasswordField createpasswordField;

    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    public Button backButton;


    private final HashMap<String, Pair<Double, Double>> offsets = new HashMap<>();
    private EventController eventController;
    private CreateAccountController createAccountController;

    public LoginFormController() {

    }

    @FXML
    private void handleLogin() {
        clearErrors();
        String username = usernameField.getText();
        String password = passwordField.getText();

        Optional<UserCredential> token = socialNetwork.findCredential(username);
        if (token.isPresent()) {
            if (password.equals(token.get().getPassword())) {
                Optional<User> user = socialNetwork.findUser(token.get().getId());
                user.ifPresent(this::showApplication);
            } else {
                passwordFieldError.setVisible(true);
                passwordFieldError.setText("Wrong password");
            }
        } else {
            usernameFieldError.setVisible(true);
            usernameFieldError.setText("Invalid username");
        }
    }

    private void showApplication(User user) {
        usernameField.setText("");
        passwordField.setText("");
        offsets.put(user.getId(), new Pair<>(0.0, 0.0));
        initializeScene(user);
    }

    private void initializeScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/main_view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setSocialNetwork(socialNetwork);
            mainController.setUser(user.getId());
            mainController.setEventController(eventController);
            eventController.addController(mainController);
            Stage currentStage = new Stage();


            Scene scene = new Scene(root, 500, 600);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main_view_style.css")).toExternalForm());
            enableWindowDragging(user.getId(), currentStage, root);

            currentStage.initStyle(StageStyle.TRANSPARENT);
            currentStage.setTitle("Main Application");
            currentStage.setScene(scene);
            currentStage.show();


            mainController.loadProfile(null);
            mainController.initializeResources();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateAccount() {
        if (createAccountPane != null) {
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), createAccountPane);
            slideIn.setFromY(createAccountPane.getScene().getHeight() - createAccountPane.getHeight() / 10);
            slideIn.setToY(0);
            slideIn.play();
            createAccountController();
        } else {
            System.err.println("Create Account Pane is not initialized. Check FXML bindings.");
        }
    }

    private void createAccountController() {
        if (createAccountController == null) {
            createAccountController = new CreateAccountController(this, firstNameField, lastNameField, createusernameField, createpasswordField, firstNameErrorLabel, lastNameErrorLabel, usernameErrorLabel, passwordErrorLabel, backButton);
        }
    }



    @FXML
    private void handleBackToLogin() {
        if (createAccountPane != null) {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), createAccountPane);
            slideOut.setToY(createAccountPane.getScene().getHeight());
            slideOut.play();
        }
    }

    public SocialNetwork getSocialNetwork() {
        return socialNetwork;
    }

    public void clearErrors() {
        usernameFieldError.setText("");
        usernameFieldError.setVisible(false);
        passwordFieldError.setText("");
        passwordFieldError.setVisible(false);
    }

    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }

    @FXML
    public void handleClose(ActionEvent actionEvent) {
        eventController.closeAll();

        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Enables dragging of the custom transparent window.
     */
    private void enableWindowDragging(String username, Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            offsets.get(username).setFirst(event.getSceneX());
            offsets.get(username).setSecond(event.getSceneY());
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offsets.get(username).getFirst());
            stage.setY(event.getScreenY() - offsets.get(username).getSecond());
        });
    }


    public void handleCreateUser(ActionEvent actionEvent) {
        createAccountController.handleCreateUser();
    }

    public void initializeResources(String url, String user, String password) {
        UserDBRepository userDBRepository = new UserDBRepository(new UserValidator(), url, user, password);
        UserService userService = new UserService(userDBRepository);
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(new FriendshipValidator(), url, user, password);
        FriendshipService friendshipService = new FriendshipService(friendshipDBRepository);
        UserAuthentificationService userAuthentificationService = new UserAuthentificationService(new UserCredentialsDBRepository(new CredentialsValidator(), url, user, password));
        FriendRequestService friendRequestService = new FriendRequestService(new FriendRequestDBRepository(new FriendRequestValidator(), url, user, password));
        MessagesService messagesService = new MessagesService(new MessageDBRepository(new MessageValidator(), url, user, password));
        this.socialNetwork = new SocialNetwork(userService, friendshipService, userAuthentificationService, friendRequestService, messagesService);
    }
}
