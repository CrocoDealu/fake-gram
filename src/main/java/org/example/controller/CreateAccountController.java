package org.example.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.exceptions.DomainValidationException;
import org.example.exceptions.ServiceValidationException;
import org.example.exceptions.UserCredentialsValidationException;
import org.example.exceptions.UserValidationException;
import org.example.networkManagement.SocialNetwork;
import org.example.userManagement.entity.User;
import org.example.authentificationManagement.entity.UserCredential;

import java.util.Optional;
import java.util.Vector;

public class CreateAccountController {


    private TextField firstNameField;


    private TextField lastNameField;


    private TextField usernameField;


    private PasswordField passwordField;


    private Label firstNameErrorLabel;


    private Label lastNameErrorLabel;


    private Label usernameErrorLabel;


    private Label passwordErrorLabel;

    private LoginFormController loginFormController;

    private Button backButton;

    public CreateAccountController() {
    }

    public CreateAccountController(LoginFormController loginFormController, TextField firstNameField, TextField lastNameField, TextField createusernameField, PasswordField createpasswordField, Label firstNameErrorLabel, Label lastNameErrorLabel, Label usernameErrorLabel, Label passwordErrorLabel, Button backButton) {
        this.firstNameField = firstNameField;
        this.lastNameField = lastNameField;
        this.usernameField = createusernameField;
        this.passwordField = createpasswordField;
        this.firstNameErrorLabel = firstNameErrorLabel;
        this.lastNameErrorLabel = lastNameErrorLabel;
        this.usernameErrorLabel = usernameErrorLabel;
        this.passwordErrorLabel = passwordErrorLabel;
        this.loginFormController = loginFormController;
        this.backButton = backButton;
    }


    public void handleCreateUser() {
        SocialNetwork socialNetwork = loginFormController.getSocialNetwork();
        clearErrorMessages();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = new User(firstName, lastName, username, null, null);
        try {
            clearErrorMessages();
            Optional<User> userWId = socialNetwork.addUser(user);
            if (userWId.isPresent()) {
                user = userWId.get();
            }

            UserCredential credential = new UserCredential(username, password);
            socialNetwork.addUserCredential(credential);
            backButton.fire();
        } catch (UserValidationException e) {
            handleUserServiceError(e);
            UserCredential credential = new UserCredential(username, password);
            try {
                socialNetwork.addUserCredential(credential);
            } catch (UserCredentialsValidationException e1) {
                handleUserCredentialsError(e1);
            }

        } catch (UserCredentialsValidationException e) {
            handleUserCredentialsError(e);
            socialNetwork.removeUser(username);
        } catch (DomainValidationException e) {
            System.out.println(e.getMessage());
        } catch (ServiceValidationException e) {
            usernameErrorLabel.setText(e.getMessage());
        }
    }

    private void clearErrorMessages() {
        firstNameErrorLabel.setText("");
        lastNameErrorLabel.setText("");
        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        firstNameErrorLabel.setVisible(false);
        lastNameErrorLabel.setVisible(false);
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
    }

    private void handleUserServiceError(RuntimeException e) {
        firstNameErrorLabel.setVisible(true);
        lastNameErrorLabel.setVisible(true);
        String[] errors = e.getMessage().split(";");

        for (String error : errors) {
            if (error.contains("First name")) {
                firstNameErrorLabel.setText(error.trim());
            }
            if (error.contains("Last name")) {
                lastNameErrorLabel.setText(error.trim());
            }
        }
    }

    private void handleUserCredentialsError(RuntimeException e) {
        usernameErrorLabel.setVisible(true);
        passwordErrorLabel.setVisible(true);
        String[] errors = e.getMessage().split(";");
        Vector<String> usernameErrors = new Vector<>();
        Vector<String> passwordErrors = new Vector<>();
        for (String error : errors) {
            if (error.contains("Username")) {
                usernameErrors.add(error.trim());
            }
            if (error.contains("Password")) {
                passwordErrors.add(error.trim());
            }
        }

        usernameErrorLabel.setText(String.join("\n", usernameErrors));
        passwordErrorLabel.setText(String.join("\n", passwordErrors));
    }
}