package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RemoveIngredientsFromPantryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemMergeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry.GetRecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeByItemsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PantryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PantryServiceTest {

    @Mock
    private PantryRepository pantryRepository;

    @Mock
    private PantryItemRepository pantryItemRepository;

    @Mock
    private PantryItemService pantryItemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ItemMapperImpl itemMapper;

    @InjectMocks
    private PantryServiceImpl pantryService;

    private Pantry pantry;
    private PantryItem pantryItem;
    private PantryItem existingItem;
    private PantryItemMergeDto mergeDto;
    private Recipe recipe;
    private Item item;

    @BeforeEach
    void setUp() {
        pantry = new Pantry();
        pantry.setId(1L);

        recipe = Recipe.builder().description("Test description").id(1L).isPublic(true).name("Test Recipe").build();

        item = new Item();
        item.setId(10L);
        item.setDescription("Test Item");
        item.setAmount(5);
        item.setUnit(Unit.Gram);

        recipe.addIngredient(item);

        pantryItem = new PantryItem();
        pantryItem.setId(1L);
        pantryItem.setDescription("Test Item");
        pantryItem.setAmount(10);
        pantryItem.setLowerLimit(12L);
        pantryItem.setUnit(Unit.Gram);
        pantryItem.setPantry(pantry);

        existingItem = new PantryItem();
        existingItem.setId(1L);
        existingItem.setDescription("Test Item");
        existingItem.setAmount(10);
        existingItem.setUnit(Unit.Gram);
        existingItem.setPantry(pantry);

        PantryItemDto existingItemDto = new PantryItemDto(1L, "Test Item", 10, Unit.Gram, null);

        mergeDto = PantryItemMergeDto.builder().itemToDeleteId(2L).result(existingItemDto).build();
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

    @Test
    void testMergeItems_Success() throws Exception {
        long pantryId = pantry.getId();
        long itemIdToDelete = mergeDto.getItemToDeleteId();

        when(pantryRepository.findById(pantryId)).thenReturn(Optional.of(pantry));
        when(pantryItemRepository.findById(itemIdToDelete)).thenReturn(Optional.of(existingItem));
        when(pantryItemRepository.findById(existingItem.getId())).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        Item mergedItem = pantryService.mergeItems(mergeDto, pantryId);

        verify(pantryItemRepository, times(1)).findById(mergeDto.getItemToDeleteId());
        verify(itemRepository, times(1)).save(any(Item.class));

        assertNotNull(mergedItem);
        assertEquals(existingItem.getId(), mergedItem.getId());
        assertEquals(existingItem.getDescription(), mergedItem.getDescription());
        assertEquals(existingItem.getAmount(), mergedItem.getAmount());
        assertEquals(existingItem.getUnit(), mergedItem.getUnit());
    }

    @Test
    void testMergeItems_ItemToDeleteNotFound() {
        long pantryId = pantry.getId();
        mergeDto.setItemToDeleteId(100L); // Non-existent item ID

        when(pantryRepository.findById(pantryId)).thenReturn(Optional.of(pantry));
        when(pantryItemRepository.findById(mergeDto.getItemToDeleteId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.mergeItems(mergeDto, pantryId));

        verify(pantryItemRepository, times(1)).findById(mergeDto.getItemToDeleteId());
        verify(pantryItemRepository, never()).save(any(PantryItem.class));
    }

    @Test
    void testMergeItems_PantryNotFound() {
        long pantryId = 100L; // Non-existent pantry ID

        when(pantryRepository.findById(pantryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pantryService.mergeItems(mergeDto, pantryId));

        verify(pantryItemRepository, never()).findById(anyLong());
        verify(pantryItemRepository, never()).save(any(PantryItem.class));
    }

    @Test
    void testGetRecipes_Success() {
        GetRecipeDto getRecipeDto = new GetRecipeDto();
        getRecipeDto.setItemIds(new Long[]{1L});

        when(pantryRepository.getReferenceById(pantry.getId())).thenReturn(pantry);
        when(recipeRepository.findRecipesByItemIds(getRecipeDto.getItemIds(), null)).thenReturn(List.of(recipe));
        when(itemMapper.listOfItemsToListOfItemDto(recipe.getIngredients())).thenCallRealMethod();
        when(itemMapper.listOfPantryItemsToListOfPantryItemDto(anyList())).thenCallRealMethod();

        List<RecipeByItemsDto> recipes = pantryService.getRecipes(getRecipeDto, pantry.getId(), null);

        assertNotNull(recipes);
        assertEquals(1, recipes.size());
        assertEquals("Test Recipe", recipes.get(0).getName());
        verify(pantryRepository, times(1)).getReferenceById(pantry.getId());
        verify(recipeRepository, times(1)).findRecipesByItemIds(getRecipeDto.getItemIds(), null);
    }

    @Test
    void testRemoveRecipeIngredientsFromPantry_Success() {
        long recipeId = 1L;
        int portion = 2;

        when(recipeRepository.findAllIngredientsByRecipeId(recipeId)).thenReturn(List.of(item));
        when(pantryItemRepository.findMatchingRecipeItemsInPantry(pantry.getId(), recipeId)).thenReturn(List.of(pantryItem));

        RemoveIngredientsFromPantryDto dto = pantryService.removeRecipeIngredientsFromPantry(pantry.getId(), recipeId, portion);

        assertNotNull(dto);
        assertFalse(dto.getRecipeItems().isEmpty());
        assertEquals(10L, dto.getRecipeItems().get(0).getId());
        assertFalse(dto.getPantryItems().isEmpty());
        assertEquals(1L, dto.getPantryItems().get(0).getId());
        verify(recipeRepository, times(1)).findAllIngredientsByRecipeId(recipeId);
        verify(pantryItemRepository, times(1)).findMatchingRecipeItemsInPantry(pantry.getId(), recipeId);
    }

    @Test
    void testFindAllMissingItems_Success() {
        when(pantryItemRepository.findAllMissingItems(pantry.getId())).thenReturn(List.of(pantryItem));
        when(itemMapper.listOfPantryItemsToListOfPantryItemDto(List.of(pantryItem))).thenReturn(List.of(PantryItemDto.builder().amount(10).lowerLimit(12L).id(1L).description("Test Item").unit(Unit.Gram).build())); //.thenCallRealMethod();

        List<PantryItemDto> missingItems = pantryService.findAllMissingItems(pantry.getId());

        assertNotNull(missingItems);
        assertEquals(1L, missingItems.get(0).getId());
        assertEquals(2, missingItems.get(0).getAmount());
        assertFalse(missingItems.isEmpty());
        verify(pantryItemRepository, times(1)).findAllMissingItems(pantry.getId());
        verify(itemMapper, times(1)).listOfPantryItemsToListOfPantryItemDto(List.of(pantryItem));
    }
}

