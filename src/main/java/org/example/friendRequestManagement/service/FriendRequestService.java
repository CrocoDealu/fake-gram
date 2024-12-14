package org.example.friendRequestManagement.service;

import org.example.utils.Pair;
import org.example.dto.FriendRequestFilterDTO;
import org.example.exceptions.ServiceValidationException;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.friendRequestManagement.repository.FriendRequestRepository;
import org.example.utils.paging.Page;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendRequestService {
    private final FriendRequestRepository friendshipRequestRepository;

    public FriendRequestService(FriendRequestRepository friendshipRequestRepository) {
        this.friendshipRequestRepository = friendshipRequestRepository;
    }

    /**
     * Returns an iterable collection of all friend requests in the social network.
     *
     * @return all friendships from the friendship repository
     */
    public Iterable<FriendRequest> getFriendRequests() {
        return friendshipRequestRepository.findAll();
    }

    /**
     * Adds a new friendship request between two users to the social network.
     * Ensures that the friendship request does not already exist before saving.
     *
     * @param friendshipRequest the friendship to be added
     * @throws ServiceValidationException if the friendship already exists
     */
    public Optional<FriendRequest> addFriendshipRequest(FriendRequest friendshipRequest) {
        return friendshipRequestRepository.save(friendshipRequest);
    }

    /**
     * Removes an existing friendship request between two users.
     * Ensures that the friendship exists before attempting to remove it.
     *
     * @param id1 the ID of the first user in the friendship
     * @param id2 the ID of the second user in the friendship
     * @throws IllegalArgumentException if the friendship does not exist
     */
    public Optional<FriendRequest> removeFriendshipRequest(Pair<String, String> id) {
        Optional<FriendRequest> removedFriendshipRequest = friendshipRequestRepository.delete(id);

        if (removedFriendshipRequest.isEmpty()) {
            throw new IllegalArgumentException("The friendship request doesn't exist!");
        }
        return removedFriendshipRequest;
    }
    /**
     * Removes all friendship requests that are accepeted
     */
    public void removeAnsweredRequests() {
        friendshipRequestRepository.deleteAcceptedOrDeclined();
    }

    public Optional<FriendRequest> updateStatus(FriendRequest friendshipRequest) {
        return friendshipRequestRepository.update(friendshipRequest);
    }

    public int size() {
        int count = 0;
        for (FriendRequest _ : friendshipRequestRepository.findAll()) {
            count++;
        }
        return count;
    }

    public Map<String, Boolean> getAllFriendRequestsForUserMap(String username) {

        Page<FriendRequest> page = friendshipRequestRepository.findAllOnPage(null, new FriendRequestFilterDTO(username, null));
        return StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .flatMap(request -> Stream.of(request.getFromUsername(), request.getToUsername()))
                .filter(user -> !user.equals(username))
                .distinct()
                .collect(Collectors.toMap(
                        user -> user,
                        user -> true
                ));
    }
}
