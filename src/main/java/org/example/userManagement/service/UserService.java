package org.example.userManagement.service;

import org.example.userManagement.entity.User;
import org.example.sharedInfrastructure.Repository;
import org.example.userManagement.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns an iterable collection of all users in the social network.
     *
     * @return all users from the user repository
     */
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds and returns a user by their ID.
     *
     * @param username the ID of the user to be found
     * @return the user with the given ID, or null if no user is found
     */
    public Optional<User> findUser(String username) {
        return userRepository.findOne(username);
    }

    /**
     * Adds a new user to the social network.
     * Assigns a new unique ID to the user before saving.
     *
     * @param u the user to be added
     */
    public Optional<User> addUser(User u) {
        return userRepository.save(u);
    }

    /**
     * Removes a user by their ID, along with all associated friendships.
     * If the user doesn't exist, an error message is printed.
     *
     * @param username the ID of the user to be removed
     * @return the removed user, or null if the user does not exist
     */
    public Optional<User> removeUser(String username) {
        try {
            Optional<User> u = Optional.ofNullable(userRepository.findOne(username).orElseThrow(() -> new IllegalArgumentException("The user does not exist")));
            return userRepository.delete(username);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }
        return Optional.empty();
    }

    public Optional<User> updateUser(User u) {
        return userRepository.update(u);
    }
}
