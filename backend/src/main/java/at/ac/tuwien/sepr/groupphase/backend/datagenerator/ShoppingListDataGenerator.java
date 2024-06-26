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
import java.util.Arrays;
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
        log.trace("generating shopping list data");
        List<GroupEntity> groups = groupRepository.findAll();
        List<ApplicationUser> users = userRepository.findAll();

        int seed = 123;
        Random random = new Random(seed);

        List<Item> items = new ArrayList<>(Arrays.asList(
            Item.builder().description("Blackberry Jam").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Rice").unit(Unit.Gram).amount(2000).build(),
            Item.builder().description("Salt").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Pasta").unit(Unit.Gram).amount(750).build(),
            Item.builder().description("Chocolate").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Tea Bags").unit(Unit.Piece).amount(50).build(),
            Item.builder().description("Canned Tuna").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Lemon").unit(Unit.Piece).amount(2).build(),
            Item.builder().description("Apple").unit(Unit.Piece).amount(6).build(),
            Item.builder().description("Onion").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Pine Nuts").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Brown Sugar").unit(Unit.Gram).amount(500).build(),
            Item.builder().description("Oatmeal").unit(Unit.Gram).amount(1000).build(),
            Item.builder().description("Peanuts").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Milk").unit(Unit.Milliliter).amount(1500).build(),
            Item.builder().description("Potato").unit(Unit.Piece).amount(5).build(),
            Item.builder().description("Tomato").unit(Unit.Piece).amount(4).build(),
            Item.builder().description("Cereal").unit(Unit.Gram).amount(800).build(),
            Item.builder().description("Eggs").unit(Unit.Piece).amount(18).build(),
            Item.builder().description("Black Tea").unit(Unit.Gram).amount(100).build(),
            Item.builder().description("Rice").unit(Unit.Gram).amount(2500).build(),
            Item.builder().description("Bread").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Yogurt").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Chicken Breast").unit(Unit.Gram).amount(900).build(),
            Item.builder().description("Olive Oil").unit(Unit.Milliliter).amount(500).build(),
            Item.builder().description("Orange Juice").unit(Unit.Milliliter).amount(1000).build(),
            Item.builder().description("Salmon Fillet").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Canned Beans").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Lettuce").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Cheese").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Banana").unit(Unit.Piece).amount(4).build(),
            Item.builder().description("Peanut Butter").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Honey").unit(Unit.Milliliter).amount(300).build(),
            Item.builder().description("Almonds").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Whole Wheat Pasta").unit(Unit.Gram).amount(500).build(),
            Item.builder().description("Strawberries").unit(Unit.Gram).amount(250).build(),
            Item.builder().description("Spinach").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Butter").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Carrot").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Garlic").unit(Unit.Piece).amount(2).build(),
            Item.builder().description("Ground Beef").unit(Unit.Gram).amount(600).build(),
            Item.builder().description("Lemon").unit(Unit.Piece).amount(2).build(),
            Item.builder().description("White Vinegar").unit(Unit.Milliliter).amount(200).build(),
            Item.builder().description("Tofu").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Cucumber").unit(Unit.Piece).amount(2).build(),
            Item.builder().description("Bell Pepper").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Broccoli").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Mango").unit(Unit.Piece).amount(2).build(),
            Item.builder().description("Avocado").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Salad Dressing").unit(Unit.Milliliter).amount(150).build(),
            Item.builder().description("Pasta Sauce").unit(Unit.Milliliter).amount(300).build(),
            Item.builder().description("Walnuts").unit(Unit.Gram).amount(100).build(),
            Item.builder().description("Kiwi").unit(Unit.Piece).amount(4).build(),
            Item.builder().description("Coconut Milk").unit(Unit.Milliliter).amount(400).build(),
            Item.builder().description("Green Beans").unit(Unit.Gram).amount(250).build(),
            Item.builder().description("Ground Turkey").unit(Unit.Gram).amount(600).build(),
            Item.builder().description("Soy Milk").unit(Unit.Milliliter).amount(1000).build(),
            Item.builder().description("Zucchini").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Quinoa").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Limes").unit(Unit.Piece).amount(8).build(),
            Item.builder().description("Maple Syrup").unit(Unit.Milliliter).amount(250).build(),
            Item.builder().description("Red Pepper Flakes").unit(Unit.Gram).amount(50).build(),
            Item.builder().description("Blueberries").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Vanilla Extract").unit(Unit.Milliliter).amount(150).build(),
            Item.builder().description("Peaches").unit(Unit.Piece).amount(6).build(),
            Item.builder().description("Almond Milk").unit(Unit.Milliliter).amount(750).build(),
            Item.builder().description("Chickpeas").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Capers").unit(Unit.Gram).amount(100).build(),
            Item.builder().description("Sesame Oil").unit(Unit.Milliliter).amount(200).build(),
            Item.builder().description("Artichoke Hearts").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Hummus").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Cocoa Powder").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Mushrooms").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Baguette").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Chia Seeds").unit(Unit.Gram).amount(250).build(),
            Item.builder().description("Couscous").unit(Unit.Gram).amount(500).build(),
            Item.builder().description("Cranberry Juice").unit(Unit.Milliliter).amount(1000).build(),
            Item.builder().description("Raisins").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Olives").unit(Unit.Gram).amount(250).build(),
            Item.builder().description("Lime").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Balsamic Glaze").unit(Unit.Milliliter).amount(300).build(),
            Item.builder().description("Pistachios").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Granola").unit(Unit.Gram).amount(600).build(),
            Item.builder().description("Ginger").unit(Unit.Gram).amount(50).build(),
            Item.builder().description("Mixed Nuts").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Red Bell Pepper").unit(Unit.Piece).amount(2).build(),
            Item.builder().description("Asparagus").unit(Unit.Piece).amount(10).build(),
            Item.builder().description("Pineapple Juice").unit(Unit.Milliliter).amount(1000).build(),
            Item.builder().description("Sesame Seeds").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Black Beans").unit(Unit.Gram).amount(500).build(),
            Item.builder().description("Dijon Mustard").unit(Unit.Gram).amount(100).build(),
            Item.builder().description("Thyme").unit(Unit.Gram).amount(15).build(),
            Item.builder().description("Greek Yogurt").unit(Unit.Gram).amount(500).build(),
            Item.builder().description("Bread Crumbs").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Canned Corn").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Coconut Flakes").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Sunflower Seeds").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Pesto").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Cashews").unit(Unit.Gram).amount(250).build(),
            Item.builder().description("Raspberry Jam").unit(Unit.Gram).amount(300).build(),
            Item.builder().description("Sour Cream").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Pumpkin Seeds").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Soy Yogurt").unit(Unit.Gram).amount(500).build(),
            Item.builder().description("Bagels").unit(Unit.Piece).amount(4).build(),
            Item.builder().description("Black Pepper").unit(Unit.Gram).amount(50).build(),
            Item.builder().description("Pitted Dates").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Tahini").unit(Unit.Gram).amount(250).build(),
            Item.builder().description("Lemon Juice").unit(Unit.Milliliter).amount(200).build(),
            Item.builder().description("Paprika").unit(Unit.Gram).amount(100).build(),
            Item.builder().description("Soy Sauce").unit(Unit.Milliliter).amount(300).build(),
            Item.builder().description("Frozen Peas").unit(Unit.Gram).amount(400).build(),
            Item.builder().description("Mayonnaise").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Bacon").unit(Unit.Gram).amount(350).build(),
            Item.builder().description("Corn Tortillas").unit(Unit.Piece).amount(12).build(),
            Item.builder().description("Pineapple").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Salsa").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Coconut Oil").unit(Unit.Milliliter).amount(500).build(),
            Item.builder().description("Ginger").unit(Unit.Gram).amount(80).build(),
            Item.builder().description("Cocoa Powder").unit(Unit.Gram).amount(200).build(),
            Item.builder().description("Pears").unit(Unit.Piece).amount(4).build(),
            Item.builder().description("Baking Powder").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Mustard").unit(Unit.Gram).amount(100).build(),
            Item.builder().description("Cilantro").unit(Unit.Gram).amount(30).build(),
            Item.builder().description("Lime").unit(Unit.Piece).amount(3).build(),
            Item.builder().description("Basil").unit(Unit.Gram).amount(20).build(),
            Item.builder().description("Papaya").unit(Unit.Piece).amount(1).build(),
            Item.builder().description("Shredded Coconut").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Cranberries").unit(Unit.Gram).amount(150).build(),
            Item.builder().description("Balsamic Vinegar").unit(Unit.Milliliter).amount(250).build(),
            Item.builder().description("Apple Cider Vinegar").unit(Unit.Milliliter).amount(500).build(),
            Item.builder().description("Vanilla Extract").unit(Unit.Milliliter).amount(100).build(),
            Item.builder().description("Pecans").unit(Unit.Gram).amount(150).build()
        ));


        // Realistic shopping list names for groups
        List<String> shoppingListNames = List.of(
            "Family Dinner",
            "Household Supplies",
            "Weekly Groceries",
            "Party Supplies",
            "Healthy Snacks",
            "Movie Night",
            "BBQ",
            "Picnic",
            "Baking",
            "Office Pantry Restock",
            "Camping Trip Supplies",
            "Holiday Dinner Prep",
            "Roommates' Essentials",
            "Friends' Weekend BBQ",
            "Birthday Party",
            "Beach Day Supplies",
            "Brunch Ingredients",
            "Game Night Snacks",
            "Road Trip Supplies",
            "Study Group Snacks",
            "Holiday Baking",
            "Garden Party",
            "Potluck Dinner",
            "Tailgate Party Supplies",
            "Fundraiser Bake Sale"
        );

        List<ShoppingList> shoppingListsToSave = new ArrayList<>();

        // Create shopping lists for users
        for (ApplicationUser user : users) {

            ShoppingList shoppingList = ShoppingList.builder().name("My Shopping List").owner(user).build();

            // Randomly choose 5-10 items to add to the shopping list
            List<ShoppingListItem> shoppingListItems = new ArrayList<>();
            for (int i = 0; i < random.nextInt(0, 6) + 5; i++) {
                Item item = items.get(random.nextInt(items.size()));
                // Get the item by deep copy to avoid detached entity exception
                Item itemCopy = Item.builder().description(item.getDescription()).unit(item.getUnit()).amount(item.getAmount()).build();
                shoppingListItems.add(ShoppingListItem.builder().item(itemCopy).addedBy(user).build());
            }

            shoppingList.setItems(shoppingListItems);
            shoppingListsToSave.add(shoppingList);
            shoppingListRepository.save(shoppingList);
        }


        // Create shopping lists for groups
        for (GroupEntity group : groups) {
            ApplicationUser owner = group.getUsers().stream().toList().get(random.nextInt(group.getUsers().size()));

            ShoppingList shoppingList =
                ShoppingList.builder().name(shoppingListNames.get(random.nextInt(0, shoppingListNames.size()))).owner(owner).group(group).build();

            List<ShoppingListItem> shoppingListItems = new ArrayList<>();
            for (int i = 0; i < random.nextInt(0, 6) + 5; i++) {
                Item item = items.get(random.nextInt(items.size()));
                // Get the item by deep copy to avoid detached entity exception
                Item itemCopy = Item.builder().description(item.getDescription()).unit(item.getUnit()).amount(item.getAmount()).build();
                shoppingListItems.add(ShoppingListItem.builder().item(itemCopy).addedBy(owner).build());
            }

            shoppingList.setItems(shoppingListItems);
            shoppingListsToSave.add(shoppingList);

            shoppingListRepository.save(shoppingList);

        }

