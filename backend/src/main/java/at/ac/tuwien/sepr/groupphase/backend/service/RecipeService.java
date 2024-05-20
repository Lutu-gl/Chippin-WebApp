package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.RecipeEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.AlreadyRatedException;

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


    /**
     * Return all recipes from the associated user.
     * Still needs doing
     *
     * @return the recipes from the specified user
     */
    List<RecipeListDto> getRecipesFromUser();

    /**
     * Update a recipe with the corresponding id.
     *
     * @param toUpdate the detailDto to update
     * @return the updated recipe
     */
    RecipeDetailDto updateRecipe(RecipeDetailDto toUpdate);

    /**
     * Get a List of all public RecipeListDto ordered in descending order by their like count.
     *
     * @return the list of all public recipes
     */
    List<RecipeListDto> getPublicRecipeOrderedByLikes();

    /**
     * Delete a recipe from the database.
     *
     * @param id the id of the recipe to delete
     */
    void deleteRecipe(long id);

    /**
     * Like a recipe.
     * If a recipe is already disliked, remove the dislike
     *
     * @param recipe Update the recipe to increase the like count
     * @param user   the user who sent the like
     */
    RecipeDetailDto likeRecipe(RecipeDetailDto recipe, ApplicationUser user) throws AlreadyRatedException;

    /**
     * Dislike a recipe.
     * If a recipe is already liked, remove the like
     *
     * @param recipe Update the recipe to increase the dislike count
     * @param user   the user who sent the dislike
     */
    RecipeDetailDto dislikeRecipe(RecipeDetailDto recipe, ApplicationUser user) throws AlreadyRatedException;
}
