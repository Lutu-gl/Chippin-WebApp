package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;

import java.util.List;

public interface PantryService {

    /**
     * Find all items in a pantry.
     *
     * @param pantryId the pantry id
     * @return ordered list of all items in the pantry
     */
    List<Item> findAllItems(long pantryId);

    /**
     * Find all items in a pantry where {@code description} is a substring of the item description ordered by the item id.
     *
     * @param description the description
     * @param pantryId    the pantry id
     * @return ordered list of all items in the pantry where {@code description} is a substring of the item description
     */
    List<Item> findItemsByDescription(String description, long pantryId);

    /**
     * Saves an item that belongs into a pantry.
     *
     * @param item the item to save
     * @param pantryId the id of the corresponding pantry
     * @return the saved item
     */
    Item addItemToPantry(Item item, long pantryId);

    /**
     * Deletes an item in a pantry.
     *
     * @param itemId the id of the item to delete
     * @param pantryId the pantry id
     */
    void deleteItem(long itemId, long pantryId);
}
