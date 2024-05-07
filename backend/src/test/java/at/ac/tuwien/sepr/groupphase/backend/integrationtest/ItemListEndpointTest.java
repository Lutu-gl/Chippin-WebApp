package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
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
public class ItemListEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemListRepository itemListRepository;

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

    private ItemList itemList;
    private ItemList emptyItemList;
    private Item item;

    @BeforeEach
    public void beforeEach() {
        itemListRepository.deleteAll();
        itemRepository.deleteAll();

        item = Item.builder()
            .description("Potato")
            .amount(1)
            .unit(Unit.Kilogram)
            .build();

        itemList = ItemList.builder()
            .name("Test Item List")
            .build();
        itemList.addItem(item);
        itemListRepository.save(itemList);

        emptyItemList = ItemList.builder()
            .name("Empty Item List")
            .build();
        itemListRepository.save(emptyItemList);
    }

    @Test
    public void givenEmptyItemList_whenFindAllInItemList_thenEmptyList()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/itemlist", emptyItemList.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemListListDto listDto = objectMapper.readValue(response.getContentAsByteArray(), ItemListListDto.class);
        LOGGER.debug("detailDto: " + listDto);
        LOGGER.debug("detailDto2: " + listDto.getItems());

        assertEquals(0, listDto.getItems().size());
    }

    @Test
    public void givenItemListWithOneItem_whenFindAllInItemList_thenListWithSizeOneAndCorrectItem()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/itemlist", itemList.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemListListDto listDto = objectMapper.readValue(response.getContentAsByteArray(), ItemListListDto.class);

        assertEquals(1, listDto.getItems().size());
        ItemDto itemDto = listDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    public void givenItemListWithOneItemAndMatchingDescription_whenSearchItemsInItemList_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/itemlist/search", itemList.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .queryParam("details", "otat")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemListListDto listDto = objectMapper.readValue(response.getContentAsByteArray(), ItemListListDto.class);

        assertEquals(1, listDto.getItems().size());
        ItemDto itemDto = listDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    public void givenNothing_whenAddItemToItemList_thenItemWithAllPropertiesPlusId()
        throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MessageFormat.format("/api/v1/group/{0}/itemlist", itemList.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
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
    public void givenNothing_whenAddInvalidItemToItemList_then400()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        MvcResult mvcResult = this.mockMvc.perform(post(MessageFormat.format("/api/v1/group/{0}/itemlist", itemList.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenDeleteExistingItem_thenItemDeleted()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(MessageFormat.format("/api/v1/group/{0}/itemlist/{1}", itemList.getId(), item.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
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
    public void givenNothing_whenPut_thenItemWithAllProperties()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build());

        MvcResult mvcResult = this.mockMvc.perform(put(MessageFormat.format("/api/v1/group/{0}/itemlist", itemList.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
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
    }
}
