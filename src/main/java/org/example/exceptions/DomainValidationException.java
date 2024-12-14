package org.example.exceptions;

public class DomainValidationException extends RuntimeException{

    public DomainValidationException() {
        super();
    }

    public DomainValidationException(String message) {
        super(message);
    }
}
