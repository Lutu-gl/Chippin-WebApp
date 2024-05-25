package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemMergeDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PantryService {

    /**
     * Find all items in a pantry.
     *
     * @param pantryId the pantry id
     * @return ordered list of all items in the pantry
     */
    List<PantryItem> findAllItems(long pantryId);

    /**
     * Find all items in a pantry where {@code description} is a substring of the item description ordered by the item id.
     *
     * @param description the description
     * @param pantryId    the pantry id
     * @return ordered list of all items in the pantry where {@code description} is a substring of the item description
     */
    List<PantryItem> findItemsByDescription(String description, long pantryId);

    /**
     * Saves an item that belongs into a pantry.
     *
     * @param item     the item to save
     * @param pantryId the id of the corresponding pantry
     * @return the saved item
     */
    Item addItemToPantry(PantryItem item, long pantryId);

    /**
     * Deletes an item in a pantry.
     *
     * @param pantryId the pantry id
     * @param itemId   the id of the item to delete
     */
    void deleteItem(long pantryId, long itemId);

    /**
     * Updates an item in a pantry.
     *
     * @param item     the item to update
     * @param pantryId the pantry id
     * @return the updated item
     */
    PantryItem updateItem(PantryItemDto item, long pantryId);

    /**
     * Updates the item in itemMergeDto and deletes the item with the id given in itemMergeDto.
     *
     * @param itemMergeDto contains the item to update and the id of the item to delete
     * @param pantryId     the id of the pantry
     * @return the updated item
     */
    PantryItem mergeItems(PantryItemMergeDto itemMergeDto, long pantryId);

    /**
     * Update items in pantry that have been used in a recipe.
     * Decreases the amount from items, according to the recipe specifics
     *
     * @param groupId  the group to substract the items from
     * @param recipeId the recipe that has the ingredients
     * @param portion  how many people ate the recipe
     */
    List<String> removeRecipeIngredientsFromPantry(long groupId, long recipeId, int portion);
}
