package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBeforeAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddRecipeItemToShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RemoveIngredientsFromPantryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.*;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RecipeEndpointTest extends BaseTestGenAndClearBeforeAfterEach {
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

    @Autowired
    private GroupRepository groupRepository;

    @SpyBean
    private SecurityService securityService;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ShoppingListService shoppingListService;


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


    @Test
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
    }


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
        when(securityService.canAccessRecipe(0L)).thenReturn(true);

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
        when(securityService.canAccessRecipe(newRecipe.getId())).thenReturn(true);
        when(securityService.canEditRecipe(newRecipe.getId())).thenReturn(true);
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
    public void givenEmptyRecipe_whenFindById_thenEmptyList() throws Exception {

        when(securityService.canAccessRecipe(emptyRecipe.getId())).thenReturn(true);

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
    public void givenRecipeWithOneItem_whenFindById_thenListWithSizeOneAndCorrectItem() throws Exception {

        when(securityService.canAccessRecipe(recipe.getId())).thenReturn(true);
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


    @Test
    @Rollback
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

        ItemDto[] listDto = objectMapper.readValue(response.getContentAsByteArray(), ItemDto[].class);

        assertEquals(1, listDto.length);
        ItemDto itemDto = listDto[0];
        assertAll(
            () -> assertEquals(item.getDescription(), itemDto.getDescription()),
            () -> assertEquals(item.getAmount(), itemDto.getAmount()),
            () -> assertEquals(item.getUnit(), itemDto.getUnit())
        );
    }


    @Test
    @Rollback
    @WithMockUser
    public void givenNothing_whenAddInvalidItemToRecipe_then400()
        throws Exception {
        String body = objectMapper.writeValueAsString(ItemCreateDto.builder().amount(-4).unit(null).description("").build());

        when(securityService.canEditRecipe(recipe.getId())).thenReturn(true);
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
        when(securityService.canEditRecipe(recipe.getId())).thenReturn(true);
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
        when(securityService.canAccessRecipe(recipe.getId())).thenReturn(true);
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
        when(securityService.canAccessRecipe(recipe.getId())).thenReturn(true);
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
        when(securityService.canEditRecipe(-5L)).thenReturn(true);
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
    @WithMockUser(username = "tester@at", roles = "USER")
    public void givenUserEmailAndSearchString_SearchLikedRecipeWithSearchParam_ReturnsListWithOneItem() throws Exception {
        recipeService.likeRecipe(recipe.getId(), user);
        String groupJson = objectMapper.writeValueAsString("Test Recipe");
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/search/liked")
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
    public void givenUserEmailAndSearchString_SearchOwnRecipeWithSearchParam_ReturnsEmptyList() throws Exception {
        String groupJson = objectMapper.writeValueAsString("Test Recipe");
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/search/own")
                .queryParam("details", "Test Recipe")
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

    @Test
    @WithMockUser(username = "tester@at", roles = "USER")
    public void givenValidItemAndRecipe_AddItemToRecipeSuccessfully_ThenRecipeWithIngredient() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().amount(3).unit(Unit.Piece).description("Carrot").build();
        String body = objectMapper.writeValueAsString(itemCreateDto);

        when(securityService.canEditRecipe(recipe.getId())).thenReturn(true);

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
    }

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


    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    public void GivenUserRecipeAndPantry_WhenRemoveRecipeIngredientsFromPantry_ReturnsCorrectPantryItems() throws Exception {


        GroupCreateDto group = groupService.create(GroupCreateDto.builder().groupName("Fortest").members(Set.of("user2@example.com", "user1@example.com")).build(), "user1@example.com");
        pantryService.addItemToPantry(PantryItem.builder().description("Blueberries").amount(200).unit(Unit.Piece).build(), group.getId());
        pantryService.addItemToPantry(PantryItem.builder().description("Strawberries").amount(200).unit(Unit.Piece).build(), group.getId());

        Long id = userDetailService.findApplicationUserByEmail("user1@example.com").getId();
        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("Fortest").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);
        when(securityService.canAccessRecipe(recipe.getId())).thenReturn(true);


        Recipe blueberryRecipe = recipeRepository.save(
            Recipe.builder()
                .description("test")
                .name("Blueberries")
                .portionSize(1)
                .owner(userDetailService.findApplicationUserByEmail("user1@example.com"))
                .isPublic(true).build());

        recipeService.addItemToRecipe(Item.builder().description("Blueberries")
                .amount(100000).unit(Unit.Piece).build()
            , blueberryRecipe.getId());

        recipeService.addItemToRecipe(Item.builder().description("Raspberries")
                .amount(100000).unit(Unit.Piece).build()
            , blueberryRecipe.getId());


        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/group/recipe/{0}/pantry/{1}/{2}", blueberryRecipe.getId(), group.getId(), 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());


        RemoveIngredientsFromPantryDto result = objectMapper.readValue(response.getContentAsByteArray(), RemoveIngredientsFromPantryDto.class);
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2, result.getRecipeItems().size()),
            () -> assertEquals(1, result.getPantryItems().size()),
            () -> assertFalse(result.getPantryItems().stream().anyMatch(o -> o.getDescription().equals("Raspberries"))),
            () -> assertFalse(result.getPantryItems().stream().anyMatch(o -> o.getDescription().equals("Strawberries"))),
            () -> assertTrue(result.getPantryItems().stream().anyMatch(o -> o.getDescription().equals("Blueberries"))),
            () -> assertTrue(result.getRecipeItems().stream().anyMatch(o -> o.getDescription().equals("Blueberries"))),
            () -> assertTrue(result.getRecipeItems().stream().anyMatch(o -> o.getDescription().equals("Raspberries")))
        );


    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    @Rollback
    public void GivenRecipePantryAndShoppingList_WhenSelectIngredientsForShoppingList_ReturnsCorrectDto() throws Exception {
        GroupCreateDto group = groupService.create(GroupCreateDto.builder().groupName("Fortest").members(Set.of("user2@example.com", "user1@example.com")).build(), "user1@example.com");
        pantryService.addItemToPantry(PantryItem.builder().description("Blueberries").amount(200).unit(Unit.Piece).build(), group.getId());
        pantryService.addItemToPantry(PantryItem.builder().description("Not Blueberries").amount(200).unit(Unit.Piece).build(), group.getId());

        Long id = userDetailService.findApplicationUserByEmail("user1@example.com").getId();

        ShoppingList shoppingList = shoppingListRepository.findAll().stream()
            .filter(o -> o.getOwner().getId().equals(id)).toList().getFirst();
        assertNotNull(shoppingList);
        shoppingListService.addItemForUser(shoppingList.getId(), ItemCreateDto.builder().description("Blueberries").amount(200).unit(Unit.Piece).build(), id);
        shoppingListService.addItemForUser(shoppingList.getId(), ItemCreateDto.builder().description("Not Blueberries").amount(200).unit(Unit.Piece).build(), id);

        when(securityService.hasCorrectId(id)).thenReturn(true);
        Long groupId = groupRepository.findByGroupName("Fortest").getId();
        when(securityService.isGroupMember(groupId)).thenReturn(true);
        when(securityService.canAccessShoppingList(shoppingList.getId())).thenReturn(true);
        when(securityService.canAccessRecipe(shoppingList.getId())).thenReturn(true);

        Recipe blueberryRecipe = recipeRepository.save(
            Recipe.builder()
                .description("test")
                .name("Blueberries")
                .portionSize(1)
                .owner(userDetailService.findApplicationUserByEmail("user1@example.com"))
                .isPublic(true).build());

        recipeService.addItemToRecipe(Item.builder().description("Blueberries")
                .amount(100).unit(Unit.Piece).build()
            , blueberryRecipe.getId());

        recipeService.addItemToRecipe(Item.builder().description("Raspberries")
                .amount(100).unit(Unit.Piece).build()
            , blueberryRecipe.getId());

        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/group/recipe/{0}/shoppinglist/{1}/pantry/{2}",
                blueberryRecipe.getId(), shoppingList.getId(), group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        AddRecipeItemToShoppingListDto result = objectMapper.readValue(response.getContentAsByteArray(), AddRecipeItemToShoppingListDto.class);

        assertNotNull(result);

        assertAll(
            () -> assertEquals(2, result.getRecipeItems().size()),
            () -> assertEquals(1, result.getShoppingListItems().size()),
            () -> assertEquals(1, result.getPantryItems().size()),
            () -> assertTrue(result.getRecipeItems().stream().anyMatch(o -> o.getDescription().equals("Blueberries"))),
            () -> assertTrue(result.getRecipeItems().stream().anyMatch(o -> o.getDescription().equals("Raspberries"))),
            () -> assertFalse(result.getPantryItems().stream().anyMatch(o -> o.getDescription().equals("Raspberries"))),
            () -> assertFalse(result.getShoppingListItems().stream().anyMatch(o -> o.getItem().getDescription().equals("Raspberries"))),
            () -> assertTrue(result.getPantryItems().getFirst().getDescription().contains("Blueberries")),
            () -> assertTrue(result.getShoppingListItems().getFirst().getItem().getDescription().contains("Blueberries"))
        );
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void givenNothing_whenUpdate_thenItemUpdated()
        throws Exception {


        when(securityService.canEditRecipe(recipe.getId())).thenReturn(true);


        Item item = recipe.getIngredients().getFirst();
        ItemDto dto = ItemDto.builder().id(item.getId()).amount(12).unit(Unit.Gram).description("New Item").build();
        String body = objectMapper.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform(put(String.format("/api/v1/group/%d/recipe", recipe.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        Item persisted = recipeRepository.findById(recipe.getId()).get().getIngredients().getFirst();

        assertAll(
            () -> assertEquals(dto.getUnit(), persisted.getUnit()),
            () -> assertEquals(dto.getAmount(), persisted.getAmount()),
            () -> assertEquals(dto.getDescription(), persisted.getDescription()),
            () -> assertEquals(dto.getId(), persisted.getId())
        );
    }
}
