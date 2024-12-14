package serviceTest;

import org.example.friendshipManagement.entity.Friendship;
import org.example.friendshipManagement.service.FriendshipService;
import org.example.userManagement.entity.User;
import org.example.friendshipManagement.entity.validator.FriendshipValidator;
import org.example.userManagement.entity.validator.UserValidator;
import org.example.networkManagement.SocialComunities;
import org.example.networkManagement.SocialNetwork;
import org.example.userManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocialComunitiesTest {
    private SocialNetwork socialNetwork;
    private SocialComunities comunities;
    private Friendship friendship;
    private Friendship friendship2;
    private Friendship friendship3;
    private Friendship friendship4;
    private Friendship friendship5;

    @BeforeEach
    public void setUp() {
        InMemoryRepository<String, User> userRepository = new InMemoryRepository<>(new UserValidator());
        InMemoryRepository<String, Friendship> friendshipRepository = new InMemoryRepository<>(new FriendshipValidator(userRepository));
        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository);
        socialNetwork = new SocialNetwork(userService, friendshipService, null, null, null);
        comunities = new SocialComunities(socialNetwork);

        // Create users for testing
        User user1 = new User("User", "1");
        User user2 = new User("User", "2");
        User user3 = new User("User", "3");
        User user4 = new User("User", "4");
        User user5 = new User("User", "5");
        User user6 = new User("User", "6");

        // Create a friendship for testing
        friendship = new Friendship(1L, 2L, java.time.LocalDateTime.now());
        friendship2 = new Friendship(2L, 3L, java.time.LocalDateTime.now());
        friendship3 = new Friendship(4L, 5L, java.time.LocalDateTime.now());
        friendship4 = new Friendship(6L, 5L, java.time.LocalDateTime.now());
        friendship5 = new Friendship(1L, 6L, java.time.LocalDateTime.now());

        socialNetwork.addUser(user1);
        socialNetwork.addUser(user2);
        socialNetwork.addUser(user3);
        socialNetwork.addUser(user4);
        socialNetwork.addUser(user5);
        socialNetwork.addUser(user6);


    }

    @Test
    public void testComunitiesNumber() {
        socialNetwork.addFriendship(friendship);
        socialNetwork.addFriendship(friendship2);
        socialNetwork.addFriendship(friendship3);
        socialNetwork.addFriendship(friendship4);

        assert comunities.comunitiesNumber() == 2;
    }

    @Test
    public void testMostSociableCommunity() {
        socialNetwork.addFriendship(friendship);
        socialNetwork.addFriendship(friendship2);
        socialNetwork.addFriendship(friendship3);
        socialNetwork.addFriendship(friendship5);

        // 1, 2; 2, 3; 1, 6; 4, 5;
        List<Long> mostSociableCommunity = comunities.mostSociableNetwork();
        List<Integer> expectedList = List.of(1, 2, 3, 6);
        mostSociableCommunity.sort(null);
        AtomicInteger i = new AtomicInteger();
        mostSociableCommunity.forEach((element) -> {
            int el = Math.toIntExact(element);
            assertEquals(el, expectedList.get(i.get()));
            i.getAndIncrement();
        });
    }

}
