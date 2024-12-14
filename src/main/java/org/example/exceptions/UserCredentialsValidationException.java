package org.example.exceptions;

public class UserCredentialsValidationException extends RuntimeException {
    public UserCredentialsValidationException(String message) {
        super(message);
    }
}
