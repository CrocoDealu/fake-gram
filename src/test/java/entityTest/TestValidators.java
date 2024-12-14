package entityTest;

import org.example.friendshipManagement.entity.Friendship;
import org.example.userManagement.entity.User;
import org.example.friendshipManagement.entity.validator.FriendshipValidator;
import org.example.userManagement.entity.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

public class TestValidators {

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    private Friendship friendship1;
    private Friendship friendship2;
    private Friendship friendship3;
    private UserValidator userValidator;
    private FriendshipValidator friendshipValidator;

    @BeforeEach
    public void setUp() {
        user1 = new User("John", "Doe");
        user2 = new User("Jane", "Smith");
        user3 = new User("", "Smith");
        user4 = new User("", "Smith");
        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
        user4.setId(4L);
        friendship1 = new Friendship(1L, 2L, LocalDateTime.now());
        friendship1.setId(1L);
        friendship2 = new Friendship(null, 2L, LocalDateTime.now());
        friendship2.setId(2L);
        friendship3 = new Friendship(3L, 2L, LocalDateTime.now());
        friendship3.setId(3L);
        userValidator = new UserValidator();
    }

}
