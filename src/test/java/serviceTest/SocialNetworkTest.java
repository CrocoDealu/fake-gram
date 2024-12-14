package serviceTest;

import org.example.exceptions.ServiceValidationException;
import org.example.friendshipManagement.service.FriendshipService;
import org.example.networkManagement.SocialNetwork;
import org.example.friendshipManagement.entity.Friendship;
import org.example.userManagement.entity.User;
import org.example.friendshipManagement.entity.validator.FriendshipValidator;
import org.example.userManagement.entity.validator.UserValidator;
import org.example.userManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class SocialNetworkTest {

    private SocialNetwork socialNetwork;
    private User user1;
    private User user2;
    private Friendship friendship;

    @BeforeEach
    public void setUp() {
        InMemoryRepository<Long, User> userRepository = new InMemoryRepository<>(new UserValidator());
        InMemoryRepository<Long, Friendship> friendshipRepository = new InMemoryRepository<>(new FriendshipValidator(userRepository));
        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository);
        socialNetwork = new SocialNetwork(userService, friendshipService, null, null, null);

        // Create users for testing
        user1 = new User("John", "Doe");
        user2 = new User("Jane", "Smith");

        // Create a friendship for testing
        friendship = new Friendship(1L, 2L, java.time.LocalDateTime.now());
    }

    // Test user CRUD operations

    @Test
    public void testAddUser() {
        socialNetwork.addUser(user1);
        assertEquals(1, socialNetwork.getUserSize());
        boolean isSameUser1 = socialNetwork.findUser(1L).isPresent() && socialNetwork.findUser(1L).get().equals(user1);
        assertTrue(isSameUser1);
    }

    @Test
    public void testFindUser_UserDoesNotExist() {
        Optional<User> empty = socialNetwork.findUser(999L);
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testRemoveUser_Valid() {
        socialNetwork.addUser(user1);
        Optional<User> removedUser = socialNetwork.removeUser(user1.getId());
        assertTrue(removedUser.isPresent());
        assertEquals(user1, removedUser.get());
        assertEquals(socialNetwork.findUser(user1.getId()), Optional.empty());
    }

    @Test
    public void testRemoveUser_UserDoesNotExist() {
        Optional<User> empty = socialNetwork.removeUser(999L);
        assertTrue(empty.isEmpty());
    }

    // Test friendship CRUD operations

    @Test
    public void testAddFriendship_Valid() {
        socialNetwork.addUser(user1);
        socialNetwork.addUser(user2);
        socialNetwork.addFriendship(friendship);
        assertEquals(1, socialNetwork.getFriendshipsSize());
    }

    @Test
    public void testAddFriendship_FriendshipAlreadyExists() {
        socialNetwork.addUser(user1);
        socialNetwork.addUser(user2);
        socialNetwork.addFriendship(friendship);

        ServiceValidationException exception = assertThrows(ServiceValidationException.class, () -> {
            socialNetwork.addFriendship(friendship);  // Same friendship, should throw exception
        });
        assertEquals("The friendship already exists!", exception.getMessage());
    }

    @Test
    public void testAddFriendship_InvalidUsers() {
        Friendship invalidFriendship = new Friendship(999L, 888L, java.time.LocalDateTime.now());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            socialNetwork.addFriendship(invalidFriendship);
        });
        assertEquals("The id doesn't exist!", exception.getMessage()); // User does not exist
    }

    @Test
    public void testRemoveFriendship_Valid() {
        socialNetwork.addUser(user1);
        socialNetwork.addUser(user2);
        socialNetwork.addFriendship(friendship);

        socialNetwork.removeFriendship(user1.getId(), user2.getId());
        assertEquals(0, socialNetwork.getFriendshipsSize());
    }

    @Test
    public void testRemoveFriendship_FriendshipDoesNotExist() {
        socialNetwork.addUser(user1);
        socialNetwork.addUser(user2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            socialNetwork.removeFriendship(user1.getId(), user2.getId());  // Friendship doesn't exist
        });
        assertEquals("The friendship doesn't exist!", exception.getMessage());
    }

    // Test utility methods

    @Test
    public void testGetNewUserId_EmptyRepository() {
        Long newId = socialNetwork.getNewUserId();
        assertEquals(1L, newId);
    }

    @Test
    public void testGetNewUserId_WithExistingUsers() {
        socialNetwork.addUser(user1);
        Long newId = socialNetwork.getNewUserId();
        assertEquals(user1.getId() + 1, newId);
    }

    @Test
    public void testGetNewFriendshipId_EmptyRepository() {
        Long newId = socialNetwork.getNewFriendshipId();
        assertEquals(1L, newId);
    }

    @Test
    public void testGetNewFriendshipId_WithExistingFriendships() {
        socialNetwork.addUser(user1);
        socialNetwork.addUser(user2);
        socialNetwork.addFriendship(friendship);
        Long newId = socialNetwork.getNewFriendshipId();
        assertEquals(friendship.getId() + 1, newId);
    }
}
