package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Find all items in a pantry ordered by the item id.
     *
     * @return ordered list of all items in the pantry
     */
    List<Item> findByPantryOrderById(Pantry pantry);

    /**
     * Find all items in a pantry where {@code description} is a substring of the item description ordered by the item id.
     *
     * @return ordered list of all items in the pantry where {@code description} is a substring of the item description
     */
    List<Item> findByDescriptionContainingAndPantryIsOrderById(String description, Pantry pantry);
}

