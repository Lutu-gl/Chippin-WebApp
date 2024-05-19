package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList.ShoppingListCreateDto;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockBean
    private CustomUserDetailService customUserDetailService;

    private ShoppingListCreateDto shoppingListCreateDto = ShoppingListCreateDto.builder()
        .name("Test Shopping List")
        .groupId(1L)
        .build();
    @SpyBean
    private UserRepository userRepository;

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
            () -> assertThat(shoppingListRepository.findAll().getFirst().getName()).isEqualTo("Test Shopping List")
        );
        assertThat(shoppingListRepository.findAllByOwnerId(id)).isNotEmpty();
        assertThat(shoppingListRepository.findAllByOwnerId(id).stream()
            .anyMatch(shoppingList -> shoppingList.getName().equals("Test Shopping List"))).isTrue();
        shoppingListRepository.deleteAll();

    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenValidShoppingLIstCreateDtoWithGroupId_whenCreateShoppingListWithGroupId_thenShoppingListIsPersisted() throws Exception {
        // Find id of user
        Long userId = userRepository.findByEmail("user1@example.com").getId();
        Long groupId = groupRepository.findByGroupName("groupExample1").getId();

        when(securityService.hasCorrectId(userId)).thenReturn(true);
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        ShoppingListCreateDto shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .groupId(groupId)
            .build();

        mockMvc.perform(post("/api/v1/users/" + userId + "/shopping-lists")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListCreateDto)))
            .andExpect(status().isOk());

        assertAll(
            () -> assertThat(shoppingListRepository.findAll()).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAll().getFirst().getName()).isEqualTo("Test Shopping List"),
            () -> assertThat(shoppingListRepository.findAllByOwnerId(userId)).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAllByOwnerId(userId).stream()
                .anyMatch(shoppingList -> shoppingList.getName().equals("Test Shopping List"))).isTrue(),
            () -> assertThat(shoppingListRepository.findAllByGroupId(groupId)).isNotEmpty(),
            () -> assertThat(shoppingListRepository.findAllByGroupId(groupId).stream()
                .anyMatch(shoppingList -> shoppingList.getName().equals("Test Shopping List"))).isTrue()
        );
        shoppingListRepository.deleteAll();

    }

}
