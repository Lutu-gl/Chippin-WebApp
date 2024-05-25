package at.ac.tuwien.sepr.groupphase.backend.repository;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {


    List<Recipe> findByIsPublicTrueOrderByLikesDesc();

    @Modifying
    @Query("delete from Recipe r where r.id=:recipeId")
    void deleteRecipe(@Param("recipeId") long recipeId);

    //r.description LIKE %:searchParam% if description should also match
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND (r.name LIKE %:searchParam%) ORDER BY r.likes DESC")
    List<Recipe> findPublicRecipesBySearchParamOrderedByLikes(@Param("searchParam") String searchParam);


    @Query("SELECT r FROM Recipe r WHERE r.owner = :user AND (r.name LIKE %:searchParam%) ORDER BY r.likes DESC")
    List<Recipe> findOwnRecipesBySearchParamOrderedByLikes(@Param("searchParam") String searchParam, ApplicationUser user);

    /**
     * Query to get find the recipes the user has liked.
     *
     * @param user the user to get the recipes from.
     * @return List of recipes from the user .
     */
    @Query("SELECT r FROM Recipe r JOIN r.likedByUsers u WHERE u = :user")
    List<Recipe> findAllByLikedByUsersContains(ApplicationUser user);
}
