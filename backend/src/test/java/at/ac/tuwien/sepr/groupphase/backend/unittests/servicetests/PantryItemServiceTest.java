package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PantryItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class PantryItemServiceTest {
    @Mock
    private PantryItemRepository pantryItemRepository;
    @InjectMocks
    private PantryItemServiceImpl pantryItemService;
    private Pantry pantry;
    private PantryItem pantryItem;

    @BeforeEach
    void setUp() {
        pantry = new Pantry();
        pantryItem = new PantryItem();
        pantryItem.setDescription("Test Item");
        pantryItem.setUnit(Unit.Piece);
        pantryItem.setAmount(5);
    }

    @Test
    void testPantryAutoMerge_NoExistingItem_SavesNewItem() {
        when(pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs(pantryItem.getDescription(), pantryItem.getUnit(), pantry))
            .thenReturn(Collections.emptyList());

        when(pantryItemRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem savedItem = pantryItemService.pantryAutoMerge(pantryItem, pantry);

        assertNotNull(savedItem);
        assertEquals(pantryItem.getDescription(), savedItem.getDescription());
        assertEquals(pantryItem.getUnit(), savedItem.getUnit());
        assertEquals(pantryItem.getAmount(), savedItem.getAmount());

        verify(pantryItemRepository, times(1)).save(pantryItem);
    }

    @Test
    void testPantryAutoMerge_ExistingItem_MergesAmounts() {
        int initialAmount = 10;
        PantryItem existingItem = new PantryItem();
        existingItem.setId(1L);
        existingItem.setDescription("Test Item");
        existingItem.setUnit(Unit.Piece);
        existingItem.setAmount(initialAmount);

        when(pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs(pantryItem.getDescription(), pantryItem.getUnit(), pantry))
            .thenReturn(Collections.singletonList(existingItem));
        when(pantryItemRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem savedItem = pantryItemService.pantryAutoMerge(pantryItem, pantry);

        assertNotNull(savedItem);
        assertEquals(existingItem.getId(), savedItem.getId());
        assertEquals(existingItem.getDescription(), savedItem.getDescription());
        assertEquals(existingItem.getUnit(), savedItem.getUnit());
        assertEquals(initialAmount + pantryItem.getAmount(), savedItem.getAmount());
        verify(pantryItemRepository, times(1)).save(existingItem);
    }

    @Test
    void testPantryAutoMerge_UpdateExistingItem() {
        pantryItem.setId(1L);
        int initialAmount = 10;
        PantryItem existingItem = new PantryItem();
        existingItem.setId(1L);
        existingItem.setDescription("Test Item");
        existingItem.setUnit(Unit.Piece);
        existingItem.setAmount(initialAmount);

        when(pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs(pantryItem.getDescription(), pantryItem.getUnit(), pantry))
            .thenReturn(Collections.singletonList(existingItem));
        when(pantryItemRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem savedItem = pantryItemService.pantryAutoMerge(pantryItem, pantry);

        assertNotNull(savedItem);
        assertEquals(pantryItem.getId(), savedItem.getId());
        assertEquals(pantryItem.getDescription(), savedItem.getDescription());
        assertEquals(pantryItem.getUnit(), savedItem.getUnit());
        assertEquals(pantryItem.getAmount(), savedItem.getAmount());
        verify(pantryItemRepository, times(1)).save(pantryItem);
    }

    @Test
    void testPantryAutoMerge_DeleteAndMergeItem() {
        pantryItem.setId(2L);
        pantryItem.setLowerLimit(3L);
        int initialAmount = 10;
        PantryItem existingItem = new PantryItem();
        existingItem.setId(1L);
        existingItem.setDescription("Test Item");
        existingItem.setUnit(Unit.Piece);
        existingItem.setAmount(initialAmount);

        when(pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs(pantryItem.getDescription(), pantryItem.getUnit(), pantry))
            .thenReturn(Collections.singletonList(existingItem));
        when(pantryItemRepository.findById(pantryItem.getId())).thenReturn(Optional.of(pantryItem));
        when(pantryItemRepository.getReferenceById(pantryItem.getId())).thenReturn(pantryItem);
        when(pantryItemRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem savedItem = pantryItemService.pantryAutoMerge(pantryItem, pantry);

        assertNotNull(savedItem);
        assertEquals(existingItem.getId(), savedItem.getId());
        assertEquals(existingItem.getDescription(), savedItem.getDescription());
        assertEquals(existingItem.getUnit(), savedItem.getUnit());
        assertEquals(initialAmount + pantryItem.getAmount(), savedItem.getAmount());
        assertEquals(pantryItem.getLowerLimit(), savedItem.getLowerLimit());
        verify(pantryItemRepository, times(1)).deleteById(pantryItem.getId());
        verify(pantryItemRepository, times(1)).save(existingItem);
    }
}
