package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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

@RestController
@RequestMapping(value = "/api/v1/group")
public class RecipeEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RecipeService recipeService;
    private final ItemMapper itemMapper;

    @Autowired
    public RecipeEndpoint(RecipeService recipeService, ItemMapper itemMapper) {
        this.recipeService = recipeService;
        this.itemMapper = itemMapper;
    }


    @Secured("ROLE_USER")
    @GetMapping("/{recipeId}/recipe")
    public RecipeDetailDto getById(@PathVariable long recipeId) {
        LOGGER.info("GET /api/v1/group/{}/recipe", recipeId);
        return new RecipeDetailDto(
            itemMapper.listOfItemsToListOfItemDto(recipeService.findAllIngredients(recipeId)),
            recipeService.getName(recipeId),
            recipeService.getDescription(recipeId),
            recipeService.getIsPublic(recipeId));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{recipeId}/recipe/search")
    public ItemListListDto searchItemsInRecipe(@PathVariable long recipeId, RecipeSearchDto searchParams) {
        LOGGER.info("GET /api/v1/recipe/{}/recipe/search", recipeId);
        LOGGER.debug("request parameters: {}", searchParams);
        return new ItemListListDto(itemMapper.listOfItemsToListOfItemDto(recipeService.findItemsByDescription(searchParams.getDetails(), recipeId)));
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
    @PutMapping("/{recipeId}/recipe")
    public ItemDto updateItem(@PathVariable long recipeId, @Valid @RequestBody ItemDto itemDto) {
        LOGGER.info("PUT /api/v1/group/{}/recipe body: {}", recipeId, itemDto);
        return itemMapper.itemToItemDto(recipeService.updateItem(itemDto, recipeId));
    }

    @Secured("ROLE_USER")
    @PostMapping("/recipe/create")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDetailDto createRecipe(@RequestBody RecipeCreateDto recipeDto) {
        LOGGER.info("POST /api/v1/group/recipe/create: {}", recipeDto);
        return recipeService.createRecipe(recipeDto);
    }
    //TODO CHANGE DESCRIPTION AND NAME
}