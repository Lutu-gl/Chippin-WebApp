package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class ItemDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository itemRepository;

    @Override
    public void generateData() {
        LOGGER.debug("generating data for items");

        Item item1 = Item.builder().description("Ground Beef").unit(Unit.Gram).amount(400).build();
        Item item2 = Item.builder().description("Onion").unit(Unit.Piece).amount(1).build();
        Item item3 = Item.builder().description("Garlic Clove").unit(Unit.Piece).amount(1).build();
        Item item4 = Item.builder().description("Oil").unit(Unit.Gram).amount(1).build();
        Item item5 = Item.builder().description("Carrot").unit(Unit.Piece).amount(1).build();
        Item item6 = Item.builder().description("Tomatoes").unit(Unit.Gram).amount(1).build();
        Item item7 = Item.builder().description("Tomato Paste").unit(Unit.Gram).amount(5).build();
        Item item8 = Item.builder().description("Ketchup").unit(Unit.Gram).amount(3).build();
        Item item9 = Item.builder().description("Basil").unit(Unit.Gram).amount(1).build();
        Item item10 = Item.builder().description("Oregano").unit(Unit.Gram).amount(1).build();
        Item item11 = Item.builder().description("Thyme").unit(Unit.Gram).amount(1).build();
        Item item12 = Item.builder().description("Salt").unit(Unit.Gram).amount(1).build();
        Item item13 = Item.builder().description("Pepper").unit(Unit.Gram).amount(1).build();

        Item item14 = Item.builder().description("Whole Chicken, kitchen-ready").unit(Unit.Piece).amount(1).build();
        Item item15 = Item.builder().description("Salt").unit(Unit.Gram).amount(1).build();
        Item item16 = Item.builder().description("Pepper").unit(Unit.Gram).amount(1).build();
        Item item17 = Item.builder().description("Oil (for frying)").unit(Unit.Gram).amount(2).build();
        Item item18 = Item.builder().description("Garlic Clove").unit(Unit.Piece).amount(1).build();
        Item item19 = Item.builder().description("Large Onion").unit(Unit.Piece).amount(1).build();
        Item item20 = Item.builder().description("Sweet Paprika Powder").unit(Unit.Gram).amount(1).build();
        Item item21 = Item.builder().description("Clear Chicken Broth").unit(Unit.Milliliter).amount(250).build();
        Item item22 = Item.builder().description("Bell Peppers").unit(Unit.Piece).amount(1).build();
        Item item23 = Item.builder().description("Tomato").unit(Unit.Piece).amount(1).build();
        Item item24 = Item.builder().description("Heavy Cream").unit(Unit.Milliliter).amount(100).build();
        Item item25 = Item.builder().description("Sour Cream").unit(Unit.Milliliter).amount(200).build();


        itemRepository.saveAll(Arrays.asList(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10,
            item11, item12, item13, item14, item15, item16, item17, item18, item19, item20, item21, item22, item23,
            item24, item25));
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for items");
        itemRepository.deleteAll();
    }
}