package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;

import java.util.List;

public interface ShoppingListService {

    /**
     * Create a new shopping list.
     *
     * @param shoppingListCreateDto the shopping list to create
     * @param ownerId               the id of the owner of the shopping list
     * @return the created shopping list
     */
    ShoppingList createShoppingList(ShoppingListCreateDto shoppingListCreateDto, Long ownerId);

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
     * @param userId         the id of the user adding the item
     * @return the added item
     */
    ShoppingListItem addItemForUser(Long shoppingListId, ItemCreateDto itemCreateDto, Long userId);

    /**
     * Delete an item from a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param itemId         the id of the item
     * @return the id of the deleted item
     */
    Long deleteItem(Long shoppingListId, Long itemId);

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


    /**
     * Get all shopping lists for a user.
     * The user must either own the shopping list or be a member of a group that the shopping list belongs to.
     *
     * @param userId the id of the user
     * @return the shopping lists
     */
    List<ShoppingList> getShoppingListsForUser(Long userId);

    /**
     * Update an item in a shopping list.
     *
     * @param shoppingListId            the id of the shopping list the item is in
     * @param itemId                    the id of the item
     * @param shoppingListItemUpdateDto the updated item
     * @param userId                    the id of the user updating the item
     * @return the updated item
     */
    ShoppingListItem updateItemForUser(Long shoppingListId, Long itemId, ShoppingListItemUpdateDto shoppingListItemUpdateDto, Long userId);
}