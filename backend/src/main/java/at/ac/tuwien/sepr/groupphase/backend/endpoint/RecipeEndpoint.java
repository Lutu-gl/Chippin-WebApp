package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateWithoutUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeGlobalListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.AlreadyRatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/group")
public class RecipeEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RecipeService recipeService;
    private final ItemMapper itemMapper;
    private final RecipeMapper recipeMapper;
    private final UserService userService;


    @Autowired
    public RecipeEndpoint(RecipeService recipeService, ItemMapper itemMapper, UserService userService, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.itemMapper = itemMapper;
        this.recipeMapper = recipeMapper;
        this.userService = userService;
    }


    @Secured("ROLE_USER")
    @GetMapping("/{recipeId}/recipe")
    public RecipeDetailDto getById(@PathVariable long recipeId) {
        LOGGER.info("GET /api/v1/group/{}/recipe", recipeId);
        return recipeService.getById(recipeId);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{recipeId}/recipe/search")
    public ItemListListDto searchItemsInRecipe(@PathVariable long recipeId, RecipeSearchDto searchParams) {
        LOGGER.info("GET /api/v1/recipe/{}/recipe/search", recipeId);
        LOGGER.debug("request parameters: {}", searchParams);
        return new ItemListListDto(itemMapper.listOfItemsToListOfItemDto(recipeService.findItemsByDescription(searchParams.getDetails(), recipeId)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/recipe/search/own")
    public List<RecipeListDto> searchOwnRecipe(RecipeSearchDto searchParams) {
        LOGGER.info("GET /api/v1/recipe/recipe/search/own: {}", searchParams);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser owner = userService.findApplicationUserByEmail(authentication.getName());
        return recipeService.searchOwnRecipe(owner, searchParams.getDetails());
    }

    @Secured("ROLE_USER")
    @GetMapping("/recipe/search/global")
    public List<RecipeListDto> searchGlobalRecipe(RecipeSearchDto searchParams) {
        LOGGER.info("GET /api/v1/recipe/recipe/search/global: {}", searchParams);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user = userService.findApplicationUserByEmail(authentication.getName());
        return recipeService.searchOwnRecipe(user, searchParams.getDetails());
    }

    @Secured("ROLE_USER")
    @PostMapping("/{recipeId}/recipe")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItemToRecipe(@PathVariable long recipeId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        LOGGER.info("POST /api/v1/group/{}/recipe body: {}", recipeId, itemCreateDto);
        Item item = itemMapper.itemCreateDtoToItem(itemCreateDto);
        return itemMapper.itemToItemDto(recipeService.addItemToRecipe(item, recipeId));
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{recipeId}/recipe/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable long recipeId, @PathVariable long itemId) {
        LOGGER.info("DELETE /api/v1/group/{}/recipe/{}", recipeId, itemId);
        recipeService.deleteItem(recipeId, itemId);
    }


    @Secured("ROLE_USER")
    @PostMapping("/recipe/create")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDetailDto createRecipe(@Valid @RequestBody RecipeCreateWithoutUserDto recipeDto) {
        LOGGER.info("POST /api/v1/group/recipe/create: {}", recipeDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser owner = userService.findApplicationUserByEmail(authentication.getName());
        return recipeService.createRecipe(recipeDto.addOwner(owner));
    }

    @Secured("ROLE_USER")
    @GetMapping("recipe/list")
    public List<RecipeListDto> getRecipesFromUser() {
        LOGGER.info("GET /api/v1/group/recipe/list");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeMapper.recipeEntityListToListOfRecipeListDto(userService.getRecipesByUserEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @GetMapping("recipe/likedlist")
    public List<RecipeListDto> getLikedRecipesFromUser() {
        LOGGER.info("GET /api/v1/group/recipe/likedlist");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeMapper.recipeEntityListToListOfRecipeListDto(
            recipeService.getLikedRecipesByUserEmail(userService.findApplicationUserByEmail(authentication.getName())));
    }

    @Secured("ROLE_USER")
    @PutMapping("recipe/update")
    public RecipeDetailDto updateRecipe(@Valid @RequestBody RecipeDetailDto toUpdate) {
        LOGGER.info("PUT /api/v1/group/recipe/update: {}", toUpdate);

        return recipeService.updateRecipe(toUpdate);
    }

    @Secured("ROLE_USER")
    @PutMapping("recipe/{recipeId}/like")
    public RecipeDetailDto likeRecipe(@PathVariable long recipeId) throws AlreadyRatedException {
        LOGGER.info("PUT /api/v1/group/recipe/{}/like", recipeId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeService.likeRecipe(recipeId, userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @PutMapping("recipe/{recipeId}/dislike")
    public RecipeDetailDto dislikeRecipe(@PathVariable long recipeId) throws AlreadyRatedException {
        LOGGER.info("PUT /api/v1/group/recipe/{}/dislike", recipeId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeService.dislikeRecipe(recipeId, userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @GetMapping("recipe/global")
    public List<RecipeGlobalListDto> getPublicRecipeOrderedByLikes() {
        LOGGER.info("GET /api/v1/group/recipe/global");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return recipeService.getPublicRecipeOrderedByLikes(userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @DeleteMapping("recipe/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable(name = "id") long id) {
        LOGGER.info("DELETE /api/v1/group/recipe/{}", id);

        recipeService.deleteRecipe(id);
    }

}