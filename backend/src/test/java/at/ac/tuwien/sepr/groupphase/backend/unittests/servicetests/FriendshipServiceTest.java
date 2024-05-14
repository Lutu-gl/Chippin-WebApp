package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidFriendRequest;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FriendshipServiceTest extends BaseTest {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipService friendshipService;

    @BeforeEach
    public void beforeEach() {
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    public void testSendFriendRequestShouldWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        assertDoesNotThrow(() -> friendshipService.sendFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testSendFriendRequestToUnknownEmailShouldNotWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        assertThrows(NotFoundException.class, () -> friendshipService.sendFriendRequest(applicationUsers[0].getEmail(), "unkown@email.com"));
    }

    @Test
    @Transactional
    @Rollback
    public void testSendFriendRequestToMyselfShouldNotWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        assertThrows(InvalidFriendRequest.class, () -> friendshipService.sendFriendRequest(applicationUsers[0].getEmail(), applicationUsers[0].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testSendFriendRequestToFriendWithAlreadyPendingRequestShouldNotWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[0])
            .receiver(applicationUsers[1])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.PENDING)
            .build();

        friendshipRepository.save(friendship);

        assertThrows(InvalidFriendRequest.class, () -> friendshipService.sendFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testSendFriendRequestToFriendWithAlreadyAcceptedRequestShouldNotWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[0])
            .receiver(applicationUsers[1])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.ACCEPTED)
            .build();

        friendshipRepository.save(friendship);

        assertThrows(InvalidFriendRequest.class, () -> friendshipService.sendFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testSendFriendRequestToFriendWhoAlsoSendARequestShouldWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[1])
            .receiver(applicationUsers[0])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.PENDING)
            .build();

        friendshipRepository.save(friendship);

        assertDoesNotThrow(() -> friendshipService.sendFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testAcceptFriendRequestShouldWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[0])
            .receiver(applicationUsers[1])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.PENDING)
            .build();

        friendshipRepository.save(friendship);

        assertDoesNotThrow(() -> friendshipService.acceptFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testAcceptFriendRequestShouldNotWorkIfThereIsNoRequest() {
        ApplicationUser[] applicationUsers = createTestUsers();

        assertThrows(InvalidFriendRequest.class, () -> friendshipService.acceptFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testRejectFriendRequestShouldWork() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[0])
            .receiver(applicationUsers[1])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.PENDING)
            .build();

        friendshipRepository.save(friendship);

        assertDoesNotThrow(() -> friendshipService.rejectFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testRejectFriendRequestShouldNotWorkIfThereIsNoRequest() {
        ApplicationUser[] applicationUsers = createTestUsers();

        assertThrows(InvalidFriendRequest.class, () -> friendshipService.rejectFriendRequest(applicationUsers[0].getEmail(), applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetIncomingFriendRequestShouldReturnOneEmail() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[1])
            .receiver(applicationUsers[0])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.PENDING)
            .build();

        friendshipRepository.save(friendship);

        Collection<String> incomingFriendRequests = friendshipService.getIncomingFriendRequest(applicationUsers[0].getEmail());
        assertEquals(1, incomingFriendRequests.size());
        assertTrue(incomingFriendRequests.contains(applicationUsers[1].getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetFriendsShouldReturnOneEmail() {
        ApplicationUser[] applicationUsers = createTestUsers();
        Friendship friendship = Friendship.builder()
            .sender(applicationUsers[1])
            .receiver(applicationUsers[0])
            .sentAt(LocalDateTime.now())
            .friendshipStatus(FriendshipStatus.ACCEPTED)
            .build();

        friendshipRepository.save(friendship);

        Collection<String> incomingFriendRequests = friendshipService.getFriends(applicationUsers[0].getEmail());
        assertEquals(1, incomingFriendRequests.size());
        assertTrue(incomingFriendRequests.contains(applicationUsers[1].getEmail()));

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

        return new ApplicationUser[]{testUser1, testUser2};
    }

}
