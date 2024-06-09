package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class ActivityDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    ActivityRepository activityRepository;
    ExpenseRepository expenseRepository;
    PaymentRepository paymentRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;


    @Override
    public void generateData() {
        LOGGER.debug("generating data for activity");
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        ActivityCategory[] activityCategories = {ActivityCategory.EXPENSE_UPDATE};

        Random random = new Random();
        random.setSeed(12345);
        int counter = 0;

        for (Expense expense : expenses) {
            if (expense.getGroup().getGroupName().equals("groupExample0")) {
                Activity activity = Activity.builder()
                    .category(ActivityCategory.EXPENSE)
                    .timestamp(expense.getDate())
                    .expense(expense)
                    .group(expense.getGroup())
                    .user(expense.getPayer())
                    .build();

                activityRepository.save(activity);
                continue;
            }

            Activity activity = Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .timestamp(expense.getDate())
                .expense(expense)
                .group(expense.getGroup())
                .user(expense.getPayer())
                .build();

            if (++counter % 5 == 0) {       // Some expenses get updated
                Activity activity2 = Activity.builder()
                    .category(activityCategories[random.nextInt(activityCategories.length)])
                    .timestamp(expense.getDate().plusDays(random.nextInt(10) + 1))
                    .expense(expense)
                    .group(expense.getGroup())
                    .user(expense.getPayer())
                    .build();
                activityRepository.save(activity2);
            }
            activityRepository.save(activity);
        }

        for (Payment payment : payments) {
            Activity activity = Activity.builder()
                .category(ActivityCategory.PAYMENT)
                .timestamp(payment.getDate())
                .payment(payment)
                .group(payment.getGroup())
                .user(payment.getPayer())
                .build();

            if (payment.isDeleted()) {
                Activity activity2 = Activity.builder()
                    .category(ActivityCategory.PAYMENT_DELETE)
                    .timestamp(payment.getDate().plusDays(random.nextInt(10) + 1))
                    .payment(payment)
                    .group(payment.getGroup())
                    .user(payment.getPayer())
                    .build();
                activityRepository.save(activity2);

            }

            activityRepository.save(activity);
        }
    }


    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for activity");
        activityRepository.deleteAll();
    }


}
