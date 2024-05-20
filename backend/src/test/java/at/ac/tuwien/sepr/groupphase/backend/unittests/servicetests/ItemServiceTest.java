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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PantryRepository pantryRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ItemService itemService;

    @Test
    public void givenNewItem_thenNewItemSavedInPantry() {
        GroupEntity group = new GroupEntity("Test");

        groupRepository.save(group);

        Pantry pantry = group.getPantry();
        PantryItem pantryItem = PantryItem.builder().description("TestItem").unit(Unit.Piece).amount(10).lowerLimit(12L).build();

        Item result = itemService.pantryAutoMerge(pantryItem, pantry);

        assertEquals(pantryItem, result);
    }

    @Test
    public void givenItemWithSameDescriptionAndUnit_thenItemsMerged() {
        GroupEntity group = new GroupEntity("Test");

        Pantry pantry = group.getPantry();
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
