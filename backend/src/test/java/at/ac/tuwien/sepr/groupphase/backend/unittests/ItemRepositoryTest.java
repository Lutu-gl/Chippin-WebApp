package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PantryRepository pantryRepository;

    @Autowired
    private GroupRepository groupRepository;


    @BeforeEach
    public void beforeEach() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();
        pantryRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenSaveItem_thenFindListWithOneItem() {
        Item item = Item.builder().description("Onion").unit(Unit.Piece).amount(2).build();

        itemRepository.save(item);

        assertAll(
            () -> assertEquals(1, itemRepository.findAll().size()),
            () -> assertNotNull(itemRepository.findById(item.getId()))
        );
    }

    @Test
    public void givenDescription_whenSaveTwoItemsWhereOneDescriptionMatches_thenFindListWithOneItem() {
        GroupEntity group = new GroupEntity("test");
        Pantry pantry = group.getPantry();
        Item descriptionMatch = Item.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        Item otherItem = Item.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();

        pantry.addItem(descriptionMatch);
        pantry.addItem(otherItem);
        groupRepository.save(group);

        assertEquals(1, itemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById("oothp", pantry).size());
    }

    @Test
    public void givenDescription_whenSaveTwoItemsWhereBothDescriptionMatch_thenFindListWithTwoItemsAndOrderedById() {
        GroupEntity group = new GroupEntity("test");
        Pantry pantry = group.getPantry();
        Item descriptionMatch = Item.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        Item otherItem = Item.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();

        pantry.addItem(descriptionMatch);
        pantry.addItem(otherItem);
        groupRepository.save(group);

        List<Item> items = itemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById("o", pantry);
        assertAll(
            () -> assertEquals(2, items.size()),
            () -> assertTrue(items.get(0).getId() < items.get(1).getId())
        );
    }

    @Test
    public void givenNothing_whenSaveTwoItems_thenFindListOrderedById() {
        GroupEntity group = new GroupEntity("test");
        Pantry pantry = group.getPantry();
        Item descriptionMatch = Item.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        Item otherItem = Item.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();

        pantry.addItem(descriptionMatch);
        pantry.addItem(otherItem);
        groupRepository.save(group);

        List<Item> items = itemRepository.findByPantryOrderById(pantry);
        assertAll(
            () -> assertEquals(2, items.size()),
            () -> assertTrue(items.get(0).getId() < items.get(1).getId())
        );
    }
}
