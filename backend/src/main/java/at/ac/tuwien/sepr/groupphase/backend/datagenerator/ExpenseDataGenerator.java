package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class ExpenseDataGenerator implements DataGenerator {
    ExpenseRepository expenseRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;


    @Override
    public void generateData() {
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();

        Expense testExpense = Expense.builder()
            .name("testExpense0")
            .category(Category.Food)
            .amount(300.0)
            .date(LocalDateTime.now().minus(5, ChronoUnit.DAYS))
            .payer(users.get(0))
            .group(groups.get(0))
            .participants(Map.of(users.get(0), 0.6, users.get(1), 0.4))
            .build();

        expenseRepository.save(testExpense);
    }

    @Override
    public void cleanData() {
        expenseRepository.deleteAll();
    }


}
