package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Blueprint;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Find all items in a itemList where {@code description} is a substring of the item description ordered by the item id.
     *
     * @return ordered list of all items in the itemList where {@code description} is a substring of the item description
     */
    //List<Item> findByDescriptionContainingIgnoreCaseAndItemListIsOrderById(String description, Blueprint blueprint);

    /**
     * Find all ingredients in a recipe where {@code description} is a substring of the item description ordered by the item id.
     *
     * @return ordered list of all items in the recipe where {@code description} is a substring of the item description
     */
    List<Item> findByDescriptionContainingIgnoreCaseAndRecipeIsOrderById(String description, Recipe recipe);
}

