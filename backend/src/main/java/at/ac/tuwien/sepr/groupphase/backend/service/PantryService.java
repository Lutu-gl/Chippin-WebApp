package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;

import java.util.List;

public interface ItemService {

    //TODO change pantry to group id later (exactly one pantry per group, group id = pantry id)
    /**
     * Find all items in a pantry ordered by the item id.
     *
     * @param pantryId the pantry id
     * @return ordered list of all items in the pantry
     */
    List<Item> findAllInPantry(long pantryId);

    /**
     * Find all items in a pantry where {@code description} is a substring of the item description ordered by the item id.
     *
     * @param description the description
     * @param pantryId the pantry id
     * @return ordered list of all items in the pantry where {@code description} is a substring of the item description
     */
    List<Item> findByDescriptionInPantry(String description, long pantryId);
}
