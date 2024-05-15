package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;

import java.util.List;

public interface RecipeService {

    /**
     * Find all items in a recipe.
     *
     * @param recipeId the recipe id
     * @return ordered list of all items in the recipe
     */
    List<Item> findAllIngredients(long recipeId);


    /**
     * Find the name for the corresponding id.
     *
     * @param recipeId the recipe id
     * @return the name of the recipe
     */
    String getName(long recipeId);

    /**
     * Find the description for the corresponding id.
     *
     * @param recipeId the recipe id
     * @return the description of the recipe
     */
    String getDescription(long recipeId);

    /**
     * Find the isPublic for the corresponding id.
     *
     * @param recipeId the recipe id
     * @return the isPublic of the recipe
     */
    boolean getIsPublic(long recipeId);

    /**
     * Find all items in a recipe where {@code description} is a substring of the item description ordered by the item id.
     *
     * @param description the description
     * @param recipeId    the recipe id
     * @return ordered list of all items in the recipe where {@code description} is a substring of the item description
     */
    List<Item> findItemsByDescription(String description, long recipeId);

    /**
     * Saves an item that belongs into a recipe.
     *
     * @param item     the item to save
     * @param recipeId the id of the corresponding recipe
     * @return the saved item
     */
    Item addItemToRecipe(Item item, long recipeId);

    /**
     * Deletes an item in a recipe.
     *
     * @param itemId   the id of the item to delete
     * @param recipeId the recipe id
     */
    void deleteItem(long recipeId, long itemId);

    /**
     * Updates an item in a recipe.
     *
     * @param item     the item to update
     * @param recipeId the recipe id
     * @return the updated item
     */
    Item updateItem(ItemDto item, long recipeId);

    /**
     * Create a new recipe.
     *
     * @param recipeDto the recipe to create
     * @return the created recipe with id
     */
    RecipeDetailDto createRecipe(RecipeCreateDto recipeDto);

    /**
     * Find a recipe by its id.
     *
     * @param id the id of the recipe
     * @return the recipe with the corresponding id
     */
    RecipeDetailDto getById(long id);
}
