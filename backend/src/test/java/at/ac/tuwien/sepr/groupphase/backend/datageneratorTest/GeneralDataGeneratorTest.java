package at.ac.tuwien.sepr.groupphase.backend.datageneratorTest;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@AllArgsConstructor
public class GeneralDataGeneratorTest implements DataGeneratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserDataGeneratorTest userDataGenerator;
    private final GroupDataGeneratorTest groupDataGenerator;
    private final FriendshipDataGeneratorTest friendshipDataGenerator;
    private final PantryDataGeneratorTest pantryDataGenerator;
    private final ItemDataGeneratorTest itemDataGenerator;
    private final RecipeDataGeneratorTest recipeDataGenerator;
    private final ExpenseDataGeneratorTest expenseDataGenerator;
    private final ActivityDataGeneratorTest activityDataGenerator;
    private final PaymentDataGeneratorTest paymentDataGenerator;
    private final ShoppingListDataGeneratorTest shoppingListDataGenerator;
    private final BudgetDataGeneratorTest budgetDataGenerator;

    public void generateData() {
        LOGGER.debug("generating all data for test");
        cleanData();
        userDataGenerator.generateData();
        groupDataGenerator.generateData();
        friendshipDataGenerator.generateData();
        itemDataGenerator.generateData();
        recipeDataGenerator.generateData();
        pantryDataGenerator.generateData();
        expenseDataGenerator.generateData();
        paymentDataGenerator.generateData();
        activityDataGenerator.generateData();
        budgetDataGenerator.generateData();

        shoppingListDataGenerator.generateData();
        LOGGER.debug("finished generating all data for test");
    }

    public void cleanData() {
        LOGGER.debug("cleaning all data for test");
        budgetDataGenerator.cleanData();
        activityDataGenerator.cleanData();
        paymentDataGenerator.cleanData();
        expenseDataGenerator.cleanData();
        pantryDataGenerator.cleanData();
        recipeDataGenerator.cleanData();
        shoppingListDataGenerator.cleanData();
        itemDataGenerator.cleanData();
        friendshipDataGenerator.cleanData();
        groupDataGenerator.cleanData();

        userDataGenerator.cleanData();
        LOGGER.debug("finished cleaning all data for test");
    }

}
