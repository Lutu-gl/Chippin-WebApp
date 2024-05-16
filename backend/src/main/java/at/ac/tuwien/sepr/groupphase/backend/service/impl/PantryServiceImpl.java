package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PantryServiceImpl implements PantryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final PantryItemRepository pantryItemRepository;
    private final PantryRepository pantryRepository;
    private final ItemService itemService;

    @Override
    @Transactional
    public List<PantryItem> findAllItems(long pantryId) {
        LOGGER.debug("Find all pantryItem in pantry with id {}", pantryId);
        Optional<Pantry> pantry = pantryRepository.findById(pantryId);
        if (pantry.isPresent()) {
            LOGGER.debug("Found pantry: {}", pantry.get());
            return pantry.get().getItems();
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public List<PantryItem> findItemsByDescription(String description, long pantryId) {
        LOGGER.debug("Find all pantryItem in pantry with id {} matching the description \"{}\"", pantryId, description);
        Optional<Pantry> pantry = pantryRepository.findById(pantryId);
        if (pantry.isPresent()) {
            LOGGER.debug("Found pantry: {}", pantry.get());
            return pantryItemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById(description, pantry.get());
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public Item addItemToPantry(PantryItem item, long pantryId) {
        LOGGER.debug("Add pantryItem {} to pantry with ID {}", item, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            return itemService.pantryAutoMerge(item, pantry);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public void deleteItem(long pantryId, long itemId) {
        LOGGER.debug("Delete pantryItem {} in pantry with ID {}", itemId, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            PantryItem item = pantryItemRepository.getReferenceById(itemId);
            pantry.removeItem(item);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    public PantryItem updateItem(PantryItemDto item, long pantryId) {
        LOGGER.debug("Update pantryItem {} in pantry with ID {}", item, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            PantryItem updatedItem = PantryItem.builder()
                .pantry(pantry)
                .id(item.getId())
                .unit(item.getUnit())
                .amount(item.getAmount())
                .description(item.getDescription())
                .lowerLimit(item.getLowerLimit())
                .build();
            return itemRepository.save(updatedItem);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }
}
