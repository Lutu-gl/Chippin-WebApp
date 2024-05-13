package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListEndpointTest {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @MockBean
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
        .budget(100.0F)
        .build();

    @Test
    @WithMockUser(username = "test")
    public void givenValidShoppingListCreateDto_whenCreateShoppingListForGroup_shoppingListForGroupIsPersisted() throws Exception {
        // Get a group id
        var group = GroupEntity.builder()
            .id(-1L)
            .groupName("Test Group")
            .users(Set.of())
            .build();
        var savedGroup = groupRepository.save(group);
        Long groupId = savedGroup.getId();

        when(securityService.isGroupMember(groupId)).thenReturn(true);

        mockMvc.perform(post("/api/v1/" + groupId + "/shoppinglist")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListCreateDto)))
            .andExpect(status().isOk());

        assertThat(shoppingListRepository.findAll()).isNotEmpty();
        assertThat(shoppingListRepository.findAll().getFirst().getName()).isEqualTo("Test Shopping List");
    }

}
