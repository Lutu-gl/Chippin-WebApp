package at.ac.tuwien.sepr.groupphase.backend.datagenerator;


import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class PantryDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final PantryRepository pantryRepository;
    private final GroupRepository groupRepository;


    @Override
    public void generateData() {
        LOGGER.debug("generating data for pantry");
        List<GroupEntity> groups = groupRepository.findAll();

        String[] descriptions = {
            "Milk", "Chocolate", "Banana", "Butter", "Honey", "Egg", "Cheese", "Bread",
            "Apple", "Orange", "Pear", "Grapes", "Strawberries", "Blueberries", "Raspberries",
            "Tomato", "Cucumber", "Lettuce", "Carrot", "Potato", "Onion", "Garlic", "Pepper",
            "Chicken", "Beef", "Pork", "Fish", "Shrimp", "Tofu", "Beans", "Rice", "Pasta",
            "Bread", "Bagel", "Muffin", "Donut", "Cake", "Pie", "Ice Cream", "Yogurt",
            "Coffee", "Tea", "Juice", "Water", "Soda", "Beer", "Wine", "Whiskey"
        };
        Unit[] units = {Unit.Milliliter, Unit.Gram, Unit.Piece};
        Random random = new Random();

        for (GroupEntity group : groups) {
            Pantry pantry = Pantry.builder()
                .group(group)
                .build();
            pantry.setGroup(group);

            group.setPantry(pantry);

            for (int i = 0; i < 5; i++) {
                String description = descriptions[random.nextInt(descriptions.length)];
                Unit unit = units[random.nextInt(units.length)];
                int amount = random.nextInt(500) + 1;

                PantryItem item = PantryItem.builder()
                    .description(description)
                    .unit(unit)
                    .amount(amount)
                    .build();

                group.getPantry().addItem(item);
            }

            groupRepository.save(group); // update groups
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for pantry");
        pantryRepository.deleteAll();
    }
}
