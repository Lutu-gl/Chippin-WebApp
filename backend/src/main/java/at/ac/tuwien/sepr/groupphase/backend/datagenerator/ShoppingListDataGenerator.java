package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
@Slf4j
public class ShoppingListDataGenerator implements DataGenerator {

    private final ShoppingListRepository shoppingListRepository;
    private final GroupRepository groupRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private UserRepository userRepository;

    @Override
    @Transactional
    public void generateData() {
        log.debug("generating shopping list data");
        List<GroupEntity> groups = groupRepository.findAll();
        List<ApplicationUser> users = userRepository.findAll();

        long seed = 12345L;

        String[] descriptions =
            {"Milk", "Chocolate", "Banana", "Butter", "Honey", "Egg", "Cheese", "Bread", "Apple", "Orange", "Pear", "Grapes", "Strawberries", "Blueberries",
                "Raspberries", "Tomato", "Cucumber", "Lettuce", "Carrot", "Potato", "Onion", "Garlic", "Pepper", "Chicken", "Beef", "Pork", "Fish", "Shrimp",
                "Tofu", "Beans", "Rice", "Pasta", "Bread", "Bagel", "Muffin", "Donut", "Cake", "Pie", "Ice Cream", "Yogurt", "Coffee", "Tea", "Juice", "Water",
                "Soda", "Beer", "Wine", "Whiskey"};
        Unit[] units = {Unit.Milliliter, Unit.Gram, Unit.Piece};
        Random random = new Random(seed);

        List<ShoppingList> shoppingListsToSave = new ArrayList<>();

        // Create shopping lists for users
        for (ApplicationUser user : users) {

            ShoppingList shoppingList = ShoppingList.builder().name("Shopping List for " + user.getEmail()).owner(user).build();

            List<ShoppingListItem> shoppingListItems = new ArrayList<>();


            for (int i = 0; i < 5; i++) {
                String description = descriptions[random.nextInt(descriptions.length)];
                Unit unit = units[new Random().nextInt(units.length)];
                int amount = new Random().nextInt(500) + 1;
                var item = Item.builder().description(description).unit(unit).amount(amount).build();
                shoppingListItems.add(ShoppingListItem.builder().item(item).addedBy(user).build());
            }
            shoppingList.setItems(shoppingListItems);
            shoppingListsToSave.add(shoppingList);
        }


        // Create shopping lists for groups
        for (GroupEntity group : groups) {
            ApplicationUser owner = group.getUsers().stream().toList().get(random.nextInt(group.getUsers().size()));

            ShoppingList shoppingList = ShoppingList.builder().name("Shopping List for " + group.getGroupName()).owner(owner).group(group).build();

            List<ShoppingListItem> shoppingListItems = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                String description = descriptions[random.nextInt(descriptions.length)];
                Unit unit = units[new Random().nextInt(units.length)];
                int amount = new Random().nextInt(500) + 1;
                var item = Item.builder().description(description).unit(unit).amount(amount).build();
                shoppingListItems.add(
                    ShoppingListItem.builder().item(item).addedBy(group.getUsers().stream().toList().get(random.nextInt(group.getUsers().size()))).build());
            }

            shoppingList.setItems(shoppingListItems);
            shoppingListsToSave.add(shoppingList);

        }

        shoppingListRepository.saveAll(shoppingListsToSave);

    }

    @Override
    public void cleanData() {
        log.debug("cleaning shopping list data");
        shoppingListRepository.deleteAll();
        shoppingListItemRepository.deleteAll();
    }
}
