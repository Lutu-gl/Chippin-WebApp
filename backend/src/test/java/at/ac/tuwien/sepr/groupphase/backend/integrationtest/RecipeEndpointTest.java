package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RecipeEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    RecipeService recipeService;

    @Autowired
    CustomUserDetailService userDetailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private PantryRepository pantryRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private ItemListRepository itemListRepository;
    @Autowired
    private ShoppingListRepository shoppingListRepository;


    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    //TODO isPublic is not being tested yet
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    private Recipe recipe;
    private Recipe emptyRecipe;
    private Item item;


    @BeforeEach
    public void beforeEach() {
        shoppingListRepository.deleteAll();
        itemListRepository.deleteAll();
        budgetRepository.deleteAll();
        recipeRepository.deleteAll();
        itemRepository.deleteAll();
        pantryRepository.deleteAll();
        friendshipRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();


        item = Item.builder()
            .description("Potato")
            .amount(1)
            .unit(Unit.Gram)
            .build();

        recipe = Recipe.builder()
            .name("Test Recipe")
            .description("This is here to Test recipes")
            .isPublic(true)
            .portionSize(1)
            .likes(0).dislikes(0).build();
        recipe.addIngredient(item);
        recipeRepository.save(recipe);

        emptyRecipe = Recipe.builder()
            .name("Empty Recipe")
            .description("This Recipe has no Ingredients")
            .isPublic(true)
            .portionSize(1)
            .ingredients(new ArrayList<>())
            .likes(0).dislikes(0).build();
        recipeRepository.save(emptyRecipe);
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser
    public void createRecipeSuccessfully_then201() throws Exception {
        ItemCreateDto item1 = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        ItemCreateDto item2 = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Banana").build();
        /*UserRegisterDto userRegisterDto = UserRegisterDto.builder()
            .email("test@example.com")
            .password("Test1234")
            .build();
        userDetailService.register(userRegisterDto, false);*/

        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
            .name("Carrot Banana")
            .description("this is a test")
            .isPublic(false)
            .portionSize(1)
            //.owner(userDetailService.findApplicationUserByEmail("test@example.com"))
            .build();
        ArrayList<ItemCreateDto> toAdd = new ArrayList<>();
        toAdd.add(item1);
        toAdd.add(item2);
        recipeCreateDto.setIngredients(toAdd);


        String groupJson = objectMapper.writeValueAsString(recipeCreateDto);
        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/group/recipe/create")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        RecipeDetailDto recipeDetailDto = objectMapper.readerFor(RecipeDetailDto.class)
            .readValue(body);

        LOGGER.debug("detailDto: " + recipeDetailDto);

        assertEquals(recipeDetailDto.getName(), recipeCreateDto.getName());
        assertEquals(recipeDetailDto.getDescription(), recipeCreateDto.getDescription());
        assertEquals(recipeDetailDto.getIngredients().size(), recipeCreateDto.getIngredients().size());


    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser
    public void createRecipeWithInvalidRecipeGets400() throws Exception {

        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder().isPublic(true).portionSize(0).build();

        String groupJson = objectMapper.writeValueAsString(recipeCreateDto);
        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/group/recipe/create")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsByteArray();


    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser
    public void getByIdOnUnknownId_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/recipe", 0)))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    @WithMockUser
    public void updateExistingRecipe_ChangesSuccessfully_Then200() throws Exception {
        recipe.setId(emptyRecipe.getId());
        String groupJson = objectMapper.writeValueAsString(recipe);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/group/recipe/update")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@example.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RecipeDetailDto recipeDetailDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeDetailDto.class);
        LOGGER.debug("detailDto: " + recipeDetailDto);

        assertEquals(recipeDetailDto.getId(), recipe.getId());
        assertEquals(recipeDetailDto.getName(), recipe.getName());
        assertEquals(recipeDetailDto.getPortionSize(), recipe.getPortionSize());
    }

    @Test
    @WithMockUser
    public void givenEmptyRecipe_whenFindById_thenEmptyList()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/recipe", emptyRecipe.getId())))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        RecipeDetailDto recipeDetailDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeDetailDto.class);
        LOGGER.debug("detailDto: " + recipeDetailDto);
        LOGGER.debug("detailDto2: " + recipeDetailDto.getIngredients());

        assertEquals(0, recipeDetailDto.getIngredients().size());
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser
    public void givenRecipeWithOneItem_whenFindById_thenListWithSizeOneAndCorrectItem()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/recipe", recipe.getId())))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RecipeDetailDto recipeDetailDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeDetailDto.class);

        assertEquals(1, recipeDetailDto.getIngredients().size());
        ItemDto itemDto = recipeDetailDto.getIngredients().get(0);
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }


    @Test
    @Rollback
    @Transactional
    @WithMockUser
    public void givenRecipeWithOneItemAndMatchingDescription_whenSearchItemsInRecipe_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(MessageFormat.format("/api/v1/group/{0}/recipe/search", recipe.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
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
    @Rollback
    @Transactional
    @WithMockUser
    public void givenNothing_whenAddItemToRecipe_thenItemWithAllPropertiesPlusId()
        throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MessageFormat.format("/api/v1/group/{0}/recipe", recipe.getId()))
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
    @Rollback
    @Transactional
    @WithMockUser
    public void givenNothing_whenAddInvalidItemToRecipe_then400()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        MvcResult mvcResult = this.mockMvc.perform(post(MessageFormat.format("/api/v1/group/{0}/recipe", recipe.getId()))
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
    @Rollback
    @Transactional
    @WithMockUser
    public void givenNothing_whenDeleteExistingItem_thenItemDeleted()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(String.format("/api/v1/group/%d/recipe/%d", recipe.getId(), item.getId()))
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@email.com", ADMIN_ROLES))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertAll(
            () -> assertFalse(recipeRepository.findById(recipe.getId()).get().getIngredients().contains(item)),
            () -> assertFalse(itemRepository.existsById(item.getId()))
        );
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser
    public void givenNothing_whenPut_thenItemWithAllProperties()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build());

        MvcResult mvcResult = this.mockMvc.perform(put(MessageFormat.format("/api/v1/group/{0}/recipe", recipe.getId()))
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
    }
}
