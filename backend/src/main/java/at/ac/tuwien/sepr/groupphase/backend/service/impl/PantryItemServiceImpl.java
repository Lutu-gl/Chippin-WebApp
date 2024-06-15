package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PantryItemServiceImpl implements PantryItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final PantryItemRepository pantryItemRepository;

    @Override
    @Transactional
    public PantryItem pantryAutoMerge(PantryItem pantryItem, Pantry pantry) {
        LOGGER.debug("Auto merge pantryItem {} in pantry {}", pantryItem, pantry);
        List<PantryItem> pantryItems = pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs(pantryItem.getDescription(), pantryItem.getUnit(), pantry);

        //no item to merge in pantry
        if (pantryItems.size() == 0) {
            if (pantryItem.getPantry() == null) {
                pantry.addItem(pantryItem);
                LOGGER.debug("No pantryItem to merge. New pantryItem {} saved", pantryItem);
            }
            return pantryItemRepository.save(pantryItem);
        }

        PantryItem baseItem = pantryItems.get(0);

        //update existing item -> do not merge item with itself
        if (pantryItem.getId() != null && pantryItem.getId().equals(baseItem.getId())) {
            LOGGER.debug("No pantryItem to merge. PantryItem {} updated", pantryItem);
            return pantryItemRepository.save(pantryItem);
        }

        //merge items
        baseItem.setAmount(pantryItem.getAmount() + baseItem.getAmount());

        //overwrite lowerLimit when item has lower limit set, otherwise keep lower limit of base item
        if(pantryItem.getLowerLimit() != null) {
            baseItem.setLowerLimit(pantryItem.getLowerLimit());
        }

        //update existing item -> delete item
        if (pantryItem.getId() != null && pantryItemRepository.findById(pantryItem.getId()).isPresent() && !pantryItem.getId().equals(baseItem.getId())) {
            PantryItem item = pantryItemRepository.getReferenceById(pantryItem.getId());
            pantry.removeItem(item);
            LOGGER.debug("PantryItem {} deleted", pantryItem);
        }
        LOGGER.debug("PantryItem {} merged into {}", pantryItem, baseItem);
        return pantryItemRepository.save(baseItem);
    }
}
