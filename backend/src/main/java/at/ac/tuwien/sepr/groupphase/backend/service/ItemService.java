package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import jakarta.transaction.Transactional;

public interface ItemService {
    /**
     * Merges a new pantryItem into an existing one. If an pantryItem with the same description and unit exists in the pantry,
     * the amount of the new pantryItem is added to the amount of the existing one.
     * Otherwise, a new PantryItem is saved in the pantry.
     *
     * @param pantryItem the pantryItem to merge or save.
     * @param pantry the pantry
     * @return the updated or newly created item
     */
    @Transactional
    Item pantryAutoMerge(PantryItem pantryItem, Pantry pantry);
}
