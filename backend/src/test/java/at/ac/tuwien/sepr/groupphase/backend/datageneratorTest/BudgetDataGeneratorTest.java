package at.ac.tuwien.sepr.groupphase.backend.datageneratorTest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetFrequency;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class BudgetDataGeneratorTest implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupRepository groupRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public void generateData() {
        LOGGER.debug("generating data for budgets");

        List<GroupEntity> groups = groupRepository.findAll();
        Random random = new Random();
        String[] budgetNames = {
            "Lebensmittel", "Restaurantbesuche", "Freizeit", "Klamotten",
            "Club"
        };

        Category[] categories = Category.values();

        for (GroupEntity group : groups) {

            if (group.getGroupName().equals("groupExample0") || group.getGroupName().equals("groupExample1")) {
                for (int i = 0; i < 3; i++) {
                    double amount = 1 + random.nextDouble() * 900;
                    amount = Math.round(amount * 100.0) / 100.0;
                    Budget budget = Budget.builder()
                        .name(budgetNames[random.nextInt(budgetNames.length)])
                        .amount(amount)
                        .category(categories[random.nextInt(categories.length)])
                        .alreadySpent(0)
                        .resetFrequency(ResetFrequency.MONTHLY)
                        .group(group)
                        .build();

                    budgetRepository.saveAndFlush(budget);
                }
            }

        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for budgets");
        budgetRepository.deleteAll();
    }
}
