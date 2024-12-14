package org.example.friendshipManagement.service;

import org.example.utils.Pair;
import org.example.dto.FriendshipFilterDTO;
import org.example.exceptions.ServiceValidationException;
import org.example.friendshipManagement.entity.Friendship;
import org.example.friendshipManagement.repository.FriendshipRepository;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Returns an iterable collection of all friendships in the social network.
     *
     * @return all friendships from the friendship repository
     */
    public Iterable<Friendship> getFriendships() {
        return friendshipRepository.findAll();
    }

    /**
     * Adds a new friendship between two users to the social network.
     * Ensures that the friendship does not already exist before saving.
     *
     * @param friendship the friendship to be added
     * @throws ServiceValidationException if the friendship already exists
     */
    public void addFriendship(Friendship friendship) {
        friendshipRepository.save(friendship);
    }

    /**
     * Removes an existing friendship between two users.
     * Ensures that the friendship exists before attempting to remove it.
     *
     * @param id the Pair<String, String> that represents the id of the friendship
     * @throws IllegalArgumentException if the friendship does not exist
     */
    public void removeFriendship(Pair<String, String> id) {
        Optional<Friendship> removedFriendship = friendshipRepository.delete(id);

        if (removedFriendship.isEmpty()) {
            throw new IllegalArgumentException("The friendship doesn't exist!");
        }
    }

    public void removeFriendshipsForUser(String username) {
        friendshipRepository.deleteForUsername(username);
    }


    public Optional<Friendship> getFriendship(Pair<String, String> id) {
        return friendshipRepository.findOne(id);
    }

    public Page<Friendship> getFriendsForUsername(String username, Pageable pageable) {
        return friendshipRepository.findAllOnPage(pageable, new FriendshipFilterDTO(username, null));
    }

    public List<Friendship> convertIterableToList(Iterable<Friendship> friendships) {
        return StreamSupport.stream(friendships.spliterator(), false).toList();
    }

    public Map<String, Boolean> convertIterableToMap(Iterable<Friendship> friendships) {
        return StreamSupport.stream(friendships.spliterator(), false)
                .flatMap(friendship -> Stream.of(
                        Map.entry(friendship.getUsername1(), true),
                        Map.entry(friendship.getUsername2(), true)
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, _) -> existing
                ));
    }

}
