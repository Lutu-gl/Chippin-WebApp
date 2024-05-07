package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

public class ItemListDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemListRepository itemListRepository;

    public ItemListDataGenerator(ItemListRepository itemListRepository) {
        this.itemListRepository = itemListRepository;
    }

    @PostConstruct
    private void generateItemListWithItems() {
        if (itemListRepository.findAll().size() > 0) {
            LOGGER.debug("itemLists already generated");
        } else {
            LOGGER.debug("generating 2 itemList entry");

            ItemList itemList1 = ItemList.builder().name("First itemList").build();

            Item item1 = Item.builder().description("Milk").unit(Unit.Milliliter).amount(500).build();
            Item item2 = Item.builder().description("Bread").unit(Unit.Gram).amount(250).build();
            Item item3 = Item.builder().description("Eggs").unit(Unit.Piece).amount(12).build();
            Item item4 = Item.builder().description("Cheese").unit(Unit.Gram).amount(200).build();
            Item item5 = Item.builder().description("Yogurt").unit(Unit.Milliliter).amount(250).build();
            Item item6 = Item.builder().description("Apple").unit(Unit.Piece).amount(5).build();
            Item item7 = Item.builder().description("Banana").unit(Unit.Piece).amount(3).build();
            Item item8 = Item.builder().description("Chicken").unit(Unit.Gram).amount(500).build();
            Item item9 = Item.builder().description("Rice").unit(Unit.Gram).amount(1000).build();
            Item item10 = Item.builder().description("Tomato").unit(Unit.Piece).amount(4).build();
            Item item11 = Item.builder().description("Potato").unit(Unit.Kilogram).amount(1).build();
            Item item12 = Item.builder().description("Carrot").unit(Unit.Gram).amount(300).build();
            Item item13 = Item.builder().description("Spinach").unit(Unit.Gram).amount(150).build();
            Item item14 = Item.builder().description("Orange").unit(Unit.Piece).amount(2).build();
            Item item15 = Item.builder().description("Onion").unit(Unit.Piece).amount(3).build();
            Item item16 = Item.builder().description("Pasta").unit(Unit.Gram).amount(500).build();
            Item item17 = Item.builder().description("Beef").unit(Unit.Gram).amount(700).build();
            Item item18 = Item.builder().description("Salmon").unit(Unit.Gram).amount(300).build();
            Item item19 = Item.builder().description("Lettuce").unit(Unit.Gram).amount(200).build();
            Item item20 = Item.builder().description("Avocado").unit(Unit.Piece).amount(2).build();
            Item item21 = Item.builder().description("Cucumber").unit(Unit.Piece).amount(1).build();
            Item item22 = Item.builder().description("Cherry").unit(Unit.Gram).amount(100).build();
            Item item23 = Item.builder().description("Pineapple").unit(Unit.Piece).amount(1).build();
            Item item24 = Item.builder().description("Watermelon").unit(Unit.Kilogram).amount(2).build();
            Item item25 = Item.builder().description("Grapes").unit(Unit.Gram).amount(500).build();


            List<Item> itemsToAdd1 = Arrays.asList(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10,
                item11, item12, item13, item14, item15, item16, item17, item18, item19, item20,
                item21, item22, item23, item24, item25);

            for (Item item : itemsToAdd1) {
                itemList1.addItem(item);
            }
            LOGGER.debug("saving itemList {}", itemList1);
            itemListRepository.save(itemList1);

            ItemList itemList2 = ItemList.builder().name("Second itemList").build();

            Item item26 = Item.builder().description("Chocolate").unit(Unit.Gram).amount(150).build();
            Item item27 = Item.builder().description("Orange Juice").unit(Unit.Milliliter).amount(1000).build();
            Item item28 = Item.builder().description("Salad Dressing").unit(Unit.Milliliter).amount(250).build();
            Item item29 = Item.builder().description("Peanut Butter").unit(Unit.Gram).amount(200).build();
            Item item30 = Item.builder().description("Jam").unit(Unit.Gram).amount(300).build();
            Item item31 = Item.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();
            Item item32 = Item.builder().description("Almonds").unit(Unit.Gram).amount(200).build();
            Item item33 = Item.builder().description("Olive Oil").unit(Unit.Milliliter).amount(500).build();
            Item item34 = Item.builder().description("Ketchup").unit(Unit.Milliliter).amount(300).build();
            Item item35 = Item.builder().description("Mustard").unit(Unit.Milliliter).amount(200).build();
            Item item36 = Item.builder().description("Mayonnaise").unit(Unit.Milliliter).amount(250).build();
            Item item37 = Item.builder().description("Pasta Sauce").unit(Unit.Milliliter).amount(400).build();
            Item item38 = Item.builder().description("Soy Sauce").unit(Unit.Milliliter).amount(200).build();
            Item item39 = Item.builder().description("Vinegar").unit(Unit.Milliliter).amount(250).build();
            Item item40 = Item.builder().description("Raspberry").unit(Unit.Gram).amount(100).build();
            Item item41 = Item.builder().description("Blueberry").unit(Unit.Gram).amount(150).build();
            Item item42 = Item.builder().description("Strawberry").unit(Unit.Gram).amount(200).build();
            Item item43 = Item.builder().description("Blackberry").unit(Unit.Gram).amount(100).build();
            Item item44 = Item.builder().description("Mango").unit(Unit.Piece).amount(1).build();
            Item item45 = Item.builder().description("Papaya").unit(Unit.Piece).amount(1).build();
            Item item46 = Item.builder().description("Coconut").unit(Unit.Piece).amount(1).build();
            Item item47 = Item.builder().description("Peach").unit(Unit.Piece).amount(1).build();
            Item item48 = Item.builder().description("Pear").unit(Unit.Piece).amount(1).build();
            Item item49 = Item.builder().description("Lemon").unit(Unit.Piece).amount(1).build();
            Item item50 = Item.builder().description("Water").unit(Unit.Milliliter).amount(1000).build();

            List<Item> itemsToAdd2 = Arrays.asList(item26, item27, item28, item29, item30, item31, item32, item33, item34, item35,
                item36, item37, item38, item39, item40, item41, item42, item43, item44, item45,
                item46, item47, item48, item49, item50
            );

            for (Item item : itemsToAdd2) {
                itemList2.addItem(item);
            }

            LOGGER.debug("saving itemList {}", itemList2);
            itemListRepository.save(itemList2);
        }
    }
}
