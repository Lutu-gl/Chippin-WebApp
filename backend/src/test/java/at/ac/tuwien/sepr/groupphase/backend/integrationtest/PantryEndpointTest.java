package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PantryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PantryEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PantryRepository pantryRepository;

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

    private Pantry pantry;
    private Pantry emptyPantry;
    private Item item;

    @BeforeEach
    public void beforeEach() {
        pantryRepository.deleteAll();
        itemRepository.deleteAll();

        item = Item.builder()
            .description("Potato")
            .amount(1)
            .unit(Unit.Kilogram)
            .build();

        pantry = new Pantry();
        pantry.addItem(item);
        pantryRepository.save(pantry);

        emptyPantry = new Pantry();
        pantryRepository.save(emptyPantry);
    }

    @Test
    public void givenEmptyPantry_whenFindAllInPantry_thenEmptyList()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry", emptyPantry.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        LOGGER.debug("detailDto: " + detailDto);
        LOGGER.debug("detailDto2: " + detailDto.getItems());
        //List<ItemDetailDto> itemDetailDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
        //    ItemDetailDto[].class));

        assertEquals(0, detailDto.getItems().size());
    }

    @Test
    public void givenPantryWithOneItem_whenFindAllInPantry_thenListWithSizeOneAndCorrectItem()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry", pantry.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        //List<ItemDetailDto> itemDetailDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
        //    ItemDetailDto[].class));

        assertEquals(1, detailDto.getItems().size());
        ItemDetailDto itemDetailDto = detailDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDetailDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDetailDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDetailDto.getUnit())
        );
    }

    @Test
    public void givenPantryWithOneItemAndMatchingDescription_whenSearchItemsInPantry_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry/search", pantry.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .queryParam("details", "otat")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        //List<ItemDetailDto> itemDetailDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
        //    ItemDetailDto[].class));

        assertEquals(1, detailDto.getItems().size());
        ItemDetailDto itemDetailDto = detailDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDetailDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDetailDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDetailDto.getUnit())
        );
    }
}
