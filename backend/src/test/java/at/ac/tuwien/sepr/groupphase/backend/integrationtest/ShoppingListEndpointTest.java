package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
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

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
