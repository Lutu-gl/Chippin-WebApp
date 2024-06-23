package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PantryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class PantryServiceTest {

    @Mock
    private PantryRepository pantryRepository;

    @Mock
    private PantryItemRepository pantryItemRepository;

    @Mock
    private PantryItemService pantryItemService;

    @InjectMocks
    private PantryServiceImpl pantryService;

    private Pantry pantry;

    private PantryItem pantryItem;

    @BeforeEach
    void setUp() {
        pantry = new Pantry();
        pantry.setId(1L);

        pantryItem = new PantryItem();
        pantryItem.setId(1L);
        pantryItem.setDescription("Test Item");
        pantryItem.setAmount(10);
        pantryItem.setUnit(Unit.Gram);
        pantryItem.setPantry(pantry);
    }

    @Test
    void testFindAllItems_Success() {
        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.of(pantry));

        List<PantryItem> expectedItems = new ArrayList<>();
        expectedItems.add(PantryItem.builder().description("item 1").unit(Unit.Piece).amount(10).build());
        expectedItems.add(PantryItem.builder().description("item 2").unit(Unit.Gram).amount(5).build());
        pantry.addItem(expectedItems.get(0));
        pantry.addItem(expectedItems.get(1));

        List<PantryItem> result = pantryService.findAllItems(pantry.getId());

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(expectedItems.size(), result.size()),
            () -> assertEquals(expectedItems.get(0).getDescription(), result.get(0).getDescription()),
            () -> assertEquals(expectedItems.get(1).getDescription(), result.get(1).getDescription())
        );

        verify(pantryRepository, times(1)).findById(pantry.getId());
    }

    @Test
    void testFindAllItems_PantryNotFound() {
        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.findAllItems(pantry.getId()));

        verify(pantryRepository, times(1)).findById(pantry.getId());
    }

    @Test
    void testFindItemsByDescription_Success() {
        String description = "Item";

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.of(pantry));

        List<PantryItem> expectedItems = new ArrayList<>();
        expectedItems.add(PantryItem.builder().description("Item 1").unit(Unit.Piece).amount(10).build());
        expectedItems.add(PantryItem.builder().description("Item 2").unit(Unit.Gram).amount(5).build());
        pantry.addItem(expectedItems.get(0));
        pantry.addItem(expectedItems.get(1));

        when(pantryItemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById(description, pantry))
            .thenReturn(expectedItems);

        List<PantryItem> result = pantryService.findItemsByDescription(description, pantry.getId());

        assertNotNull(result);
        assertEquals(expectedItems.size(), result.size());
        assertEquals(expectedItems.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(expectedItems.get(1).getDescription(), result.get(1).getDescription());

        verify(pantryRepository, times(1)).findById(pantry.getId());
    }

    @Test
    void testFindItemsByDescription_PantryNotFound() {
        String description = "Item";

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.findItemsByDescription(description, pantry.getId()));

        verify(pantryRepository, times(1)).findById(pantry.getId());
    }

    @Test
    void testAddItemToPantry_Success() {
        PantryItem pantryItem = PantryItem.builder()
            .description("Item 1")
            .unit(Unit.Piece)
            .amount(10)
            .build();

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.of(pantry));
        when(pantryItemService.pantryAutoMerge(any(PantryItem.class), any(Pantry.class))).thenReturn(pantryItem);

        Item addedItem = pantryService.addItemToPantry(pantryItem, pantry.getId());

        assertNotNull(addedItem);
        assertEquals(pantryItem.getDescription(), addedItem.getDescription());
        assertEquals(pantryItem.getUnit(), addedItem.getUnit());
        assertEquals(pantryItem.getAmount(), addedItem.getAmount());

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemService, times(1)).pantryAutoMerge(eq(pantryItem), any(Pantry.class));
    }

    @Test
    void testAddItemToPantry_PantryNotFound() {
        PantryItem pantryItem = PantryItem.builder()
            .description("Item 1")
            .unit(Unit.Piece)
            .amount(10)
            .build();

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.addItemToPantry(pantryItem, pantry.getId()));

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemService, never()).pantryAutoMerge(any(PantryItem.class), any(Pantry.class));
    }

    @Test
    void testDeleteItem_Success() {
        long itemId = 1L;

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.of(pantry));
        when(pantryItemRepository.findById(itemId)).thenReturn(Optional.of(pantryItem));
        doNothing().when(pantryItemRepository).deleteById(any(Long.class));

        pantryService.deleteItem(pantry.getId(), itemId);

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemRepository, times(1)).findById(itemId);
        verify(pantryItemRepository, times(1)).deleteById(pantryItem.getId());
    }

    @Test
    void testDeleteItem_PantryNotFound() {
        long itemId = 1L;

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.deleteItem(pantry.getId(), itemId));

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemRepository, never()).findById(anyLong());
        verify(pantryItemRepository, never()).deleteById(pantryItem.getId());
    }

    @Test
    void testDeleteItem_ItemNotFound() {
        long itemId = 1L;

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.of(pantry));
        when(pantryItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.deleteItem(pantry.getId(), itemId));

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemRepository, times(1)).findById(itemId);
        verify(pantryItemRepository, never()).deleteById(pantryItem.getId());
    }

    @Test
    void testUpdateItem_Success() {
        PantryItem pantryItem = PantryItem.builder()
            .id(1L)
            .description("Updated Item")
            .unit(Unit.Piece)
            .amount(20)
            .build();

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.of(pantry));
        when(pantryItemService.pantryAutoMerge(any(PantryItem.class), any(Pantry.class))).thenReturn(pantryItem);

        Item updatedItem = pantryService.updateItem(pantryItem, pantry.getId());

        assertNotNull(updatedItem);
        assertEquals(pantryItem.getDescription(), updatedItem.getDescription());
        assertEquals(pantryItem.getUnit(), updatedItem.getUnit());
        assertEquals(pantryItem.getAmount(), updatedItem.getAmount());

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemService, times(1)).pantryAutoMerge(eq(pantryItem), any(Pantry.class));
    }

    @Test
    void testUpdateItem_PantryNotFound() {
        PantryItem pantryItem = PantryItem.builder()
            .id(1L)
            .description("Updated Item")
            .unit(Unit.Gram)
            .amount(20)
            .build();

        when(pantryRepository.findById(pantry.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.updateItem(pantryItem, pantry.getId()));

        verify(pantryRepository, times(1)).findById(pantry.getId());
        verify(pantryItemService, never()).pantryAutoMerge(any(PantryItem.class), any(Pantry.class));
    }

}
