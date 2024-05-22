package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@SpringJUnitConfig
@ComponentScan("at.ac.tuwien.sepr.groupphase.backend.repository")
public class PaymentRepositoryTest implements TestData {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void beforeEach() {
        friendshipRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    @Transactional
    public void testCreatePaymentSuccess() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();

        Payment payment = Payment.builder()
            .amount(10.0)
            .receiver(testUsers[0])
            .payer(testUsers[1])
            .date(LocalDateTime.now())
            .build();

        Payment payment1 = paymentRepository.save(payment);

        assertEquals(payment, payment1);
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

        Set<ApplicationUser> users = new HashSet<>();
        users.add(testUser1);
        users.add(testUser2);
        GroupEntity group = GroupEntity.builder()
            .groupName("testGroupDebt")
            .users(users)
            .build();
        groupRepository.save(group);

        return new ApplicationUser[]{testUser1, testUser2};
    }
}
