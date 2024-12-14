package org.example.friendRequestManagement.entity.validator;

import org.example.exceptions.DomainValidationException;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.sharedInfrastructure.Validator;

import java.util.Objects;

public class FriendRequestValidator implements Validator<FriendRequest> {

    public FriendRequestValidator() {
    }

    @Override
    public void validate(FriendRequest entity) throws DomainValidationException {
        if (entity.getFromUsername() == null || entity.getToUsername() == null)
            throw new DomainValidationException("The usernames can't be null! ");

        if (Objects.equals(entity.getFromUsername(), entity.getToUsername()))
            throw new DomainValidationException("The usernames cant be the same");
    }
}
