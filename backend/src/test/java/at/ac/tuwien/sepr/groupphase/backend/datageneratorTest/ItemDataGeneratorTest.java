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

        Item item1 = Item.builder().description("Rinderfaschiertes").unit(Unit.Gram).amount(400).build();
        Item item2 = Item.builder().description("Zwiebel").unit(Unit.Piece).amount(1).build();
        Item item3 = Item.builder().description("Knoblauchzehe").unit(Unit.Piece).amount(1).build();
        Item item4 = Item.builder().description("Öl").unit(Unit.Gram).amount(1).build();
        Item item5 = Item.builder().description("Karotte").unit(Unit.Piece).amount(1).build();
        Item item6 = Item.builder().description("Tomaten").unit(Unit.Gram).amount(1).build();
        Item item7 = Item.builder().description("Tomatenmark").unit(Unit.Gram).amount(5).build();
        Item item8 = Item.builder().description("Ketchup").unit(Unit.Gram).amount(3).build();
        Item item9 = Item.builder().description("Basilikum").unit(Unit.Gram).amount(1).build();
        Item item10 = Item.builder().description("Oregano").unit(Unit.Gram).amount(1).build();
        Item item11 = Item.builder().description("Thymian").unit(Unit.Gram).amount(1).build();
        Item item12 = Item.builder().description("Salz").unit(Unit.Gram).amount(1).build();
        Item item13 = Item.builder().description("Pfeffer").unit(Unit.Gram).amount(1).build();

        Item item14 = Item.builder().description("Huhn (im Ganzen, küchenfertig)").unit(Unit.Piece).amount(1).build();
        Item item15 = Item.builder().description("Salz").unit(Unit.Gram).amount(1).build();
        Item item16 = Item.builder().description("Pfeffer").unit(Unit.Gram).amount(1).build();
        Item item17 = Item.builder().description("Öl (für die Pfanne)").unit(Unit.Gram).amount(2).build();
        Item item18 = Item.builder().description("Knoblauchzehe").unit(Unit.Piece).amount(1).build();
        Item item19 = Item.builder().description("Zwiebel (groß)").unit(Unit.Piece).amount(1).build();
        Item item20 = Item.builder().description("Paprikapulver (edelsüß)").unit(Unit.Gram).amount(1).build();
        Item item21 = Item.builder().description("Hühnersuppe (klar)").unit(Unit.Milliliter).amount(250).build();
        Item item22 = Item.builder().description("Paprikaschoten").unit(Unit.Piece).amount(1).build();
        Item item23 = Item.builder().description("Tomaten").unit(Unit.Piece).amount(1).build();
        Item item24 = Item.builder().description("Schlagobers").unit(Unit.Milliliter).amount(100).build();
        Item item25 = Item.builder().description("Sauerrahm").unit(Unit.Milliliter).amount(200).build();

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