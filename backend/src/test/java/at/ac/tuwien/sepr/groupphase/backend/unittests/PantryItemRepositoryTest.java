package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class PantryItemRepositoryTest {

    @Autowired
    private PantryItemRepository pantryItemRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PantryRepository pantryRepository;

    @BeforeEach
    public void beforeEach() {
        groupRepository.deleteAll();
        pantryItemRepository.deleteAll();
        pantryRepository.deleteAll();
    }

    @Test
    public void givenDescription_whenSaveTwoItemsWhereOneDescriptionMatches_thenFindListWithOneItem() {
        GroupEntity group = GroupEntity.builder().groupName("test").build();
        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);

        PantryItem descriptionMatch = PantryItem.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        PantryItem otherItem = PantryItem.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();

        pantry.addItem(descriptionMatch);
        pantry.addItem(otherItem);
        groupRepository.save(group);

        assertEquals(1, pantryItemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById("oothp", pantry).size());
    }

    @Test
    public void givenNothing_whenSaveTwoItems_thenFindListOrderedById() {
        GroupEntity group = GroupEntity.builder().groupName("test").build();
        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);

        PantryItem descriptionMatch = PantryItem.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        PantryItem otherItem = PantryItem.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();

        pantry.addItem(descriptionMatch);
        pantry.addItem(otherItem);
        groupRepository.save(group);

        List<PantryItem> items = pantryItemRepository.findByPantryOrderById(pantry);
        assertAll(
            () -> assertEquals(2, items.size()),
            () -> assertTrue(items.get(0).getId() < items.get(1).getId())
        );
    }

    @Test
    public void givenDescription_whenSaveTwoItemsWhereBothDescriptionMatch_thenFindListWithTwoItemsAndOrderedById() {
        GroupEntity group = GroupEntity.builder().groupName("test").build();
        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);

        PantryItem descriptionMatch = PantryItem.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        PantryItem otherItem = PantryItem.builder().description("Honey").unit(Unit.Milliliter).amount(300).build();

        pantry.addItem(descriptionMatch);
        pantry.addItem(otherItem);
        groupRepository.save(group);

        List<PantryItem> items = pantryItemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById("o", pantry);
        assertAll(
            () -> assertEquals(2, items.size()),
            () -> assertTrue(items.get(0).getId() < items.get(1).getId())
        );
    }

    @Test
    public void matchingItemExists_thenFindItem() {
        GroupEntity group = GroupEntity.builder().groupName("test").build();
        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);

        PantryItem descriptionMatch = PantryItem.builder().description("Toothpaste").unit(Unit.Piece).amount(1).build();
        pantry.addItem(descriptionMatch);
        groupRepository.save(group);

        List<PantryItem> list = pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs("Toothpaste", Unit.Piece, pantry);
        assertAll(
            () -> assertEquals(1, list.size()),
            () -> assertEquals(descriptionMatch, list.get(0))
        );
    }
}
