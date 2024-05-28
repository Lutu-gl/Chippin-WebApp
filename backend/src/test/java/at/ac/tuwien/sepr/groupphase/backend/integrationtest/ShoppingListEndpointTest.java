package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShoppingListEndpointTest extends BaseTest {

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

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupMapperImpl groupMapperImpl;

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenValidShoppingListCreateDtoWithoutGroupId_whenCreateShoppingListWithoutGroupId_thenShoppingListIsPersisted() throws Exception {
        // Find id of user
        Long id = userRepository.findByEmail("user1@example.com").getId();

        when(securityService.hasCorrectId(id)).thenReturn(true);

        ShoppingListCreateDto shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .build();

        mockMvc.perform(post("/api/v1/users/" + id + "/shopping-lists")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListCreateDto)))
            .andExpect(status().isOk());

        assertAll(
            () -> assertThat(shoppingListRepository.findAll()).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAll().stream().anyMatch(sl -> sl.getName().equals("Test Shopping List"))).isTrue()
        );
        assertThat(shoppingListRepository.findAllByOwnerId(id)).isNotEmpty();
        assertThat(shoppingListRepository.findAllByOwnerId(id).stream()
            .anyMatch(shoppingList -> shoppingList.getName().equals("Test Shopping List"))).isTrue();

    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenValidShoppingLIstCreateDtoWithGroupId_whenCreateShoppingListWithGroupId_thenShoppingListIsPersisted() throws Exception {
        // Find id of user
        List<ApplicationUser> all = userRepository.findAll();

        Long userId = userRepository.findByEmail("user1@example.com").getId();
        GroupDetailDto group = groupMapperImpl.groupEntityToGroupDto(groupRepository.findByGroupName("groupExample1"));

        when(securityService.hasCorrectId(userId)).thenReturn(true);
        when(securityService.isGroupMember(group.getId())).thenReturn(true);

        ShoppingListCreateDto shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .group(group)
            .build();

        mockMvc.perform(post("/api/v1/users/" + userId + "/shopping-lists")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListCreateDto)))
            .andExpect(status().isOk());

        assertAll(
            () -> assertThat(shoppingListRepository.findAll()).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAll().stream().anyMatch(sl -> sl.getName().equals("Test Shopping List"))).isTrue(),
            () -> assertThat(shoppingListRepository.findAllByOwnerId(userId)).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAllByOwnerId(userId).stream()
                .anyMatch(shoppingList -> shoppingList.getName().equals("Test Shopping List"))).isTrue(),
            () -> assertThat(shoppingListRepository.findAllByGroupId(group.getId())).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAllByGroupId(group.getId()).stream()
                .anyMatch(shoppingList -> shoppingList.getName().equals("Test Shopping List"))).isTrue()
        );

    }

    @Test
    @WithMockUser
    public void givenValidShoppingListCreateDtoWithCategories_whenCreateShoppingList_thenShoppingListIsPersisted() throws Exception {
        // Find id of user
        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);

        ShoppingListCreateDto shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List with Categories")
            .categories(Set.of(Category.Food, Category.Entertainment, Category.Other))
            .build();


        mockMvc.perform(post("/api/v1/users/" + id + "/shopping-lists")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListCreateDto)))
            .andExpect(status().isOk());

        var shoppingList = shoppingListRepository.findAllByOwnerId(id).stream()
            .filter(sl -> sl.getName().equals("Test Shopping List with Categories"))
            .findFirst()
            .orElseThrow();

        assertAll(
            () -> assertThat(shoppingList.getCategories()).isNotEmpty(),
            () -> assertThat(shoppingList.getCategories()).contains(Category.Food, Category.Entertainment, Category.Other),
            () -> assertThat(shoppingList.getCategories().size()).isEqualTo(3)
        );

    }

    @Test
    @WithMockUser
    public void givenValidShoppingListIdAndItemId_whenMoveItemToPantry_thenItemIsMovedToPantry() throws Exception {
        // Find id of user
        Long userId = userRepository.findByEmail("user5@example.com").getId();

        when(securityService.hasCorrectId(userId)).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find id of shopping list find a
        var shoppingList = shoppingListRepository.findByGroup_Users_Id(userId).stream().filter(sl -> sl.getGroup() != null).findFirst().orElseThrow();

        var shoppingListItem = shoppingList.getItems().getFirst();


        mockMvc.perform(put("/api/v1/users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}/pantry", userId, shoppingList.getId(),
                shoppingListItem.getId(),
                shoppingList.getGroup().getId()))
            .andExpect(status().isOk());

        // Refresh shopping list
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).orElseThrow();

        assertAll(
            () -> assertThat(updatedShoppingList.getItems()).doesNotContain(shoppingListItem),
            () -> assertThat(updatedShoppingList.getGroup().getPantry().getItems()).anyMatch(
                item -> item.getDescription().equals(shoppingListItem.getItem().getDescription()))
        );

    }

    @Test
    @WithMockUser
    public void givenValidShoppingListWithCheckedItems_whenMoveItemsToPantry_thenItemsAreMovedToPantry() throws Exception {
        // Find id of user
        var user = userRepository.findByEmail("user5@example.com");
        Long userId = user.getId();

        when(securityService.hasCorrectId(userId)).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find shopping list
        var shoppingList = shoppingListRepository.findByGroup_Users_Id(userId).stream().filter(sl -> sl.getGroup() != null).findFirst().orElseThrow();
        var shoppingListItems = shoppingList.getItems();

        //Check 3 items
        shoppingListItems.stream().limit(3).forEach(item -> item.setCheckedBy(user));
        // Persist changes
        shoppingListRepository.save(shoppingList);

        // Move checked items to pantry
        mockMvc.perform(put("/api/v1/users/{userId}/shopping-lists/{shoppingListId}/pantry", userId, shoppingList.getId()))
            .andExpect(status().isOk());

        // Refresh shopping list
        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).orElseThrow();
        // Get pantry
        var pantry = updatedShoppingList.getGroup().getPantry();

        assertAll(
            () -> assertThat(updatedShoppingList.getItems().stream().filter(item -> item.getCheckedBy() != null)).isEmpty(),
            () -> assertThat(pantry.getItems().stream().map(Item::getDescription))
                .contains(shoppingListItems.stream().limit(3).map(item -> item.getItem().getDescription()).toArray(String[]::new))
        );
    }


    @Test
    @WithMockUser
    public void givenValidShoppingListUpdateDto_whenUpdateShoppingList_thenShoppingListIsUpdated() throws Exception {
        // Find id of user
        Long userId = userRepository.findByEmail("user1@example.com").getId();

        when(securityService.hasCorrectId(userId)).thenReturn(true);
        when(securityService.canAccessShoppingList(any())).thenReturn(true);

        // Find id of shopping list
        var shoppingList = shoppingListRepository.findAllByOwnerId(userId).stream().findFirst().orElseThrow();

        ShoppingListUpdateDto shoppingListUpdateDto = ShoppingListUpdateDto.builder()
            .name("Updated Shopping List")
            .categories(Set.of(Category.Food))
            .group(null)
            .build();

        mockMvc.perform(patch("/api/v1/shopping-lists/{shoppingListId}", shoppingList.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListUpdateDto)))
            .andExpect(status().isOk());

        var updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).orElseThrow();

        assertAll(
            () -> assertThat(updatedShoppingList.getName()).isEqualTo("Updated Shopping List"),
            () -> assertThat(updatedShoppingList.getCategories()).contains(Category.Food),
            () -> assertThat(updatedShoppingList.getGroup()).isNull()
        );
    }

}
