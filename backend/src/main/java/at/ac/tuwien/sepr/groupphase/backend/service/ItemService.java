package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;

public interface ItemService {
    /**
     * Merges a new item into an existing one. If an item with the same description and unit exists in the pantry,
     * the amount of the new item is added to the amount of the existing one.
     * Otherwise, a new item is saved in the pantry.
     *
     * @param item the item to merge or save.
     * @param pantry the pantry
     * @return the updated or newly created item
     */
    Item pantryAutoMerge(Item item, Pantry pantry);
}
