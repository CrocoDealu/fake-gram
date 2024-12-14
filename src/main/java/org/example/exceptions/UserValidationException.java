package org.example.exceptions;

public class UserValidationException extends DomainValidationException{
    public UserValidationException() {
    }

    public UserValidationException(String message) {
        super(message);
    }
}
