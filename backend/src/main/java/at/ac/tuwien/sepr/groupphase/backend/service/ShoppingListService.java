package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AddRecipeItemToShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

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
    void deleteShoppingList(Long id) throws ConflictException;

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
     * @throws ConflictException if the user is not the owner of the shopping list and tries to update the group
     */
    ShoppingList updateShoppingList(Long shoppingListId, ShoppingListUpdateDto shoppingList) throws ConflictException;


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

    /**
     * Move an item from a shopping list to the pantry.
     *
     * @param shoppingListId the id of the shopping list
     * @param itemId         the id of the item
     */
    void moveItemToPantry(Long shoppingListId, Long itemId);

    /**
     * Move all checked items from a shopping list to the pantry.
     *
     * @param shoppingListId the id of the shopping list
     */
    void moveItemsToPantry(Long shoppingListId);

    /**
     * Return a suggestion for the user what items he can add to his shopping list.
     * This function takes into account what already is in the selected shopping list and the optional pantry
     *
     * @param recipeId       the recipe with the ingredients
     * @param shoppingListId the shopping list to add
     * @param pantryId       the pantry the user wants to be considered
     * @return a list of items with
     */
    AddRecipeItemToShoppingListDto selectIngredientsForShoppingList(long recipeId, long shoppingListId, Long pantryId);

    /**
     * Delete all checked items from a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     */

    void deleteCheckedItems(Long shoppingListId);

    /**
     * Add multiple items to a shopping list.
     *
     * @param shoppingListId the id of the shopping list
     * @param items          the items to add
     * @param userId         the id of the user adding the items
     * @return the added items
     */
    List<ShoppingListItem> addItemsForUser(Long shoppingListId, List<ItemCreateDto> items, Long userId);

    /**
     * Get the amount of a specific item in all shopping lists of a group.
     *
     * @param groupId     the id of the group
     * @param description the description of the item
     * @param unit        the unit of the item
     * @return the amount of the item (matching description and unit) in all shopping lists of the group
     */
    Long getAmountOfItemInGroupShoppingLists(Long groupId, String description, Unit unit);
}