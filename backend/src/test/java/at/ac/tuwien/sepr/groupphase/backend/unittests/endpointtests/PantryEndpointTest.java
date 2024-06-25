package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PantryEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryService pantryService;

    @MockBean
    private ItemMapper itemMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityService securityService;

    @Test
    @WithMockUser(roles = "USER")
    void findAllInPantry() throws Exception {

        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.isGroupMember(any())).thenReturn(true);

        long pantryId = 1L;
        List<PantryItemDto> items = List.of(PantryItemDto.builder().id(1L).description("Test Item").amount(1).unit(Unit.Gram).lowerLimit(null).build());

        Mockito.when(pantryService.findAllItems(anyLong())).thenReturn(List.of());
        Mockito.when(itemMapper.listOfPantryItemsToListOfPantryItemDto(any())).thenReturn(items);

        mockMvc.perform(get("/api/v1/group/{pantryId}/pantry", pantryId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void addItemToPantry() throws Exception {

        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.isGroupMember(any())).thenReturn(true);

        long pantryId = 1L;
        PantryItemCreateDto itemCreateDto = PantryItemCreateDto.builder().description("Test Item").unit(Unit.Piece).amount(1).lowerLimit(1L).build();
        ItemDto itemDto = ItemDto.builder().description("Test Item").unit(Unit.Piece).amount(1).id(1L).build();

        Mockito.when(pantryService.addItemToPantry(any(), anyLong())).thenReturn(Item.builder().description("Test Item").unit(Unit.Piece).amount(1).id(1L).build());
        Mockito.when(itemMapper.pantryItemCreateDtoToPantryItem(any())).thenReturn(PantryItem.builder().description("Test Item").unit(Unit.Piece).amount(1).lowerLimit(1L).build());
        Mockito.when(itemMapper.itemToItemDto(any())).thenReturn(itemDto);

        mockMvc.perform(post("/api/v1/group/{pantryId}/pantry", pantryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemCreateDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteItem() throws Exception {

        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.isGroupMember(any())).thenReturn(true);

        long pantryId = 1L;
        long itemId = 1L;

        mockMvc.perform(delete("/api/v1/group/{pantryId}/pantry/{itemId}", pantryId, itemId))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteItems() throws Exception {

        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.isGroupMember(any())).thenReturn(true);

        long pantryId = 1L;

        mockMvc.perform(delete("/api/v1/group/{pantryId}/pantry", pantryId)
                .param("itemIds", "1", "2"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void updateItem() throws Exception {

        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.isGroupMember(any())).thenReturn(true);

        long pantryId = 1L;

        PantryItemDto itemDto = PantryItemDto.builder().id(1L).description("Test Item updated").unit(Unit.Piece).amount(2).lowerLimit(1L).build();
        ItemDto updatedItemDto = ItemDto.builder().description("Test Item updated").unit(Unit.Piece).amount(2).id(1L).build();

        Mockito.when(pantryService.updateItem(any(), anyLong())).thenReturn(new Item());
        Mockito.when(itemMapper.pantryItemDtoToPantryItem(any())).thenReturn(new PantryItem());
        Mockito.when(itemMapper.itemToItemDto(any())).thenReturn(updatedItemDto);

        mockMvc.perform(put("/api/v1/group/{pantryId}/pantry", pantryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllMissingItems() throws Exception {

        when(securityService.hasCorrectId(any())).thenReturn(true);
        when(securityService.isGroupMember(any())).thenReturn(true);

        long pantryId = 1L;
        List<PantryItemDto> missingItems = List.of();

        Mockito.when(pantryService.findAllMissingItems(anyLong())).thenReturn(missingItems);

        mockMvc.perform(get("/api/v1/group/{pantryId}/pantry/missing", pantryId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}