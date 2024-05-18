package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList.ShoppingListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListEndpointTest extends BaseTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingListServiceImpl shoppingListService;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test")
    public void givenValidShoppingListCreateDto_whenCreateShoppingListForGroup_thenNoException() throws Exception {
        when(shoppingListService.createShoppingList(any(), any())).thenReturn(
            ShoppingList.builder().id(1L).name("Test Shopping List").owner(null).items(List.of()).build()
        );
        when(securityService.isGroupMember(any())).thenReturn(true);
        var shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .ownerId(1L)
            .build();

        mockMvc.perform(post("/api/v1/1/shoppinglist")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(shoppingListCreateDto)))
            .andExpect(status().isOk());

        verify(shoppingListService, times(1)).createShoppingList(any(), any());
    }


    @Test
    @WithMockUser(username = "test")
    public void givenValidShoppingListId_whenGetShoppingList_thenNoException() throws Exception {
        when(shoppingListService.getShoppingList(-1L)).thenReturn(
            ShoppingList.builder().id(-1L).name("Test Shopping List").owner(null).items(List.of())
                .group(GroupEntity.builder().id(-1L).build())
                .build()
        );
        when(securityService.isGroupMember(-1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/-1/shoppinglist/-1"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(
                ShoppingListDetailDto.builder().id(-1L).groupId(-1L).name("Test Shopping List").owner(null).items(List.of()).build()
            )));

        verify(shoppingListService, times(1)).getShoppingList(-1L);
    }


    @Test
    @WithMockUser(username = "test")
    public void givenValidShoppingListId_whenDeleteShoppingList_thenNoException() throws Exception {
        when(securityService.isGroupMember(-1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/-1/shoppinglist/-1"))
            .andExpect(status().isOk());

        verify(shoppingListService, times(1)).deleteShoppingList(-1L);

    }


    @Test
    @WithMockUser(username = "test")
    public void givenValidGroupId_whenGetShoppingListsForGroup_thenNoException() throws Exception {
        when(shoppingListService.getShoppingListsForGroup(-1L)).thenReturn(List.of());
        when(securityService.isGroupMember(-1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/-1/shoppinglist"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

}
