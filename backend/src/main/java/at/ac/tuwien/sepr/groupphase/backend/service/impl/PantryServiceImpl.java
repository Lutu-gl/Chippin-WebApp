package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PantryServiceImpl implements PantryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final PantryRepository pantryRepository;

    @Override
    public List<Item> findAllItems(long pantryId) {
        LOGGER.debug("Find all items in pantry with id {}", pantryId);
        Optional<Pantry> pantry = pantryRepository.findById(pantryId);
        if (pantry.isPresent()) {
            return itemRepository.findByPantryOrderById(pantry.get());
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    public List<Item> findItemsByDescription(String description, long pantryId) {
        LOGGER.debug("Find all items in pantry with id {} matching the description \"{}\"", pantryId, description);
        Optional<Pantry> pantry = pantryRepository.findById(pantryId);
        if (pantry.isPresent()) {
            return itemRepository.findByDescriptionLikeAndPantryIsOrderById(description, pantry.get());
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }
}
