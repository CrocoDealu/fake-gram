package org.example.authentificationManagement.entity.validator;

import org.example.authentificationManagement.entity.UserCredential;
import org.example.exceptions.DomainValidationException;
import org.example.exceptions.UserCredentialsValidationException;
import org.example.sharedInfrastructure.Validator;

import java.util.ArrayList;
import java.util.List;

public class CredentialsValidator implements Validator<UserCredential> {

    public CredentialsValidator() {}

    @Override
    public void validate(UserCredential entity) throws DomainValidationException {
        List<String> errors = new ArrayList<>();

        if (entity == null) {
            throw new DomainValidationException("UserCredentials cannot be null.");
        }

        // Validate ID


        // Validate Username
        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            errors.add("Username cannot be null or empty.");
        } else if (entity.getUsername().length() < 3 || entity.getUsername().length() > 50) {
            errors.add("Username must be between 3 and 50 characters.");
        }

        // Validate Password
        if (entity.getPassword() == null || entity.getPassword().trim().isEmpty()) {
            errors.add("Password cannot be null or empty.");
        } else {
            if (entity.getPassword().length() < 8) {
                errors.add("Password must be at least 8 characters long.");
            }
            if (!containsUppercase(entity.getPassword())) {
                errors.add("Password must contain at least one uppercase letter.");
            }
            if (!containsLowercase(entity.getPassword())) {
                errors.add("Password must contain at least one lowercase letter.");
            }
            if (!containsDigit(entity.getPassword())) {
                errors.add("Password must contain at least one digit.");
            }
        }

        if (!errors.isEmpty()) {
            throw new UserCredentialsValidationException(String.join("; ", errors));
        }
    }

    private boolean containsUppercase(String str) {
        return str.chars().anyMatch(Character::isUpperCase);
    }

    private boolean containsLowercase(String str) {
        return str.chars().anyMatch(Character::isLowerCase);
    }

    private boolean containsDigit(String str) {
        return str.chars().anyMatch(Character::isDigit);
    }
}
