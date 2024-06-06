package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBevorAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemMergeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry.PantryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PantryEndpointTest extends BaseTestGenAndClearBevorAfterEach {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PantryRepository pantryRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PantryItemRepository pantryItemRepository;

    @SpyBean
    private SecurityService securityService;

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenInvalidPantryId_whenFindAllInPantry_then403()
        throws Exception {
        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/pantry", -1)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenEmptyPantry_whenFindAllInPantry_thenEmptyList()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup3").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/pantry", groupId)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        assertAll(
            () -> assertEquals(0, detailDto.getItems().size())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenPantryWithOneItem_whenFindAllInPantry_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup2").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/{groupId}/pantry", groupId))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        PantryItem item = pantryItemRepository.findByPantryOrderById(groupRepository.findByGroupName("PantryTestGroup2").getPantry()).get(0);

        PantryItemDto itemDto = detailDto.getItems().get(0);
        assertAll(
            () -> assertEquals(1, detailDto.getItems().size()),
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenPantryWithOneItemAndMatchingDescription_whenSearchItemsInPantry_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup2").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/{groupId}/pantry/search", groupId)
                .queryParam("details", "otat")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        PantryItem item = pantryItemRepository.findByPantryOrderById(groupRepository.findByGroupName("PantryTestGroup2").getPantry()).get(0);

        PantryItemDto itemDto = detailDto.getItems().get(0);
        assertAll(
            () -> assertEquals(1, detailDto.getItems().size()),
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenAddItemToPantry_thenReturnItemWithAllPropertiesPlusId()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup3").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(String.format("/api/v1/group/%d/pantry", groupId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ItemDto itemDto = objectMapper.readValue(response.getContentAsByteArray(), ItemDto.class);

        assertAll(
            () -> assertEquals(itemCreateDto.getDescription(), itemDto.getDescription()),
            () -> assertEquals(itemCreateDto.getAmount(), itemDto.getAmount()),
            () -> assertEquals(itemCreateDto.getUnit(), itemDto.getUnit()),
            () -> assertNotNull(itemDto.getId())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenAddItemToPantry_thenItemPersisted()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup3").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/group/{0}/pantry", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ItemDto itemDto = objectMapper.readValue(response.getContentAsByteArray(), ItemDto.class);
        PantryItem item = pantryItemRepository.findById(itemDto.getId()).get();

        assertAll(
            () -> assertNotNull(item),
            () -> assertEquals(itemCreateDto.getDescription(), item.getDescription()),
            () -> assertEquals(itemCreateDto.getAmount(), item.getAmount()),
            () -> assertEquals(itemCreateDto.getUnit(), item.getUnit())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenAddInvalidItemToPantry_then400()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup3").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        MvcResult mvcResult = this.mockMvc.perform(post(String.format("/api/v1/group/%d/pantry", groupId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenDeleteExistingItem_thenItemDeleted()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup2").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        PantryItem item = pantryItemRepository.findByPantryOrderById(groupRepository.findByGroupName("PantryTestGroup2").getPantry()).get(0);

        MvcResult mvcResult = this.mockMvc.perform(delete(String.format("/api/v1/group/%d/pantry/%d", groupId, item.getId()))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<PantryItem> items = pantryRepository.findById(groupId).get().getItems();

        assertAll(
            () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus()),
            //PantryTestGroup2 is generated with exactly one item
            () -> assertTrue(items.isEmpty()),
            () -> assertFalse(itemRepository.existsById(item.getId()))
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenUpdate_thenReturnItemWithAllProperties()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup2").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        GroupEntity group = groupRepository.findByGroupName("PantryTestGroup2");
        PantryItem item = group.getPantry().getItems().get(0);
        ItemDto dto = ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build();
        String body = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform(put(String.format("/api/v1/group/%d/pantry", group.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ItemDto returned = objectMapper.readValue(response.getContentAsByteArray(), ItemDto.class);

        assertAll(
            () -> assertEquals(dto.getUnit(), returned.getUnit()),
            () -> assertEquals(dto.getAmount(), returned.getAmount()),
            () -> assertEquals(dto.getDescription(), returned.getDescription()),
            () -> assertEquals(dto.getId(), returned.getId())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenUpdate_thenItemUpdated()
        throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup2").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        GroupEntity group = groupRepository.findByGroupName("PantryTestGroup2");
        PantryItem item = group.getPantry().getItems().get(0);
        ItemDto dto = ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build();
        String body = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform(put(String.format("/api/v1/group/%d/pantry", group.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        PantryItem persisted = pantryItemRepository.findById(dto.getId()).get();

        assertAll(
            () -> assertEquals(dto.getUnit(), persisted.getUnit()),
            () -> assertEquals(dto.getAmount(), persisted.getAmount()),
            () -> assertEquals(dto.getDescription(), persisted.getDescription()),
            () -> assertEquals(dto.getId(), persisted.getId())
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenMerge_thenItemWithAllPropertiesAndOtherItemDeleted() throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup1").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        PantryItem item1 = pantryItemRepository.findByPantryOrderById(groupRepository.findByGroupName("PantryTestGroup1").getPantry()).get(0);
        PantryItemDto itemDto = PantryItemDto.builder()
            .unit(item1.getUnit())
            .lowerLimit(item1.getLowerLimit())
            .description(item1.getDescription())
            .amount(item1.getAmount() + 100)
            .id(item1.getId()).build();
        PantryItem item2 = pantryItemRepository.findByPantryOrderById(groupRepository.findByGroupName("PantryTestGroup1").getPantry()).get(1);

        PantryItemMergeDto mergeDto = PantryItemMergeDto.builder().itemToDeleteId(item2.getId()).result(itemDto).build();
        String body = objectMapper.writeValueAsString(mergeDto);

        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/group/{0}/pantry/merged", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        PantryItemDto returned = objectMapper.readValue(response.getContentAsByteArray(), PantryItemDto.class);
        Item fromRepository = itemRepository.findById(item1.getId()).get();

        assertAll(
            () -> assertEquals(item1.getAmount() + 100, returned.getAmount()),
            () -> assertEquals(fromRepository.getAmount(), returned.getAmount()),
            () -> assertEquals(fromRepository.getUnit(), returned.getUnit()),
            () -> assertEquals(fromRepository.getDescription(), returned.getDescription()),
            () -> assertEquals(fromRepository.getId(), returned.getId()),
            () -> assertFalse(itemRepository.existsById(item2.getId()))
        );
    }

    /*
    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenGetRecipes_thenReturnListOfRecipesContainingItemsStoredInPantry() throws Exception {

        Long id = userRepository.findByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("PantryTestGroup1").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        byte[] body = this.mockMvc.perform(get("/api/v1/group/" + groupId + "/pantry/recipes")
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        List<RecipeListDto> recipes = objectMapper.readValue(body, new TypeReference<List<RecipeListDto>>() {
        });
        assertAll(
            () -> assertEquals(2, recipes.size()),
            () -> assertEquals("Test 1", recipes.get(0).getName()),
            () -> assertEquals("Test 2", recipes.get(1).getName())
        );
    }

     */
}
