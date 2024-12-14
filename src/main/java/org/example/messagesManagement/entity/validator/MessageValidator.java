package org.example.messagesManagement.entity.validator;

import org.example.exceptions.DomainValidationException;
import org.example.messagesManagement.entity.Message;
import org.example.sharedInfrastructure.Validator;

import java.util.Objects;

public class MessageValidator implements Validator<Message> {

    public MessageValidator() {
    }

    @Override
    public void validate(Message entity) throws DomainValidationException {
        if (entity.getFromUsername() == null || entity.getToUsername() == null)
            throw new DomainValidationException("The id can't be null! ");

        if (Objects.equals(entity.getFromUsername(), entity.getToUsername()))
            throw new DomainValidationException("The id cant be the same");

        if (entity.getMessage() == null || entity.getMessage().trim().isEmpty())
            throw new DomainValidationException("The message can't be empty");
    }

}
