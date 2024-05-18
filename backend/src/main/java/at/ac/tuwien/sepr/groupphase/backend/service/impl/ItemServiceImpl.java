package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final PantryItemRepository pantryItemRepository;

    @Override
    @Transactional
    public Item pantryAutoMerge(PantryItem pantryItem, Pantry pantry) {
        LOGGER.debug("Auto merge pantryItem {} in pantry {}", pantryItem, pantry);
        List<PantryItem> pantryItems = pantryItemRepository.findByDescriptionIsAndUnitIsAndPantryIs(pantryItem.getDescription(), pantryItem.getUnit(), pantry);
        if (pantryItems.size() == 0) {
            pantry.addItem(pantryItem);
            LOGGER.debug("No item to merge. New pantryItem {} saved", pantryItem);
            return itemRepository.save(pantryItem);
        }
        //TODO: error if pantryItems.size > 1
        PantryItem baseItem = pantryItems.get(0);
        baseItem.setAmount(pantryItem.getAmount() + baseItem.getAmount());
        LOGGER.debug("PantryItem {} merged into {}", pantryItem, baseItem);
        return itemRepository.save(baseItem);
    }



}
