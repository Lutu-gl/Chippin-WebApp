package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
