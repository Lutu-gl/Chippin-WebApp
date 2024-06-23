package at.ac.tuwien.sepr.groupphase.backend.repository;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByIsPublicTrueOrderByLikesDesc();

    /**
     * Finds all recipes that use ingredients from a list of ingredients.
     *
     * @param itemIds list of item ids
     * @return list of recipes
     */
    @Query("SELECT r FROM Recipe r "
        + "JOIN r.ingredients i "
        + "JOIN Item it ON i.description = it.description AND i.unit = it.unit "
        + "WHERE it.id IN :itemIds "
        + "GROUP BY r.id "
        + "HAVING r.isPublic = true OR r.owner.id = :userId "
        + "ORDER BY COUNT(i.id) DESC")
    List<Recipe> findRecipesByItemIds(@Param("itemIds") Long[] itemIds, @Param("userId") Long ownerId);

    /**
     * Finds all recipes that use ingredients from a list of ingredients.
     *
     * @param itemIds list of item ids
     * @return list of recipes
     */
    @Query("SELECT r FROM Recipe r "
        + "JOIN r.ingredients i "
        + "JOIN Item it ON i.description = it.description AND i.unit = it.unit "
        + "WHERE it.id IN :itemIds "
        + "GROUP BY r.id "
        + "HAVING r.isPublic = true OR r.owner.id = :userId "
        + "ORDER BY COUNT(i.id) DESC")
    List<Recipe> findRecipesByItemIds(@Param("itemIds") Long[] itemIds, @Param("userId") Long ownerId);

    @Modifying
    @Query("delete from Recipe r where r.id=:recipeId")
    void deleteRecipe(@Param("recipeId") long recipeId);


    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND (LOWER (r.name) LIKE %:searchParam%) ORDER BY r.likes DESC")
    List<Recipe> findPublicRecipesBySearchParamOrderedByLikes(@Param("searchParam") String searchParam);


    @Query("SELECT r FROM Recipe r WHERE r.owner = :user AND (LOWER (r.name) LIKE %:searchParam%) ORDER BY r.likes DESC")
    List<Recipe> findOwnRecipesBySearchParamOrderedByLikes(@Param("searchParam") String searchParam, ApplicationUser user);

    @Query("SELECT r FROM Recipe r JOIN r.likedByUsers u WHERE u = :user AND (LOWER (r.name) LIKE %:searchParam%) ORDER BY r.likes DESC")
    List<Recipe> findLikedRecipesBySearchParamOrderedByLikes(@Param("searchParam") String searchParam, ApplicationUser user);

    /**
     * Query to get find the recipes the user has liked.
     *
     * @param user the user to get the recipes from.
     * @return List of recipes from the user .
     */
    @Query("SELECT r FROM Recipe r JOIN r.likedByUsers u WHERE u = :user")
    List<Recipe> findAllByLikedByUsersContains(ApplicationUser user);

    /**
     * Find all ingredients by the recipe id.
     *
     * @param recipeId the id of the recipe
     * @return the list of items
     */
    @Query("SELECT i FROM Item i WHERE i.recipe.id = :recipeId")
    List<Item> findAllIngredientsByRecipeId(@Param("recipeId") Long recipeId);
}
