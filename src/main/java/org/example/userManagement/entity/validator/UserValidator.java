package org.example.userManagement.entity.validator;

import org.example.exceptions.UserValidationException;
import org.example.sharedInfrastructure.Validator;
import org.example.userManagement.entity.User;
import org.example.exceptions.DomainValidationException;

import java.util.ArrayList;
import java.util.List;


/**
 * Validator implementation for validating User entities.
 * Ensures that required fields such as first and last names are not empty.
 */
public class UserValidator implements Validator<User> {

    public UserValidator() {
    }

    @Override
    public void validate(User entity) throws DomainValidationException {
        List<String> errorMessage = new ArrayList<>();

        if (entity.getFirstName().isEmpty()) {
            errorMessage.add("First name can't be null!");
        }
        if (entity.getLastName().isEmpty()) {
            errorMessage.add("Last name can't be null!");
        }

        if (entity.getId() == null || entity.getId().trim().length() <= 0 || entity.getId().length() <= 1) {
            errorMessage.add("Invalid username. Username must have 2 or more characters!.");
        }
        if (!errorMessage.isEmpty()) {
            throw new UserValidationException(String.join("; ", errorMessage));
        }
    }
}
