package org.example.friendshipManagement.entity.validator;

import org.example.friendshipManagement.entity.Friendship;
import org.example.userManagement.entity.User;
import org.example.sharedInfrastructure.Validator;
import org.example.exceptions.DomainValidationException;
import org.example.sharedInfrastructure.Repository;

import java.util.Objects;
import java.util.Optional;

/**
 * Validator implementation for validating Friendship entities.
 * Ensures that the friendship is established between two different users
 * and that both users exist within the provided repository.
 */
public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Constructs a FriendshipValidator with access to the User repository.
    */
    public FriendshipValidator() {
    }

    @Override
    public void validate(Friendship entity) throws DomainValidationException {
        if (entity.getUsername1() == null || entity.getUsername2() == null)
            throw new DomainValidationException("The username can't be null! ");

        if (Objects.equals(entity.getUsername1(), entity.getUsername2()))
            throw new DomainValidationException("The username cant be the same");
    }
}
