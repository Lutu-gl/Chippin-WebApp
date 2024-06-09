package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShoppingListItemTest extends BaseTest {
    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @SpyBean
    private SecurityService securityService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @SpyBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    @Transactional
    public void givenValidItemCreateDto_whenAddItemToShoppingList_thenItemIsAddedToShoppingList() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();

        var owner = shoppingList.getOwner();

        // Create an item
        var itemCreateDto = ItemCreateDto.builder()
            .description("Test Item description")
            .unit(Unit.Piece)
            .build();

        // Add the item to the shopping list
        mockMvc.perform(post("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemCreateDto)))
            .andExpect(status().isOk());

        // Check if the item was added to the shopping list
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems()).isNotEmpty(),
            () -> assertThat(updatedShoppingList.getItems().stream().anyMatch(i -> i.getItem().getDescription().equals("Test Item description"))).isTrue()
        );

    }

    @Test
    @WithMockUser
    @Transactional
    public void givenValidItemCreateDto_whenAddItemToShoppingListInGroupWithUserThatIsNotShoppingListOwner_thenItemIsAddedToShoppingList() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list in a group
        var shoppingList = shoppingListRepository.findAll().stream()
            .filter(sl -> sl.getGroup() != null)
            .findFirst().get();

        // Find a user in the group that is not the owner of the shopping list
        var user = shoppingList.getGroup().getUsers().stream()
            .filter(u -> !u.equals(shoppingList.getOwner()))
            .findFirst().get();

        // Create an item
        var itemCreateDto = ItemCreateDto.builder()
            .description("Test Item description")
            .unit(Unit.Piece)
            .amount(1)
            .build();

        // Add the item to the shopping list
        mockMvc.perform(post("/api/v1/users/" + user.getId() + "/shopping-lists/" + shoppingList.getId() + "/items")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemCreateDto)))
            .andExpect(status().isOk());

        // Check if the item was added to the shopping list
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems()).isNotEmpty(),
            () -> assertThat(updatedShoppingList.getItems().stream().anyMatch(i -> i.getItem().getDescription().equals("Test Item description"))).isTrue()
        );


    }

    @Test
    @WithMockUser
    @Transactional
    public void givenValidShoppingListItemUpdateDto_whenUpdateItemInShoppingList_thenItemIsUpdated() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();

        var owner = shoppingList.getOwner();

        // Find an item in the shopping list
        var item = shoppingList.getItems().get(0);

        // Create a shopping list item update dto
        var shoppingListItemUpdateDto = ShoppingListItemUpdateDto.builder()
            .item(ItemUpdateDto.builder()
                .description("Updated Item description")
                .amount(200)
                .unit(Unit.Piece)
                .build())
            .checked(true)
            .build();

        // Update the item in the shopping list
        mockMvc.perform(patch("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items/" + item.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListItemUpdateDto)))
            .andExpect(status().isOk());

        // Check if the item was updated
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        var updatedItem = updatedShoppingList.getItems().stream().filter(i -> i.getId().equals(item.getId())).findFirst().get();
        assertAll(
            () -> assertThat(updatedItem.getItem().getDescription()).isEqualTo("Updated Item description"),
            () -> assertThat(updatedItem.getItem().getUnit()).isEqualTo(Unit.Piece),
            () -> assertThat(updatedItem.getItem().getAmount()).isEqualTo(200),
            () -> assertThat(updatedItem.getCheckedBy()).isNotNull(),
            () -> assertThat(updatedItem.getCheckedBy().getId()).isEqualTo(owner.getId())
        );

    }

    @Test
    @WithMockUser
    @Transactional
    public void givenExistingShoppingListItemId_whenDeleteItemInShoppingList_thenItemIsDeleted() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();

        var owner = shoppingList.getOwner();

        // Find an item in the shopping list
        var item = shoppingList.getItems().get(0);

        // Delete the item in the shopping list
        mockMvc.perform(delete("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items/" + item.getId()))
            .andExpect(status().isOk());

        // Check if the item was deleted
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems().stream().noneMatch(i -> i.getId().equals(item.getId()))).isTrue()
        );

    }

    @Test
    @WithMockUser
    @Transactional
    public void givenValidShoppingListId_whenDeleteCheckedItems_thenCheckedItemsAreDeleted() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();
        var itemAmount = shoppingList.getItems().size();

        var owner = shoppingList.getOwner();

        // Check 3 items
        var items = shoppingList.getItems().subList(0, 3);
        items.forEach(i -> i.setCheckedBy(owner));
        shoppingListRepository.save(shoppingList);

        // Delete the checked items
        mockMvc.perform(delete("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items/checked-items"))
            .andExpect(status().isOk());

        // Check if the checked items were deleted
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems().stream().noneMatch(i -> i.getCheckedBy() != null)).isTrue(),
            () -> assertThat(updatedShoppingList.getItems().size()).isEqualTo(itemAmount - 3)
        );
    }

    @Test
    @WithMockUser
    @Transactional
    public void givenInvalidShoppingListId_whenDeleteCheckedItems_thenNotFoundException() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Delete the checked items
        mockMvc.perform(delete("/api/v1/users/-1/shopping-lists/-1/items/checked-items"))
            .andExpect(status().isNotFound());
    }

    /**
     * Add an item with a name that already exists in the shopping list (and with the same unit) merges the items.
     * Both items are not checked.
     */
    @Test
    @WithMockUser
    @Transactional
    public void givenItemWithSameNameAndUnit_whenAddItemToShoppingList_thenItemsAreMerged() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();
        var itemsAmount = shoppingList.getItems().size();

        var owner = shoppingList.getOwner();

        // Create an item
        var itemCreateDto = ItemCreateDto.builder()
            .description("Test Item description")
            .amount(10)
            .unit(Unit.Piece)
            .build();

        // Add the item to the shopping list
        mockMvc.perform(post("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemCreateDto)))
            .andExpect(status().isOk());

        // Add the same item to the shopping list
        mockMvc.perform(post("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemCreateDto)))
            .andExpect(status().isOk());

        // Check if the item was added to the shopping list
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems().size()).isEqualTo(itemsAmount + 1),
            () -> assertThat(
                updatedShoppingList.getItems().stream().filter(i -> i.getItem().getDescription().equals("Test Item description")).count()).isEqualTo(1),
            () -> assertThat(
                updatedShoppingList.getItems().stream().filter(i -> i.getItem().getDescription().equals("Test Item description")).findFirst().get().getItem()
                    .getAmount()).isEqualTo(20)
        );

    }

    /**
     * Add an item with a name that already exists in the shopping list (and with the same unit) but one of the items is checked does not merge the items.
     */
    @Test
    @WithMockUser
    @Transactional
    public void givenCheckedItemWithSameNameAndUnit_whenAddItemToShoppingList_thenItemsAreNotMerged() throws Exception {
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find a shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();
        var itemsAmount = shoppingList.getItems().size();

        var owner = shoppingList.getOwner();

        // Create an item
        var itemCreateDto = ItemCreateDto.builder()
            .description("Test Item description")
            .amount(10)
            .unit(Unit.Piece)
            .build();

        // Add the item to the shopping list
        mockMvc.perform(post("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(itemCreateDto))
        ).andExpect(status().isOk());

        // Check the item
        shoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        var item = shoppingList.getItems().stream().filter(i -> i.getItem().getDescription().equals("Test Item description")).findFirst().get();
        item.setCheckedBy(owner);
        shoppingListRepository.save(shoppingList);

        // Add the same item to the shopping list
        mockMvc.perform(post("/api/v1/users/" + owner.getId() + "/shopping-lists/" + shoppingList.getId() + "/items")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(itemCreateDto))
        ).andExpect(status().isOk());

        // Check if both items were added to the shopping list
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems().size()).isEqualTo(itemsAmount + 2),
            () -> assertThat(
                updatedShoppingList.getItems().stream().filter(i -> i.getItem().getDescription().equals("Test Item description")).count()).isEqualTo(2),
            () -> assertThat(updatedShoppingList.getItems().stream().filter(i -> i.getItem().getDescription().equals("Test Item description"))
                .filter(i -> i.getCheckedBy() != null).count()).isEqualTo(1),
            () -> assertThat(updatedShoppingList.getItems().stream().filter(i -> i.getItem().getDescription().equals("Test Item description"))
                .filter(i -> i.getCheckedBy() == null).count()).isEqualTo(1)
        );
    }

    @Test
    @WithMockUser(username = "test")
    @Transactional
    public void givenValidData_whenAddItems_thenItemsAreAdded() throws Exception {
        List<ItemCreateDto> items = List.of(
            ItemCreateDto.builder().description("Item 1").unit(Unit.Gram).amount(2000).build(),
            ItemCreateDto.builder().description("Item 2").unit(Unit.Piece).amount(5).build()
        );
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find an existing shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();
        var initialItemsAmount = shoppingList.getItems().size();
        var owner = shoppingList.getOwner();

        mockMvc.perform(post("/api/v1/users/{ownerId}/shopping-lists/{shoppingListId}/items/list", owner.getId(), shoppingList.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(items)))
            .andExpect(status().isOk());

        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        assertAll(
            () -> assertThat(updatedShoppingList.getItems().size()).isEqualTo(initialItemsAmount + 2),
            () -> assertThat(updatedShoppingList.getItems().stream().anyMatch(i -> i.getItem().getDescription().equals("Item 1"))).isTrue(),
            () -> assertThat(updatedShoppingList.getItems().stream().anyMatch(i -> i.getItem().getDescription().equals("Item 2"))).isTrue()
        );
    }

    @Test
    @WithMockUser
    @Transactional
    public void givenOneInvalidAndOneValidItem_whenAddItems_thenException() throws Exception {
        List<ItemCreateDto> items = List.of(
            ItemCreateDto.builder().description("Item 1").unit(Unit.Gram).amount(2000).build(),
            ItemCreateDto.builder().description("Item 2").unit(Unit.Piece).amount(-5).build()
        );
        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find an existing shopping list
        var shoppingList = shoppingListRepository.findAll().getFirst();
        var owner = shoppingList.getOwner();

        mockMvc.perform(
                post("/api/v1/users/{ownerId}/shopping-lists/{shoppingListId}/items/list",
                    owner.getId(), shoppingList.getId())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(items)))
            .andExpect(status().isBadRequest());
    }
}
