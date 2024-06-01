package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddRecipeItemToShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.blueprint.BlueprintListDto;
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
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final PantryService pantryService;
    private final ShoppingListService shoppingListService;


    @Autowired
    public RecipeEndpoint(RecipeService recipeService, ItemMapper itemMapper, UserService userService,
                          RecipeMapper recipeMapper, PantryService pantryService, ShoppingListService shoppingListService) {
        this.recipeService = recipeService;
        this.itemMapper = itemMapper;
        this.recipeMapper = recipeMapper;
        this.userService = userService;
        this.pantryService = pantryService;
        this.shoppingListService = shoppingListService;
    }


    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canAccessRecipe(#recipeId)")
    @GetMapping("/{recipeId}/recipe")
    public RecipeDetailDto getById(@PathVariable long recipeId) {
        LOGGER.trace("GET /api/v1/group/{}/recipe", recipeId);
        return recipeService.getById(recipeId);
    }

    //TODO
    @Secured("ROLE_USER")
    @GetMapping("/{recipeId}/recipe/search")
    public BlueprintListDto searchItemsInRecipe(@PathVariable long recipeId, @Valid RecipeSearchDto searchParams) {
        LOGGER.trace("GET /api/v1/recipe/{}/recipe/search", recipeId);
        return new BlueprintListDto(itemMapper.listOfItemsToListOfItemDto(recipeService.findItemsByDescription(searchParams.getDetails(), recipeId)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/recipe/search/own")
    public List<RecipeListDto> searchOwnRecipe(@Valid RecipeSearchDto searchParams) {
        LOGGER.trace("GET /api/v1/recipe/search/own: {}", searchParams);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser owner = userService.findApplicationUserByEmail(authentication.getName());
        return recipeService.searchOwnRecipe(owner, searchParams.getDetails());
    }

    @Secured("ROLE_USER")
    @GetMapping("/recipe/search/global")
    public List<RecipeGlobalListDto> searchGlobalRecipe(@Valid RecipeSearchDto searchParams) {
        LOGGER.trace("GET /api/v1/group/recipe/search/global: {}", searchParams);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user = userService.findApplicationUserByEmail(authentication.getName());
        return recipeService.searchGlobalRecipe(user, searchParams.getDetails());
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canEditRecipe(#recipeId)")
    @PostMapping("/{recipeId}/recipe")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItemToRecipe(@PathVariable long recipeId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        LOGGER.trace("POST /api/v1/group/{}/recipe body: {}", recipeId, itemCreateDto);
        Item item = itemMapper.itemCreateDtoToItem(itemCreateDto);
        return itemMapper.itemToItemDto(recipeService.addItemToRecipe(item, recipeId));
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canEditRecipe(#recipeId)")
    @DeleteMapping("/{recipeId}/recipe/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable long recipeId, @PathVariable long itemId) {
        LOGGER.trace("DELETE /api/v1/group/{}/recipe/{}", recipeId, itemId);
        recipeService.deleteItem(recipeId, itemId);
    }


    @Secured("ROLE_USER")
    @PostMapping("/recipe/create")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDetailDto createRecipe(@Valid @RequestBody RecipeCreateWithoutUserDto recipeDto) {
        LOGGER.trace("POST /api/v1/group/recipe/create: {}", recipeDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser owner = userService.findApplicationUserByEmail(authentication.getName());
        return recipeService.createRecipe(recipeDto.addOwner(owner));
    }

    @Secured("ROLE_USER")
    @GetMapping("recipe/list")
    public List<RecipeListDto> getRecipesFromUser() {
        LOGGER.trace("GET /api/v1/group/recipe/list");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeMapper.recipeEntityListToListOfRecipeListDto(userService.getRecipesByUserEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @GetMapping("recipe/likedlist")
    public List<RecipeListDto> getLikedRecipesFromUser() {
        LOGGER.trace("GET /api/v1/group/recipe/likedlist");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeMapper.recipeEntityListToListOfRecipeListDto(
            recipeService.getLikedRecipesByUserEmail(userService.findApplicationUserByEmail(authentication.getName())));
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canEditRecipe(#toUpdate.getId())")
    @PutMapping("recipe/update")
    public RecipeDetailDto updateRecipe(@Valid @RequestBody RecipeDetailDto toUpdate) {
        LOGGER.trace("PUT /api/v1/group/recipe/update: {}", toUpdate);

        return recipeService.updateRecipe(toUpdate);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canAccessRecipe(#recipeId)")
    @PutMapping("recipe/{recipeId}/like")
    public RecipeDetailDto likeRecipe(@PathVariable long recipeId) throws AlreadyRatedException {
        LOGGER.trace("PUT /api/v1/group/recipe/{}/like", recipeId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeService.likeRecipe(recipeId, userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canAccessRecipe(#recipeId)")
    @PutMapping("recipe/{recipeId}/dislike")
    public RecipeDetailDto dislikeRecipe(@PathVariable long recipeId) throws AlreadyRatedException {
        LOGGER.trace("PUT /api/v1/group/recipe/{}/dislike", recipeId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return recipeService.dislikeRecipe(recipeId, userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @GetMapping("recipe/global")
    public List<RecipeGlobalListDto> getPublicRecipeOrderedByLikes() {
        LOGGER.trace("GET /api/v1/group/recipe/global");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return recipeService.getPublicRecipeOrderedByLikes(userService.findApplicationUserByEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canEditRecipe(#id)")
    @DeleteMapping("recipe/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable long id) {
        LOGGER.trace("DELETE /api/v1/group/recipe/{}/delete", id);

        recipeService.deleteRecipe(id);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @PutMapping("recipe/{recipeId}/pantry/{groupId}/{portion}")
    public List<String> removeRecipeIngredientsFromPantry(@PathVariable long groupId, @PathVariable long recipeId,
                                                          @PathVariable @Max(value = 100) @Min(value = 1) int portion) {
        LOGGER.trace("PUT /api/v1/group/recipe/{}/pantry/{} : {} Portions", recipeId, groupId, portion);


        return pantryService.removeRecipeIngredientsFromPantry(groupId, recipeId, portion);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canAccessShoppingList(#shoppingListId)")
    @GetMapping("recipe/{recipeId}/shoppinglist/{shoppingListId}")

    public AddRecipeItemToShoppingListDto selectIngredientsForShoppingList(@PathVariable long recipeId, @PathVariable long shoppingListId) {
        LOGGER.trace("GET recipe/{}/shoppinglist/{}", recipeId, shoppingListId);

        return shoppingListService.selectIngredientsForShoppingList(recipeId, shoppingListId, -1L);

    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canAccessShoppingList(#shoppingListId) && @securityService.isGroupMember(#pantryId)")
    @GetMapping("recipe/{recipeId}/shoppinglist/{shoppingListId}/pantry/{pantryId}")

    public AddRecipeItemToShoppingListDto selectIngredientsForShoppingListWithPantry(@PathVariable long recipeId, @PathVariable long shoppingListId, @PathVariable long pantryId) {
        LOGGER.trace("GET recipe/{}/shoppinglist/{}/pantry/{}", recipeId, shoppingListId, pantryId);

        return shoppingListService.selectIngredientsForShoppingList(recipeId, shoppingListId, pantryId);

    }

}