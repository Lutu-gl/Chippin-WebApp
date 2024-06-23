package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailWithUserInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeGlobalListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.AlreadyRatedException;

import java.util.List;

public interface RecipeService {


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
     * Find a recipe by its id and see if the user liked it or not.
     *
     * @param id   the id of the recipe
     * @param user the user to get the information from
     * @return the recipe with the corresponding id
     */
    RecipeDetailWithUserInfoDto getByIdWithInfo(long id, ApplicationUser user);


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
    List<RecipeGlobalListDto> getPublicRecipeOrderedByLikes(ApplicationUser user);

    /**
     * Delete a recipe from the database.
     *
     * @param id the id of the recipe to delete
     */
    void deleteRecipe(long id);

    /**
     * Updates an item in a recipe.
     *
     * @param item     the updated item
     * @param recipeId the id of the recipe, where the item is in
     * @return the updated item
     */
    Item updateItem(ItemDto item, long recipeId);

    /**
     * Like a recipe.
     * If a recipe is already disliked, remove the dislike
     *
     * @param recipeId Update the recipe to increase the like count
     * @param user     the user who sent the like
     */
    RecipeDetailDto likeRecipe(long recipeId, ApplicationUser user) throws AlreadyRatedException;

    /**
     * Dislike a recipe.
     * If a recipe is already liked, remove the like
     *
     * @param recipeId Update the recipe to increase the dislike count
     * @param user     the user who sent the dislike
     */
    RecipeDetailDto dislikeRecipe(long recipeId, ApplicationUser user) throws AlreadyRatedException;

    /**
     * Return a list of recipes from user whose name matches the searchparams.
     *
     * @param owner        the owner whose recipes should be returned
     * @param searchParams the string that should find a name
     * @return a list of all matching recipes
     */
    List<RecipeListDto> searchOwnRecipe(ApplicationUser owner, String searchParams);

    /**
     * Return a list of recipes the user liked whose name matches the searchparams.
     *
     * @param owner        the owner whose recipes should be returned
     * @param searchParams the string that should find a name
     * @return a list of all matching recipes
     */
    List<RecipeListDto> searchLikedRecipe(ApplicationUser owner, String searchParams);


    /**
     * Return a list of recipes whose name matches the searchparams.
     *
     * @param searchParams the string that should find a name
     * @return a list of all matching recipes
     */
    List<RecipeGlobalListDto> searchGlobalRecipe(ApplicationUser user, String searchParams);

    /**
     * Getting the recipes the user has liked.
     *
     * @param user the user
     * @return List of recipes from the user
     */
    List<Recipe> getLikedRecipesByUserEmail(ApplicationUser user);

}
