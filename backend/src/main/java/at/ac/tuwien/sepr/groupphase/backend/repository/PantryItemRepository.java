package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {

    /**
     * Find all pantryItems in a pantry ordered by the item id.
     *
     * @return ordered list of all items in the pantry
     */
    List<PantryItem> findByPantryOrderById(Pantry pantry);

    /**
     * Find all pantryItems in a pantry where {@code description} is a substring of the item description ordered by the item id.
     *
     * @return ordered list of all pantryItems in the pantry where {@code description} is a substring of the item description
     */
    List<PantryItem> findByDescriptionContainingIgnoreCaseAndPantryIsOrderById(String description, Pantry pantry);

    /**
     * Find all pantryItems in a pantry that match a description and a unit.
     *
     * @return list of all pantryItems in the pantry that match the given description and the given unit.
     */
    List<PantryItem> findByDescriptionIsAndUnitIsAndPantryIs(String description, Unit unit, Pantry pantry);

    /**
     * Find all pantryItems that are used in a recipe.
     *
     * @param pantryId the pantry to get the items from
     * @param recipeId the recipe whose items are being used
     * @return a list of all pantryItems that are in the recipe and the pantry
     */
    @Query("SELECT pi FROM PantryItem pi "
        + "JOIN pi.pantry p "
        + "JOIN Recipe r ON r.id = :recipeId "
        + "JOIN r.ingredients i "
        + "WHERE p.id = :pantryId "
        + "AND pi.description = i.description "
        + "AND pi.unit = i.unit")
    List<PantryItem> findMatchingRecipeItemsInPantry(@Param("pantryId") Long pantryId, @Param("recipeId") Long recipeId);
}
