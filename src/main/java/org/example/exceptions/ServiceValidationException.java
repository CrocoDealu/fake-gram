package org.example.exceptions;

public class ServiceValidationException extends RuntimeException {
    public ServiceValidationException(String message) {
        super(message);
    }
}
