package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ExchangeRate;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
@AllArgsConstructor
public class GeneralDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserDataGenerator userDataGenerator;
    private final GroupDataGenerator groupDataGenerator;
    private final FriendshipDataGenerator friendshipDataGenerator;
    private final PantryDataGenerator pantryDataGenerator;
    private final ItemDataGenerator itemDataGenerator;
    private final RecipeDataGenerator recipeDataGenerator;
    private final ExpenseDataGenerator expenseDataGenerator;
    private final ActivityDataGenerator activityDataGenerator;
    private final PaymentDataGenerator paymentDataGenerator;
    private final ShoppingListDataGenerator shoppingListDataGenerator;
    private final BudgetDataGenerator budgetDataGenerator;
    private final ExchangeRateDataGenerator exchangeRateDataGenerator;

    @PostConstruct
    public void generateData() {
        LOGGER.debug("generating all data");
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
        exchangeRateDataGenerator.generateData();

        shoppingListDataGenerator.generateData();
        LOGGER.debug("finished generating all data");
    }

    public void cleanData() {
        LOGGER.debug("cleaning all data");
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
        exchangeRateDataGenerator.cleanData();

        userDataGenerator.cleanData();
        LOGGER.debug("finished cleaning all data");
    }

}
