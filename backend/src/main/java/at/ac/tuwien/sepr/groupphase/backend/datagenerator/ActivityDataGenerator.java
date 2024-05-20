package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@AllArgsConstructor
public class ActivityDataGenerator implements DataGenerator {
    ActivityRepository activityRepository;
    ExpenseRepository expenseRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;


    @Override
    public void generateData() {
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();

        Activity testActivity = Activity.builder()
            .category(ActivityCategory.EXPENSE)
            .timestamp(LocalDateTime.now().minus(5, ChronoUnit.DAYS))
            .expense(expenses.get(0))
            .group(groups.get(0))
            .user(users.get(0))
            .build();

        activityRepository.save(testActivity);
    }

    @Override
    public void cleanData() {
        activityRepository.deleteAll();
    }


}
