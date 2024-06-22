package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetFrequency;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class BudgetDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupRepository groupRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public void generateData() {
        LOGGER.debug("generating data for budgets");

        List<GroupEntity> groups = groupRepository.findAll();
        Random random = new Random();
        random.setSeed(12345);
        String[] budgetNames = {
            "Lebensmittel", "Restaurantbesuche", "Freizeit", "Klamotten",
            "Club"
        };

        Category[] categories = Category.values();
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime firstOfMonth = today.with(TemporalAdjusters.firstDayOfNextMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<Expense> expenses;

        for (GroupEntity group : groups) {
            expenses = expenseRepository.findAllByGroupId(group.getId());

            if (group.getGroupName().equals("Chippin")) {
                generateDataForChippin(group, random, categories, firstOfMonth, expenses);
            } else {
                for (int i = 0; i < 3; i++) {
                    double amount = 1 + random.nextDouble() * 900;
                    amount = Math.round(amount * 100.0) / 100.0;
                    Category category = categories[random.nextInt(categories.length)];
                    double alreadySpent = calculateAlreadySpent(expenses, category, firstOfMonth);
                    Budget budget = Budget.builder()
                        .name(budgetNames[random.nextInt(budgetNames.length)])
                        .amount(amount)
                        .category(category)
                        .alreadySpent(alreadySpent)
                        .timestamp(firstOfMonth)
                        .resetFrequency(ResetFrequency.MONTHLY)
                        .group(group)
                        .build();

                    budgetRepository.saveAndFlush(budget);
                }
            }
        }
    }

    private double calculateAlreadySpent(List<Expense> expenses, Category category, LocalDateTime firstOfMonth) {

        double sum = 0.0;
        for (Expense expense : expenses) {
            if (expense.getCategory().equals(category)) {
                if (expense.getDate().isAfter(firstOfMonth)) {
                    sum += expense.getAmount();
                }
            }
        }
        return sum;
    }


    private void generateDataForChippin(GroupEntity group, Random random, Category[] categories, LocalDateTime firstOfMonth, List<Expense> expenses) {
        if (group.getGroupName().equals("Chippin")) {
            double spent = calculateAlreadySpent(expenses, Category.Food, firstOfMonth.minusMonths(1));

            Budget budget = Budget.builder()
                .name("Restaurantsbesuche")
                .amount(600)
                .category(Category.Food)
                .alreadySpent(spent)
                .resetFrequency(ResetFrequency.MONTHLY)
                .timestamp(firstOfMonth)
                .group(group)
                .build();


            budgetRepository.saveAndFlush(budget);

            spent = calculateAlreadySpent(expenses, Category.Entertainment, firstOfMonth.minusMonths(1));

            Budget budget2 = Budget.builder()
                .name("Hobby")
                .amount(450)
                .category(Category.Entertainment)
                .alreadySpent(spent)
                .resetFrequency(ResetFrequency.MONTHLY)
                .timestamp(firstOfMonth)
                .group(group)
                .build();

            budgetRepository.saveAndFlush(budget2);

            spent = calculateAlreadySpent(expenses, Category.Travel, firstOfMonth.minusMonths(1));
            Budget budget3 = Budget.builder()
                .name("Ausfluege")
                .amount(500)
                .category(Category.Travel)
                .alreadySpent(spent)
                .resetFrequency(ResetFrequency.MONTHLY)
                .timestamp(firstOfMonth)
                .group(group)
                .build();

            budgetRepository.saveAndFlush(budget3);
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for budgets");
        budgetRepository.deleteAll();
    }
}
