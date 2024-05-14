package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.Map;

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

    @BeforeEach
    public void beforeEach() {
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Rollback
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
