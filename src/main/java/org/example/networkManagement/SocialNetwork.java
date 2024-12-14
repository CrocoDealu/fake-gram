package org.example.networkManagement;

import org.example.utils.Pair;
import org.example.authentificationManagement.entity.UserCredential;
import org.example.authentificationManagement.service.UserAuthentificationService;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.friendRequestManagement.service.FriendRequestService;
import org.example.friendshipManagement.entity.Friendship;
import org.example.friendshipManagement.service.FriendshipService;
import org.example.messagesManagement.entity.Message;
import org.example.messagesManagement.service.MessagesService;
import org.example.userManagement.entity.User;
import org.example.exceptions.ServiceValidationException;
import org.example.userManagement.service.UserService;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class SocialNetwork {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final UserAuthentificationService userAuthentificationService;
    private final FriendRequestService friendRequestService;
    private final MessagesService messagesService;
    /**
     * Constructor for SocialNetwork.
     *
     * @param userService the repository for managing users
     * @param friendshipService the repository for managing friendships
     */
    public SocialNetwork(UserService userService, FriendshipService friendshipService, UserAuthentificationService userAuthentificationService, FriendRequestService friendRequestService, MessagesService messagesService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.userAuthentificationService = userAuthentificationService;
        this.friendRequestService = friendRequestService;
        this.messagesService = messagesService;
    }

    /**
     * Returns an iterable collection of all users in the social network.
     *
     * @return all users from the user repository
     */
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * Returns an iterable collection of all friendships in the social network.
     *
     * @return all friendships from the friendship repository
     */
    public Iterable<Friendship> getFriendships() {
        return friendshipService.getFriendships();
    }

    // CRUD for users

    /**
     * Finds and returns a user by their ID.
     *
     * @param username the ID of the user to be found
     * @return the user with the given ID, or null if no user is found
     */
    public Optional<User> findUser(String username) {
        return userService.findUser(username);
    }

    /**
     * Adds a new user to the social network.
     * Assigns a new unique ID to the user before saving.
     *
     * @param u the user to be added
     */
    public Optional<User> addUser(User u) {
        return userService.addUser(u);
    }

    /**
     * Removes a user by their ID, along with all associated friendships.
     * If the user doesn't exist, an error message is printed.
     *
     * @param id the ID of the user to be removed
     * @return the removed user, or null if the user does not exist
     */
    public Optional<User> removeUser(String username) {
        try {
            friendshipService.removeFriendshipsForUser(username);
            return userService.removeUser(username);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }
        return Optional.empty();
    }

    public Optional<User> updateUser(User u) {
        return userService.updateUser(u);
    }

    // CRUD for friendships

    /**
     * Adds a new friendship between two users to the social network.
     * Ensures that the friendship does not already exist before saving.
     *
     * @param friendship the friendship to be added
     * @throws ServiceValidationException if the friendship already exists
     */
    public void addFriendship(Friendship friendship) {
        friendshipService.addFriendship(friendship);
    }

    /**
     * Removes an existing friendship between two users.
     * Ensures that the friendship exists before attempting to remove it.
     *
     * @param username1 the ID of the first user in the friendship
     * @param username2 the ID of the second user in the friendship
     * @throws IllegalArgumentException if the friendship does not exist
     */
    public void removeFriendship(String username1, String username2) {
        Pair<String, String> id = new Pair<>(username1, username2);
        friendshipService.removeFriendship(id);
    }

    public Pair<List<User>, Page<Friendship>> getPage(String username, int pageNumber, int pageSize) {
        Page<Friendship> pageOfFriendships = friendshipService.getFriendsForUsername(username, new Pageable(pageNumber, pageSize));
        List<Friendship> friendships = friendshipService.convertIterableToList(pageOfFriendships.getElementsOnPage());
        return new Pair<>(friendships.stream()
                .flatMap(friendship -> Stream.of(
                        friendship.getUsername1(),
                        friendship.getUsername2()
                ))
                .filter(iusername -> !iusername.equals(username))
                .map(this::findUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .toList(), pageOfFriendships);
    }

    public List<User> getAllFriendsForUserList(String username) {
        Page<Friendship> pageOfFriendships = friendshipService.getFriendsForUsername(username, null);
        List<Friendship> friendships = friendshipService.convertIterableToList(pageOfFriendships.getElementsOnPage());
        return friendships.stream()
                .flatMap(friendship -> Stream.of(
                        friendship.getUsername1(),
                        friendship.getUsername2()
                ))
                .filter(iusername -> !iusername.equals(username))
                .map(this::findUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .toList();
    }

    public Map<String, Boolean> getAllFriendsForUserMap(String username) {
        Page<Friendship> pageOfFriendships = friendshipService.getFriendsForUsername(username, null);
        return friendshipService.convertIterableToMap(pageOfFriendships.getElementsOnPage());
    }

    /**
     * Adds a new user credential to the social network.
     *
     * @param userCredential the user credential to be added
     */
    public void addUserCredential(UserCredential userCredential) {
        userAuthentificationService.addCredential(userCredential);
    }

    /**
     * Removes a user credential by user ID.
     *
     * @param username the ID of the user whose credential is to be removed
     * @return the removed user credential, if it exists
     * @throws IllegalArgumentException if the credential does not exist
     */
    public Optional<UserCredential> removeUserCredential(String username) {
        return userAuthentificationService.removeCredential(username);
    }

    /**
     * Finds a user credential by username.
     *
     * @param username the username of the credential to be found
     * @return the user credential, if it exists
     */
    public Optional<UserCredential> findCredential(String username) {
        return userAuthentificationService.findCredential(username);
    }


    //Friend requests
    public Iterable<FriendRequest> getFriendRequests() {
        return friendRequestService.getFriendRequests();
    }

    public Optional<FriendRequest> addFriendshipRequest(FriendRequest friendshipRequest) {
        return friendRequestService.addFriendshipRequest(friendshipRequest);
    }

    public Optional<FriendRequest> removeFriendshipRequest(String username1, String username2) {
        Pair<String, String> id = new Pair<>(username1, username2);
        return friendRequestService.removeFriendshipRequest(id);
    }

    public void setStatusForRequest(FriendRequest friendRequest, String status) {
        friendRequest.setStatus(status);
        friendRequestService.updateStatus(friendRequest);
    }

    public void removeAnsweredRequests() {
        friendRequestService.removeAnsweredRequests();
    }

    public int friendshipRequestCount() {
        return friendRequestService.size();
    }

    public Map<String, Boolean> getAllFriendRequestsForUserMap(String username) {
        return friendRequestService.getAllFriendRequestsForUserMap(username);
    }

    //messages

    public void addMessage(Message message) {
        messagesService.addMessage(message);
    }

    public void removeMessage(Message message) {
        messagesService.removeMessage(message);
    }

    public Iterable<Message> getMessages() {
        return messagesService.getAllMessages();
    }

    public Optional<Message> getMessageById(Pair<String, Pair<String, LocalDateTime>> id) {
        return messagesService.getMessageById(id);
    }

    public Map<String, Vector<Message>> getMessagesGroupedByUser(String username) {
        return messagesService.getMessagesGroupedByUser(username);
    }

    public void updateMessage(Message message) {
        messagesService.updateMessage(message);
    }


}
