package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    public void beforeEach() {
        budgetRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    public void testCreateAndFindBudget() {

        GroupEntity group1 = GroupEntity.builder()
            .groupName("Developers")
            .users(new HashSet<>())
            .build();

        groupRepository.save(group1);

        Budget budget = Budget.builder()
            .name("Abos")
            .amount(30)
            .group(group1)
            .build();


        budgetRepository.save(budget);

        List<Budget> foundBudgets = budgetRepository.findAll();
        assertAll(
            () -> assertNotNull(foundBudgets, "Budget list should not be null"),
            () -> assertFalse(foundBudgets.isEmpty(), "Budget list should not be empty"),
            () -> assertEquals(1, foundBudgets.size(), "There should be exactly one budget"),
            () -> assertEquals("Abos", foundBudgets.get(0).getName(), "Budget name should match"),
            () -> assertEquals(30, foundBudgets.get(0).getAmount(), "Budget amount should match")
        );
    }

    @Test
    public void testUpdateBudget() {

        GroupEntity group1 = GroupEntity.builder()
            .groupName("Developers")
            .users(new HashSet<>())
            .build();

        groupRepository.save(group1);


        Budget budget = Budget.builder()
            .name("Food")
            .amount(250)
            .group(group1)
            .build();

        budgetRepository.save(budget);

        budget.setName("Food 2.0");
        budget.setAmount(2500);
        budgetRepository.save(budget);

        Budget updatedBudget = budgetRepository.findById(budget.getId()).orElseThrow();
        assertAll(
            () -> assertEquals("Food 2.0", updatedBudget.getName(), "Updated name should match"),
            () -> assertEquals(2500, updatedBudget.getAmount(), "Updated amount should match")
        );
    }

    @Test
    public void testDeleteBudget() {

        GroupEntity group1 = GroupEntity.builder()
            .groupName("Developers")
            .users(new HashSet<>())
            .build();

        groupRepository.save(group1);


        Budget budget = Budget.builder()
            .name("Food")
            .amount(250)
            .group(group1)
            .build();

        budgetRepository.save(budget);

        // Delete the budget
        budgetRepository.delete(budget);

        // Check that the budget no longer exists
        assertFalse(budgetRepository.existsById(budget.getId()), "Budget should have been deleted");
    }
}
