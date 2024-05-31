package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.entity.Blueprint;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.BluePrintRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BlueprintEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BluePrintRepository bluePrintRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    private Blueprint blueprint;
    private Blueprint emptyBlueprint;
    private Item item;

    @BeforeEach
    public void beforeEach() {

        item = Item.builder()
            .description("Potato")
            .amount(1)
            .unit(Unit.Gram)
            .build();

        blueprint = Blueprint.builder()
            .name("Test Item List")
            .build();
        blueprint.addItem(item);
        bluePrintRepository.save(blueprint);

        emptyBlueprint = Blueprint.builder()
            .name("Empty Item List")
            .build();
        bluePrintRepository.save(emptyBlueprint);
    }

    /*@Test
    @Test
    @WithMockUser
    public void givenEmptyItemList_whenFindAllInItemList_thenEmptyList()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/itemlist", emptyItemList.getId())))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        BlueprintListDto listDto = objectMapper.readValue(response.getContentAsByteArray(), BlueprintListDto.class);
        LOGGER.debug("detailDto: " + listDto);
        LOGGER.debug("detailDto2: " + listDto.getItems());

        assertEquals(0, listDto.getItems().size());
    }

    @Test
    @WithMockUser
    public void givenItemListWithOneItem_whenFindAllInItemList_thenListWithSizeOneAndCorrectItem()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/itemlist", itemList.getId())))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        BlueprintListDto listDto = objectMapper.readValue(response.getContentAsByteArray(), BlueprintListDto.class);

        assertEquals(1, listDto.getItems().size());
        ItemDto itemDto = listDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    @WithMockUser
    public void givenItemListWithOneItemAndMatchingDescription_whenSearchItemsInItemList_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/itemlist/search", itemList.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .queryParam("details", "otat")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        BlueprintListDto listDto = objectMapper.readValue(response.getContentAsByteArray(), BlueprintListDto.class);

        assertEquals(1, listDto.getItems().size());
        ItemDto itemDto = listDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    @WithMockUser
    public void givenNothing_whenAddItemToItemList_thenItemWithAllPropertiesPlusId()
        throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(String.format("/api/v1/group/%d/itemlist", itemList.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemDto itemDto = objectMapper.readValue(response.getContentAsByteArray(), ItemDto.class);

        assertAll(
            () -> assertEquals(itemCreateDto.getDescription(), itemDto.getDescription()),
            () -> assertEquals(itemCreateDto.getAmount(), itemDto.getAmount()),
            () -> assertEquals(itemCreateDto.getUnit(), itemDto.getUnit()),
            () -> assertNotNull(itemDto.getId())
        );
    }

    @Test
    @WithMockUser
    public void givenNothing_whenAddInvalidItemToItemList_then400()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        MvcResult mvcResult = this.mockMvc.perform(post(String.format("/api/v1/group/%d/itemlist", itemList.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser
    public void givenNothing_whenDeleteExistingItem_thenItemDeleted()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(String.format("/api/v1/group/%d/itemlist/%d", itemList.getId(), item.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertAll(
            () -> assertFalse(itemListRepository.findById(itemList.getId()).get().getItems().contains(item)),
            () -> assertFalse(itemRepository.existsById(item.getId()))
        );
    }

    @Test
    @WithMockUser
    public void givenNothing_whenPut_thenItemWithAllProperties()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build());

        MvcResult mvcResult = this.mockMvc.perform(put(String.format("/api/v1/group/%d/itemlist", itemList.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemDto returned = objectMapper.readValue(response.getContentAsByteArray(), ItemDto.class);
        Item fromRepository = itemRepository.findById(item.getId()).get();

        assertAll(
            () -> assertEquals(fromRepository.getUnit(), returned.getUnit()),
            () -> assertEquals(fromRepository.getAmount(), returned.getAmount()),
            () -> assertEquals(fromRepository.getDescription(), returned.getDescription()),
            () -> assertEquals(fromRepository.getId(), returned.getId())
        );
    }*/
}
