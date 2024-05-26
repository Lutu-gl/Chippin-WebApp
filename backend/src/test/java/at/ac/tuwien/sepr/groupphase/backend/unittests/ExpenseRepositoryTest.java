package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
@SpringJUnitConfig
@ComponentScan("at.ac.tuwien.sepr.groupphase.backend.repository")
public class ExpenseRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    public void beforeEach() {
        friendshipRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateExpenseSuccess() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();

        Expense expense = Expense.builder()
            .name("Test Expense")
            .category(Category.Food)
            .amount(10.0)
            .payer(testUsers[0])
            .date(LocalDateTime.now())
            .participants(Map.of(testUsers[0], 0.1, testUsers[1], 0.9))
            .build();

        Expense savedExpense = expenseRepository.save(expense);

        assertEquals(expense, savedExpense);
    }

    @Test
    @Rollback
    @Transactional
    public void testRetieveDebtSuccess() {
        // create test users
        ApplicationUser[] testUsers = createTestUsers();
        GroupEntity group = groupRepository.findAll().get(0);

        Expense expense = Expense.builder()
            .name("Test Expense")
            .category(Category.Food)
            .amount(10.0)
            .payer(testUsers[0])
            .group(group)
            .date(LocalDateTime.now())
            .participants(Map.of(testUsers[0], 0.1, testUsers[1], 0.9))
            .build();

        expenseRepository.save(expense);
        List<Object[]> objects = expenseRepository.calculateBalancesExpensesAndPaymentsForUser(testUsers[0].getEmail(), group.getId());
        String participantEmail = (String) objects.get(0)[0];
        BigDecimal amount = (BigDecimal) objects.get(0)[1];
        assertEquals(testUsers[1].getEmail(), participantEmail);
        assertEquals(9.0, amount.doubleValue());
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
