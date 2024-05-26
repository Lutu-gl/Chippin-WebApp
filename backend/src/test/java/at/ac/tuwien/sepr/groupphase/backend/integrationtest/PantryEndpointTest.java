package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemMergeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry.PantryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PantryEndpointTest extends BaseTest {
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
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private GroupMapper groupMapper;

    @SpyBean
    private RecipeRepository recipeRepository;

    @SpyBean
    private PantryItemRepository pantryItemRepository;

    @SpyBean
    private SecurityService securityService;

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    private GroupEntity group;
    private GroupEntity groupEmptyPantry;
    private GroupEntity group3;
    private PantryItem item;
    private PantryItem item2;
    private PantryItem item3;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1GE@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");
        userRepository.save(user1);

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2GE@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");
        userRepository.save(user2);

        item = PantryItem.builder()
            .description("Potato")
            .amount(100)
            .unit(Unit.Gram)
            .build();

        item2 = PantryItem.builder()
            .description("Potato")
            .amount(300)
            .unit(Unit.Gram)
            .build();

        item3 = PantryItem.builder()
            .description("Potato")
            .amount(2)
            .unit(Unit.Piece)
            .build();

        group = GroupEntity.builder().groupName("T1").users(Set.of(user1)).build();
        Pantry pantry = Pantry.builder().build();
        pantry.setGroup(group);
        group.setPantry(pantry);

        group.getPantry().addItem(item);
        groupRepository.save(group);

        groupEmptyPantry = GroupEntity.builder().groupName("T2").users(Set.of(user1)).build();
        Pantry pantry2 = Pantry.builder().build();
        pantry2.setGroup(groupEmptyPantry);
        groupEmptyPantry.setPantry(pantry2);

        groupRepository.save(groupEmptyPantry);

        group3 = GroupEntity.builder().groupName("T3").users(Set.of(user1)).build();
        Pantry pantry3 = Pantry.builder().build();
        pantry3.setGroup(group3);
        group3.setPantry(pantry3);

        group3.getPantry().addItem(item2);
        group3.getPantry().addItem(item3);
        groupRepository.save(group3);

    }

    @Test
    public void givenInvalidPantryId_whenFindAllInPantry_then403()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry", -1))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void givenEmptyPantry_whenFindAllInPantry_thenEmptyList()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry", groupEmptyPantry.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(0, detailDto.getItems().size())
        );
    }

    @Test
    public void givenPantryWithOneItem_whenFindAllInPantry_thenListWithSizeOneAndCorrectItem()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry", group.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);

        assertEquals(1, detailDto.getItems().size());
        PantryItemDto itemDto = detailDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    public void givenPantryWithOneItemAndMatchingDescription_whenSearchItemsInPantry_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/pantry/search", group.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES))
                .queryParam("details", "otat")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PantryDetailDto detailDto = objectMapper.readValue(response.getContentAsByteArray(), PantryDetailDto.class);

        assertEquals(1, detailDto.getItems().size());
        PantryItemDto itemDto = detailDto.getItems().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }

    @Test
    public void givenNothing_whenAddItemToPantry_thenItemWithAllPropertiesPlusId()
        throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MessageFormat.format("/api/v1/group/{0}/pantry", group.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES))
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
    public void givenNothing_whenAddInvalidItemToPantry_then400()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        MvcResult mvcResult = this.mockMvc.perform(post(MessageFormat.format("/api/v1/group/{0}/pantry", group.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES))
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
        MvcResult mvcResult = this.mockMvc.perform(delete(String.format("/api/v1/group/%d/pantry/%d", group.getId(), item.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertAll(
            () -> assertFalse(pantryRepository.findById(group.getId()).get().getItems().contains(item)),
            () -> assertFalse(itemRepository.existsById(item.getId()))
        );
    }

    @Test
    public void givenNothing_whenUpdate_thenItemWithAllProperties()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build());

        MvcResult mvcResult = this.mockMvc.perform(put(MessageFormat.format("/api/v1/group/{0}/pantry", group.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES))
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

    @Test
    public void givenNothing_whenMerge_thenItemWithAllPropertiesAndOtherItemDeleted() throws Exception {
        PantryItemDto pantryItemDto = PantryItemDto.builder()
            .description(item2.getDescription())
            .unit(item2.getUnit())
            .lowerLimit(item2.getLowerLimit())
            .id(item2.getId())
            .amount(item2.getAmount() + 100).build();
        String body = objectMapper.writeValueAsString(PantryItemMergeDto.builder().result(pantryItemDto).itemToDeleteId(item3.getId()).build());

        MvcResult mvcResult = this.mockMvc.perform(put(MessageFormat.format("/api/v1/group/{0}/pantry/merged", group3.getId()))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PantryItemDto returned = objectMapper.readValue(response.getContentAsByteArray(), PantryItemDto.class);
        Item fromRepository = itemRepository.findById(item2.getId()).get();

        assertAll(
            () -> assertEquals(fromRepository.getUnit(), returned.getUnit()),
            () -> assertEquals(fromRepository.getAmount(), returned.getAmount()),
            () -> assertEquals(400, returned.getAmount()),
            () -> assertEquals(fromRepository.getDescription(), returned.getDescription()),
            () -> assertEquals(fromRepository.getId(), returned.getId()),
            () -> assertFalse(itemRepository.existsById(item3.getId()))
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenGetRecipes_thenReturnListOfRecipesContainingItemsStoredInPantry() throws Exception {
        Long userId = userRepository.findByEmail("user1@example.com").getId();
        GroupDetailDto group = groupMapper.groupEntityToGroupDto(groupRepository.findByGroupName("groupExample1"));
        Long groupId = group.getId();
        when(securityService.hasCorrectId(userId)).thenReturn(true);
        when(securityService.isGroupMember(groupId)).thenReturn(true);

        List<PantryItem> ingredients = List.of(
            PantryItem.builder().description("Water").unit(Unit.Milliliter).amount(100).build(),
            PantryItem.builder().description("Milk").unit(Unit.Milliliter).amount(200).build(),
            PantryItem.builder().description("Milk").unit(Unit.Piece).amount(1).build(),
            PantryItem.builder().description("Sugar").unit(Unit.Gram).amount(200).build(),
            PantryItem.builder().description("Banana").unit(Unit.Gram).amount(300).build(),
            PantryItem.builder().description("Honey").unit(Unit.Milliliter).amount(10).build(),
            PantryItem.builder().description("Tomato").unit(Unit.Gram).amount(100).build());
        List<Recipe> recipeList = List.of(
            Recipe.builder().name("Recipe1").portionSize(1).description("Description1").ingredients(List.of(ingredients.get(0))).build(),
            Recipe.builder().name("Recipe2").portionSize(1).description("Description2").ingredients(List.of(ingredients.get(0), ingredients.get(1))).build(),
            Recipe.builder().name("Recipe3").portionSize(1).description("Description3").ingredients(List.of(ingredients.get(1))).build(),
            Recipe.builder().name("Recipe4").portionSize(1).description("Description4").ingredients(List.of(ingredients.get(2))).build(),
            Recipe.builder().name("Recipe5").portionSize(1).description("Description5").ingredients(List.of(ingredients.get(0), ingredients.get(1), ingredients.get(2))).build());

        when(pantryItemRepository.findAll()).thenReturn(List.of(ingredients.get(0), ingredients.get(1)));

        when(recipeRepository.findRecipeByPantry(groupId)).thenReturn(List.of(recipeList.get(0), recipeList.get(1), recipeList.get(2), recipeList.get(4)));

        byte[] body = this.mockMvc.perform(get("/api/v1/group/" + group.getId() + "/pantry/recipes")
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        Collection<RecipeListDto> recipes = objectMapper.readerFor(Collection.class).readValue(body);

        assertAll(
            () -> assertEquals(4, recipes.size())
        );
    }
}
