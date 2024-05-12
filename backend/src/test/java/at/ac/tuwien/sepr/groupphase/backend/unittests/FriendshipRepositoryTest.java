package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
@SpringJUnitConfig
@ComponentScan("at.ac.tuwien.sepr.groupphase.backend.repository")
public class FriendshipRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @BeforeEach
    public void beforeEach() {
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testGetIncomingFriendRequestsShouldReturnOneUser() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        // create test friendship
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .friendshipStatus(FriendshipStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build()
        );

        // test method
        List<ApplicationUser> incomingFriendRequests = friendshipRepository.findIncomingFriendRequestsOfUser(testUsers[1]);
        assertEquals(1, incomingFriendRequests.size());
        assertTrue(incomingFriendRequests.contains(testUsers[0]));
    }

    @Test
    public void testPendingFriendRequestExistsShouldReturnTrue() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        // create pending friend request
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .sentAt(LocalDateTime.now())
                .friendshipStatus(FriendshipStatus.PENDING)
                .build()
        );

        // test method
        assertTrue(friendshipRepository.pendingFriendRequestExists(testUsers[0], testUsers[1]));
    }

    @Test
    public void testPendingFriendRequestExistsShouldReturnFalse() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();

        // test method
        assertFalse(friendshipRepository.pendingFriendRequestExists(testUsers[0], testUsers[1]));
    }

    @Test
    public void testAnyFriendshipRelationBetweenUsersExistsShouldReturnTrueForPendingRequest() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        // create pending friendship relation
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .sentAt(LocalDateTime.now())
                .friendshipStatus(FriendshipStatus.PENDING)
                .build()
        );

        // test method
        assertTrue(friendshipRepository.anyFriendshipRelationBetweenUsersExists(testUsers[0], testUsers[1]));
        assertTrue(friendshipRepository.anyFriendshipRelationBetweenUsersExists(testUsers[1], testUsers[0]));
    }

    @Test
    public void testAnyFriendshipRelationBetweenUsersExistsShouldReturnTrueForAcceptedRequest() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        // create accepted friendship relation
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .sentAt(LocalDateTime.now())
                .friendshipStatus(FriendshipStatus.ACCEPTED)
                .build()
        );

        // test method
        assertTrue(friendshipRepository.anyFriendshipRelationBetweenUsersExists(testUsers[0], testUsers[1]));
        assertTrue(friendshipRepository.anyFriendshipRelationBetweenUsersExists(testUsers[1], testUsers[0]));
    }

    @Test
    public void testAnyFriendshipRelationBetweenUsersExistsShouldReturnFalse() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();

        // test method
        assertFalse(friendshipRepository.anyFriendshipRelationBetweenUsersExists(testUsers[0], testUsers[1]));
        assertFalse(friendshipRepository.anyFriendshipRelationBetweenUsersExists(testUsers[1], testUsers[0]));
    }

    @Test
    public void testFindFriendsOfUserShouldFindOneFriend() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        // create friendship
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .sentAt(LocalDateTime.now())
                .friendshipStatus(FriendshipStatus.ACCEPTED)
                .build()
        );

        List<ApplicationUser> friends = friendshipRepository.findFriendsOfUser(testUsers[0]);
        assertEquals(1, friends.size());
        assertTrue(friends.contains(testUsers[1]));

        friends = friendshipRepository.findFriendsOfUser(testUsers[1]);
        assertEquals(1, friends.size());
        assertTrue(friends.contains(testUsers[0]));
    }

    @Test
    public void testAcceptFriendRequestShouldReturnTrue() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        // create pending friend request
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .sentAt(LocalDateTime.now())
                .friendshipStatus(FriendshipStatus.PENDING)
                .build()
        );

        // test method
        assertTrue(friendshipRepository.acceptFriendRequest(testUsers[0], testUsers[1]));

        List<ApplicationUser> friends = friendshipRepository.findFriendsOfUser(testUsers[0]);
        assertEquals(1, friends.size());
        assertTrue(friends.contains(testUsers[1]));

        friends = friendshipRepository.findFriendsOfUser(testUsers[1]);
        assertEquals(1, friends.size());
        assertTrue(friends.contains(testUsers[0]));
    }

    @Test
    public void testAcceptFriendRequestShouldReturnFalse() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();

        // test method
        assertFalse(friendshipRepository.acceptFriendRequest(testUsers[0], testUsers[1]));
    }

    @Test
    public void testRejectFriendRequestShouldReturnTrue() {
        // create test user
        ApplicationUser[] testUsers = createTestUsers();
        // create pending friend request
        friendshipRepository.save(
            Friendship.builder()
                .sender(testUsers[0])
                .receiver(testUsers[1])
                .sentAt(LocalDateTime.now())
                .friendshipStatus(FriendshipStatus.PENDING)
                .build()
        );

        // test method
        assertTrue(friendshipRepository.rejectFriendRequest(testUsers[0], testUsers[1]));

        List<ApplicationUser> friends = friendshipRepository.findFriendsOfUser(testUsers[0]);
        assertEquals(0, friends.size());

        friends = friendshipRepository.findFriendsOfUser(testUsers[1]);
        assertEquals(0, friends.size());
    }

    @Test
    public void testRejectFriendRequestShouldReturnFalse() {
        // create test user
        ApplicationUser[] testUsers = createTestUsers();

        // test method
        assertFalse(friendshipRepository.rejectFriendRequest(testUsers[0], testUsers[1]));
    }

    private ApplicationUser[] createTestUsers() {
        ApplicationUser testUser1 = ApplicationUser.builder()
            .email("friendshipTestUser1@test.com")
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .admin(false)
            .build();

        ApplicationUser testUser2 = ApplicationUser.builder()
            .email("friendshipTestUser2@test.com")
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .admin(false)
            .build();

        userRepository.save(testUser1);
        userRepository.save(testUser2);

        return new ApplicationUser[]{ testUser1, testUser2 };
    }


}
