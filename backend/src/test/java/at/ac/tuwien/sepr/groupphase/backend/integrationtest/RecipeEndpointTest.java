package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBevorAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RecipeEndpointTest extends BaseTestGenAndClearBevorAfterEach {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userDetailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemMapper itemMapper;


    @Autowired
    RecipeMapper recipeMapper;

    @Autowired
    PantryService pantryService;

    @Autowired
    private GroupService groupService;


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

    private Recipe recipe;
    private Recipe emptyRecipe;
    private Item item;
    private ApplicationUser user;


    @BeforeEach
    public void beforeEach() throws UserAlreadyExistsException {
        userDetailService.register(UserRegisterDto.builder().email("tester@at").password("RezeptTest1").build(), false);
        user = userDetailService.findApplicationUserByEmail("tester@at");

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
            .owner(user)
            .likes(0).dislikes(0).build();
        recipe.addIngredient(item);
        recipe = recipeRepository.save(recipe);

        emptyRecipe = Recipe.builder()
            .name("Empty Recipe")
            .description("This Recipe has no Ingredients")
            .isPublic(false)
            .portionSize(1)
            .owner(user)
            .ingredients(new ArrayList<>())
            .likes(0).dislikes(0).build();
        emptyRecipe = recipeRepository.save(emptyRecipe);
    }


    /*@Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    public void createRecipeSuccessfully_then201() throws Exception {
        RecipeCreateWithoutUserDto recipeCreateDto = RecipeCreateWithoutUserDto.builder()
            .name("New Recipe")
            .description("This is a test recipe")
            .portionSize(4)
            .isPublic(true)
            .ingredients(List.of(
                ItemCreateDto.builder().description("new item").amount(4).unit(Unit.Piece).build()
                , ItemCreateDto.builder().description("new item2").amount(4).unit(Unit.Piece).build()))
            .build();

        String recipeJson = objectMapper.writeValueAsString(recipeCreateDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/group/recipe/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson))
            .andExpect(status().isCreated())
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RecipeDetailDto responseDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeDetailDto.class);
        assertEquals("New Recipe", responseDto.getName());
        assertEquals("This is a test recipe", responseDto.getDescription());
    }*/


    @Test
    @Rollback
    @WithMockUser
    public void createRecipeWithInvalidRecipeGets400() throws Exception {

        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder().isPublic(true).portionSize(0).build();

        String groupJson = objectMapper.writeValueAsString(recipeCreateDto);
        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/group/recipe/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsByteArray();


    }

    @Test
    @Rollback
    @WithMockUser
    public void getByIdOnUnknownId_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/recipe", 0)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    @Rollback
    @WithMockUser
    public void updateExistingRecipe_ChangesSuccessfully_Then200() throws Exception {

        RecipeDetailDto newRecipe = RecipeDetailDto.builder().id(recipe.getId()).owner(recipe.getOwner()).portionSize(6)
            .description("for a test").name("differentName").ingredients(itemMapper.listOfItemsToListOfItemDto(recipe.getIngredients()))
            .isPublic(true).likes(0).dislikes(0).build();
        String groupJson = objectMapper.writeValueAsString(newRecipe);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/group/recipe/update")
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
        assertEquals(recipeDetailDto.getName(), "differentName");
        assertEquals(recipeDetailDto.getPortionSize(), 6);


    }

    @Test
    @WithMockUser
    public void givenEmptyRecipe_whenFindById_thenEmptyList()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/recipe", emptyRecipe.getId())))
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
    @WithMockUser
    public void givenRecipeWithOneItem_whenFindById_thenListWithSizeOneAndCorrectItem()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/recipe", recipe.getId())))
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


    /*@Test
    @Rollback
    @Transactional
    @WithMockUser
    public void givenRecipeWithOneItemAndMatchingDescription_whenSearchItemsInRecipe_thenListWithSizeOneAndCorrectItem()
        throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("/api/v1/group/%d/recipe/search", recipe.getId()))
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
    }*/


    @Test
    @Rollback
    @WithMockUser
    public void givenNothing_whenAddInvalidItemToRecipe_then400()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        MvcResult mvcResult = this.mockMvc.perform(post(String.format("/api/v1/group/%d/recipe", recipe.getId()))
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
    @WithMockUser
    public void givenNothing_whenDeleteExistingItem_thenItemDeleted()
        throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(String.format("/api/v1/group/%d/recipe/%d", recipe.getId(), item.getId()))
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
    @WithMockUser(username = "tester@at", roles = "USER")
    public void likeRecipeSuccessfully() throws Exception {
        String groupJson = objectMapper.writeValueAsString(recipe);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/group/recipe/{0}/like", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();


        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        Recipe result = recipeRepository.findById(recipe.getId()).get();
        ApplicationUser resultUser = userDetailService.findApplicationUserByEmail("tester@at");
        assertEquals(1, result.getLikedByUsers().size());
        assertTrue(resultUser.getLikedRecipes().stream().anyMatch(o -> o.getId().equals(result.getId())));

        assertEquals(resultUser.getLikedRecipes().iterator().next().getId(), result.getId());
        assertEquals(resultUser.getId(), result.getLikedByUsers().iterator().next().getId());
    }

    @Test
    @Rollback
    @WithMockUser(username = "tester@at", roles = "USER")
    public void dislikeRecipeSuccessfully() throws Exception {
        String groupJson = objectMapper.writeValueAsString(recipe);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/group/recipe/{0}/dislike", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();


        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        Recipe result = recipeRepository.findById(recipe.getId()).get();
        ApplicationUser resultUser = userDetailService.findApplicationUserByEmail("tester@at");
        assertEquals(1, result.getDislikedByUsers().size());
        assertTrue(resultUser.getDislikedRecipes().stream().anyMatch(o -> o.getId().equals(result.getId())));

        assertEquals(resultUser.getDislikedRecipes().iterator().next().getId(), result.getId());
        assertEquals(resultUser.getId(), result.getDislikedByUsers().iterator().next().getId());
    }

    @Test
    @Rollback
    @WithMockUser(username = "tester@at", roles = "USER")
    public void givenUser_findRecipesByUser_returnsListOfRecipesByUserThen200() throws Exception {
        ItemCreateDto item1 = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        ItemCreateDto item2 = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Banana").build();

        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
            .name("Carrot Banana")
            .description("this is a test")
            .isPublic(false)
            .portionSize(1)
            .owner(user)
            .build();
        ArrayList<ItemCreateDto> toAdd = new ArrayList<>();
        toAdd.add(item1);
        toAdd.add(item2);
        recipeCreateDto.setIngredients(toAdd);

        RecipeDetailDto createdRecipe = recipeService.createRecipe(recipeCreateDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/group/recipe/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RecipeListDto[] resultDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeListDto[].class);

        assertNotNull(resultDto);
        assertTrue(resultDto.length > 1);
        assertNotNull(resultDto[0]);
        assertEquals(resultDto[0].getId(), recipe.getId());
    }

    @Test
    @Rollback
    @WithMockUser(username = "tester@at", roles = "USER")
    public void getPublicRecipeOrderedByLikes_returnsAllPublicRecipesOrderedSuccessfully() throws Exception {

        Recipe recipe1 = Recipe.builder()
            .name("Carrot Banana")
            .description("this is a test")
            .isPublic(true)
            .portionSize(1)
            .likes(1)
            .owner(user)
            .build();
        Recipe recipe2 = Recipe.builder()
            .name("new Carrot Banana")
            .description("this is a test")
            .isPublic(true)
            .portionSize(1)
            .likes(5)
            .owner(userDetailService.findApplicationUserByEmail("tester@at"))
            .build();


        Recipe newRecipe1 = recipeRepository.save(recipe1);
        Recipe newRecipe2 = recipeRepository.save(recipe2);


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/group/recipe/global")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        RecipeListDto[] resultDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeListDto[].class);

        assertNotNull(resultDto);
        assertNotNull(resultDto[0]);
        assertNotNull(resultDto[1]);


    }

    @Test
    @Rollback
    @WithMockUser
    public void givenNothing_DeleteExistingRecipe_returns204_RecipeDoesntExistAnymore() throws Exception {
        recipeRepository.save(Recipe.builder().id(-5L).isPublic(false).portionSize(0).name("test").owner(user).description("test").build());
        MvcResult mvcResult = this.mockMvc.perform(delete(String.format("/api/v1/group/recipe/%d/delete", -5L))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andReturn();

        Optional<Recipe> optional = recipeRepository.findById(-5L);

        assertFalse(optional.isPresent());


    }

    @Test
    @Rollback
    @WithMockUser(username = "tester@at", roles = "USER")
    public void givenUserEmailAndSearchString_SearchOwnRecipeWithSearchParam_ReturnsListWithOneItem() throws Exception {
        String groupJson = objectMapper.writeValueAsString("Test Recipe");
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/search/own")
                .queryParam("details", "Test Recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        RecipeListDto[] resultDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeListDto[].class);

        assertNotNull(resultDto);
        assertEquals(resultDto.length, 1);
        assertEquals(resultDto[0].getName(), "Test Recipe");
    }

    @Test
    @Rollback
    @WithMockUser(username = "user1@example.com", roles = "USER")
    public void givenWrongUserEmailAndSearchString_SearchOwnRecipeWithSearchParam_Returns404() throws Exception {
        String groupJson = objectMapper.writeValueAsString("Test Recipe");
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/search/own")
                .contentType(MediaType.APPLICATION_JSON)
                .content(groupJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        RecipeGlobalListDto[] resultDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeGlobalListDto[].class);

        assertNotNull(resultDto);
        assertEquals(resultDto.length, 0);

    }

    @Test
    @Rollback
    @WithMockUser(username = "user5@example.com", roles = "USER")
    public void givenSearchString_SearchGlobalRecipeWithSearchParam_ReturnsListWithOneItem() throws Exception {
        String groupJson = objectMapper.writeValueAsString("Test Recipe");
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/search/global")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("details", "Test Recipe")
                .content(groupJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        RecipeGlobalListDto[] resultDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeGlobalListDto[].class);

        assertNotNull(resultDto);
        assertEquals(resultDto.length, 1);
        assertEquals(resultDto[0].getName(), "Test Recipe");
    }

    /*@Test
    @WithMockUser(username = "tester@at", roles = "USER")
    public void givenValidItemAndRecipe_AddItemToRecipeSuccessfully_ThenRecipeWithIngredient() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(String.format("/api/v1/group/%d/recipe", recipe.getId()))
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
    }*/

    @Test
    @Rollback
    @WithMockUser(username = "tester@at", roles = "USER")
    public void givenUserAndRecipe_LikeRecipeSuccessfully_ThenGetLikedRecipeFromUserReturnsLikedRecipeCorrectly() throws Exception {
        recipeService.likeRecipe(recipe.getId(), user);
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/likedlist")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        RecipeListDto[] listDto = objectMapper.readValue(response.getContentAsByteArray(), RecipeListDto[].class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(listDto);
        assertEquals(1, listDto.length);
        assertEquals(recipe.getId(), listDto[0].getId());
    }

    /*@Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    public void GivenUserRecipeAndPantry_WhenRemoveRecipeIngredientsFromPantry_ThenPantryChanges() throws Exception {
        ApplicationUser testUser = userDetailService.findApplicationUserByEmail("user1@example.com");
        GroupCreateDto group = groupService.create(GroupCreateDto.builder().groupName("Fortest").members(Set.of("user2@example.com", "user1@example.com")).build(), "user1@example.com");
        pantryService.addItemToPantry(PantryItem.builder().description("Blueberries").amount(200).unit(Unit.Piece).build(), group.getId());
        Recipe blueberryRecipe = recipeRepository.save(
            Recipe.builder()
                .description("test")
                .name("Blueberries")
                .portionSize(1)
                .owner(userDetailService.findApplicationUserByEmail("user1@example.com"))
                .isPublic(true).ingredients(List.of(Item.builder().description("Blueberries").amount(100).unit(Unit.Piece).build())).build());
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/group/recipe/{0}/pantry/{1}/{2}", blueberryRecipe.getId(), group.getId(), 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String result = response.getContentAsString();
        PantryItem changedItem = pantryService.findItemsByDescription("Blueberries", group.getId()).getFirst();
        assertNotNull(result);
        assertEquals("Blueberries", result);
        assertEquals(100, changedItem.getAmount());


    }*/
}
