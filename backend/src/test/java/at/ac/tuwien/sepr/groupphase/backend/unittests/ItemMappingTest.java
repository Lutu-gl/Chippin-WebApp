package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ItemMappingTest {
    private final List<Item> items = List.of(new Item(1L, "Potato", 2, Unit.Piece, null, null));
    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void givenNothing_whenMapListWithOneItem_thenReturnListWithOneElementAndAllProperties() {
        List<ItemDto> dtoList = itemMapper.listOfItemsToListOfItemDto(items);
        assertEquals(1, dtoList.size());
        ItemDto item = dtoList.get(0);
        assertAll(
            () -> assertEquals(2, item.getAmount()),
            () -> assertEquals("Potato", item.getDescription()),
            () -> assertEquals(Unit.Piece, item.getUnit())
        );
    }
}