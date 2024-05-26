package at.ac.tuwien.sepr.groupphase.backend.repository;


import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {


    List<Recipe> findByIsPublicTrueOrderByLikesDesc();

    /**
     * Finds all recipes that use ingredients that are stored in the pantry.
     *
     * @param groupId the id of the group
     * @return list of recipes
     */
    @Query("SELECT r "
        + "FROM Recipe r "
        + "JOIN r.ingredients i "
        + "JOIN Pantry p ON p.group.id = :groupId "
        + "JOIN p.items pi "
        + "WHERE i.description=pi.description "
        + "AND i.unit=pi.unit "
        + "GROUP BY r.id "
        + "ORDER BY COUNT(i.id) DESC")
    List<Recipe> findRecipeByPantry(long groupId);
}
