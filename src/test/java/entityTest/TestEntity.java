package entityTest;

import org.example.authentificationManagement.entity.UserCredential;
import org.example.friendshipManagement.entity.Friendship;
import org.example.userManagement.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestEntity {

    private User user1;
    private User user2;
    private User user3;
    private UserCredential userCredentials;
    private Friendship friendship;

    @BeforeEach
    public void setUp() {
        user1 = new User("John", "Doe");
        user2 = new User("Jane", "Smith");
        user3 = new User("Alice", "Johnson");
        userCredentials = new UserCredential( "John", "Doe");
        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
        friendship = new Friendship(1L, 2L, LocalDateTime.now());
        friendship.setId(1L);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals("John", user1.getFirstName());
        assertEquals("Doe", user1.getLastName());

        user1.setFirstName("Jack");
        user1.setLastName("Daniels");

        assertEquals("Jack", user1.getFirstName());
        assertEquals("Daniels", user1.getLastName());


        assertEquals("John", userCredentials.getUsername());
        assertEquals("Doe", userCredentials.getPassword());

        userCredentials.setPassword("1234");

        assertEquals("1234", userCredentials.getPassword());
    }

    @Test
    public void testEqualsAndHashCode() {
        User userCopy = new User("John", "Doe");
        userCopy.setId(1L);

        assertEquals(user1, userCopy);
        assertEquals(user1.hashCode(), userCopy.hashCode());
    }

    @Test
    public void testGetters() {
        assertEquals(1L, friendship.getUsername1());
        assertEquals(2L, friendship.getUsername2());
        assertNotNull(friendship.getCreatedAt());
    }

    @Test
    public void testCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        Friendship newFriendship = new Friendship(1L, 2L, createdAt);
        assertEquals(createdAt, newFriendship.getCreatedAt());
    }

    @Test
    public void testIDSetAndGet() {
        assertEquals(1L, friendship.getId());
        friendship.setId(2L);
        assertEquals(2L, friendship.getId());
    }
}