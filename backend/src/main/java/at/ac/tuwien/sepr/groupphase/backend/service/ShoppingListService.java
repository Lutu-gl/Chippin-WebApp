package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;

import java.util.List;

public interface ShoppingListService {

    /**
     * Create a new shopping list.
     *
     * @param shoppingListCreateDto the shopping list to create
     * @return the created shopping list
     */
    ShoppingList createShoppingList(ShoppingListCreateDto shoppingListCreateDto, Long groupId);

    /**
     * Get a shopping list by id.
     *
     * @param id the id of the shopping list
     * @return the shopping list
     */
    ShoppingList getShoppingList(Long id);

    /**
     * Delete a shopping list by id.
     *
     * @param id the id of the shopping list
     */
    void deleteShoppingList(Long id);

    /**
     * Add an item to a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param itemCreateDto  the item to add
     * @return the updated shopping list
     */
    ShoppingList addItem(Long shoppingListId, ItemCreateDto itemCreateDto);

    /**
     * Mark an item as bought in a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param itemid         the id of the item
     * @return the updated shopping list
     */
    ShoppingList buyItem(Long shoppingListId, Long itemid);

    /**
     * Mark an item as not bought in a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param itemId         the id of the item
     * @return the updated shopping list
     */
    ShoppingList unbuyItem(Long shoppingListId, Long itemId);

    /**
     * Delete an item from a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param itemId         the id of the item
     * @return the updated shopping list
     */
    ShoppingList deleteItem(Long shoppingListId, Long itemId);

    /**
     * Get all shopping lists for a group.
     *
     * @param groupId the id of the group
     * @return the shopping lists
     */
    List<ShoppingList> getShoppingListsForGroup(Long groupId);

    /**
     * Update a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param shoppingList   the shopping list to update
     * @return the updated shopping list
     */
    ShoppingList updateShoppingList(Long shoppingListId, ShoppingListUpdateDto shoppingList);


}