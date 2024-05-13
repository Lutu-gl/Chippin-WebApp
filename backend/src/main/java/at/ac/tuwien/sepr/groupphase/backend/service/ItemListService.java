package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;

import java.util.List;

public interface ItemListService {

    /**
     * Find all items in a itemList.
     *
     * @param itemListId the itemList id
     * @return ordered list of all items in the itemList
     */
    List<Item> findAllItems(long itemListId);


    /**
     * Find the name for the corresponding id.
     *
     * @param itemListId the itemList id
     * @return the name of the itemList
     */
    String getName(long itemListId);

    /**
     * Find all items in a itemList where {@code description} is a substring of the item description ordered by the item id.
     *
     * @param description the description
     * @param itemListId  the itemList id
     * @return ordered list of all items in the itemList where {@code description} is a substring of the item description
     */
    List<Item> findItemsByDescription(String description, long itemListId);

    /**
     * Saves an item that belongs into a itemList.
     *
     * @param item       the item to save
     * @param itemListId the id of the corresponding itemList
     * @return the saved item
     */
    Item addItemToItemList(Item item, long itemListId);

    /**
     * Deletes an item in a itemList.
     *
     * @param itemId     the id of the item to delete
     * @param itemListId the itemList id
     */
    void deleteItem(long itemListId, long itemId);

    /**
     * Updates an item in a itemlist.
     *
     * @param item       the item to update
     * @param itemListId the itemList id
     * @return the updated item
     */
    Item updateItem(ItemDto item, long itemListId);
}