//        shoppingListRepository.saveAll(shoppingListsToSave);

        // --- Specific shopping lists for Chippin Group ---
        GroupEntity chippInGroup = groupRepository.findByGroupName("Chippin");

        ShoppingList chippinShoppingList = ShoppingList.builder()
            .name("Weekly Groceries")
            .owner(chippInGroup.getUsers().stream().findFirst().orElseThrow())
            .group(chippInGroup)
            .build();

        List<ShoppingListItem> chippinShoppingListItems = new ArrayList<>();
        chippinShoppingListItems.addAll(List.of(
            ShoppingListItem.builder()
                .item(Item.builder().description("Milk").unit(Unit.Milliliter).amount(2).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Eggs").unit(Unit.Piece).amount(12).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Bread").unit(Unit.Piece).amount(1).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Butter").unit(Unit.Gram).amount(250).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Cheese").unit(Unit.Gram).amount(200).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Yogurt").unit(Unit.Gram).amount(500).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Apples").unit(Unit.Piece).amount(6).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Bananas").unit(Unit.Piece).amount(6).build())
                .addedBy(chippInGroup.getUsers().stream().findAny().get())
                .build()
        ));

        chippinShoppingList.setItems(chippinShoppingListItems);
        shoppingListRepository.save(chippinShoppingList);

        // Second shopping list for Chippin Group
        ShoppingList chippinShoppingList2 = ShoppingList.builder()
            .name("BBQ Supplies")
            .owner(chippInGroup.getUsers().stream().filter(user -> user.getEmail().equals("rafael@chippin.com")).findFirst().orElseThrow())
            .group(chippInGroup)
            .build();

        List<ShoppingListItem> chippinShoppingListItems2 = new ArrayList<>();
        chippinShoppingListItems2.addAll(List.of(
            ShoppingListItem.builder()
                .item(Item.builder().description("Chicken Breast").unit(Unit.Gram).amount(1000).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Pork Ribs").unit(Unit.Gram).amount(800).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Beef").unit(Unit.Gram).amount(600).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Sausages").unit(Unit.Piece).amount(10).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Buns").unit(Unit.Piece).amount(10).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Ketchup").unit(Unit.Milliliter).amount(500).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Mustard").unit(Unit.Milliliter).amount(300).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build(),
            ShoppingListItem.builder()
                .item(Item.builder().description("Mayonnaise").unit(Unit.Milliliter).amount(300).build())
                .addedBy(chippInGroup.getUsers().stream().findFirst().get())
                .build()
        ));

        chippinShoppingList2.setItems(chippinShoppingListItems2);
        shoppingListRepository.save(chippinShoppingList2);


    }

    @Override
    public void cleanData() {
        log.trace("cleaning shopping list data");
        shoppingListRepository.deleteAll();
        shoppingListItemRepository.deleteAll();
    }
}
