package at.ac.tuwien.sepr.groupphase.backend.datagenerator;


import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import jakarta.transaction.Transactional;
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

            if (group.getGroupName().equals("PantryTestGroup1")) {
                PantryItem item1 = PantryItem.builder()
                    .description("PantryTest-Potato")
                    .unit(Unit.Piece)
                    .amount(2)
                    .build();
                group.getPantry().addItem(item1);

                PantryItem item2 = PantryItem.builder()
                    .description("PantryTest-Milk")
                    .unit(Unit.Milliliter)
                    .amount(500)
                    .build();
                group.getPantry().addItem(item2);

                PantryItem item3 = PantryItem.builder()
                    .description("PantryTest-Tea")
                    .unit(Unit.Gram)
                    .amount(250)
                    .build();
                group.getPantry().addItem(item3);

                PantryItem item4 = PantryItem.builder()
                    .description("PantryTest-Bread")
                    .unit(Unit.Gram)
                    .amount(900)
                    .build();
                group.getPantry().addItem(item4);
            } else if (group.getGroupName().equals("PantryTestGroup2")) {
                PantryItem item5 = PantryItem.builder()
                    .description("PantryTest-Potato")
                    .unit(Unit.Piece)
                    .amount(2)
                    .build();
                group.getPantry().addItem(item5);
            } else if (!group.getGroupName().equals("PantryTestGroup3")) {
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
            }
            groupRepository.save(group); // update groups
        }

        /*
        //Pantry Test Group 1
        GroupEntity pantryTestGroup1 = groupRepository.findByGroupName("PantryTestGroup1");
        Pantry pantry1 = Pantry.builder()
            .group(pantryTestGroup1)
            .build();
        pantry1.setGroup(pantryTestGroup1);
        pantryTestGroup1.setPantry(pantry1);

        PantryItem item1 = PantryItem.builder()
            .description("PantryTest-Potato")
            .unit(Unit.Piece)
            .amount(2)
            .build();
        pantryTestGroup1.getPantry().addItem(item1);

        PantryItem item2 = PantryItem.builder()
            .description("PantryTest-Milk")
            .unit(Unit.Milliliter)
            .amount(500)
            .build();
        pantryTestGroup1.getPantry().addItem(item2);

        PantryItem item3 = PantryItem.builder()
            .description("PantryTest-Tea")
            .unit(Unit.Gram)
            .amount(250)
            .build();
        pantryTestGroup1.getPantry().addItem(item3);

        PantryItem item4 = PantryItem.builder()
            .description("PantryTest-Bread")
            .unit(Unit.Gram)
            .amount(900)
            .build();
        pantryTestGroup1.getPantry().addItem(item4);

        groupRepository.save(pantryTestGroup1);

        //Pantry Test Group 2
        GroupEntity pantryTestGroup2 = groupRepository.findByGroupName("PantryTestGroup2");
        Pantry pantry2 = Pantry.builder()
            .group(pantryTestGroup2)
            .build();
        pantry2.setGroup(pantryTestGroup2);
        pantryTestGroup2.setPantry(pantry2);

        PantryItem item5 = PantryItem.builder()
            .description("PantryTest-Potato")
            .unit(Unit.Piece)
            .amount(2)
            .build();
        pantryTestGroup2.getPantry().addItem(item5);

        groupRepository.save(pantryTestGroup2);

        //Pantry Test Group 3 (Empty)
        GroupEntity pantryTestGroup3 = groupRepository.findByGroupName("PantryTestGroup3");
        Pantry pantry = Pantry.builder()
            .group(pantryTestGroup3)
            .build();
        pantry.setGroup(pantryTestGroup3);
        pantryTestGroup3.setPantry(pantry);

        groupRepository.save(pantryTestGroup3);

         */
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for pantry");
        pantryRepository.deleteAll();
    }

}
