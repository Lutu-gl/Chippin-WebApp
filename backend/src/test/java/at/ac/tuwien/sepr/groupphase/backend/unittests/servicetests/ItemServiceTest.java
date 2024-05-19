package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PantryRepository pantryRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ItemService itemService;

    @BeforeEach
    public void beforeEach() {
        itemRepository.deleteAll();
        pantryRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    public void givenNewItem_thenNewItemSavedInPantry() {
        GroupEntity group = GroupEntity.builder().groupName("Test").build();

        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);
        groupRepository.save(group);

        PantryItem pantryItem = PantryItem.builder().description("TestItem").unit(Unit.Piece).amount(10).lowerLimit(12L).build();

        Item result = itemService.pantryAutoMerge(pantryItem, pantry);

        assertEquals(pantryItem, result);
    }

    @Test
    public void givenItemWithSameDescriptionAndUnit_thenItemsMerged() {
        GroupEntity group = GroupEntity.builder().groupName("Test").build();
        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);

        PantryItem existingItem = PantryItem.builder().description("TestItem").unit(Unit.Piece).amount(4).lowerLimit(12L).build();
        pantry.addItem(existingItem);
        groupRepository.save(group);
        PantryItem pantryItem = PantryItem.builder().description("TestItem").unit(Unit.Piece).amount(10).lowerLimit(12L).build();

        Item result = itemService.pantryAutoMerge(pantryItem, pantry);

        assertAll(
            () -> assertEquals(result.getId(), existingItem.getId()),
            () -> assertEquals(result.getAmount(), 14)
        );
    }
}
