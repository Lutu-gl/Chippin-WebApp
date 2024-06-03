package at.ac.tuwien.sepr.groupphase.backend.service.impl;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Blueprint;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.BluePrintRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.BlueprintService;
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
public class BlueprintServiceImpl implements BlueprintService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final BluePrintRepository bluePrintRepository;

    /*@Override
    @Transactional
    public List<Item> findAllItems(long itemListId) {
        LOGGER.debug("Find all items in itemList with id {}", itemListId);
        Optional<Blueprint> itemList = bluePrintRepository.findById(itemListId);
        if (itemList.isPresent()) {
            LOGGER.debug("Found itemList: {}", itemList.get());
            return itemList.get().getItems();
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", itemListId));
        }
    }

    @Override
    @Transactional
    public String getName(long itemListId) {
        LOGGER.debug("Find name for itemList({})", itemListId);
        Optional<Blueprint> itemList = bluePrintRepository.findById(itemListId);
        if (itemList.isPresent()) {
            LOGGER.debug("Found itemList: {}", itemList.get());
            return itemList.get().getName();
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", itemListId));
        }
    }
*/
    @Override
    @Transactional
    public List<Item> findItemsByDescription(String description, long blueprintId) {
        LOGGER.debug("Find all items in itemList with id {} matching the description \"{}\"", blueprintId, description);
        Optional<Blueprint> itemList = bluePrintRepository.findById(blueprintId);
        if (itemList.isPresent()) {
            LOGGER.debug("Found itemList: {}", itemList.get());
            return null;
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", blueprintId));
        }
    }

    @Override
    @Transactional
    public Item addItemToBlueprint(Item item, long blueprintId) {
        LOGGER.debug("Add item {} to itemList with ID {}", item, blueprintId);
        Optional<Blueprint> optionalItemList = bluePrintRepository.findById(blueprintId);
        if (optionalItemList.isPresent()) {
            Blueprint blueprint = optionalItemList.get();
            blueprint.addItem(item);
            return itemRepository.save(item);
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", blueprintId));
        }
    }

    @Override
    @Transactional
    public void deleteItem(long itemListId, long itemId) {
        LOGGER.debug("Delete item {} in itemList with ID {}", itemId, itemListId);
        Optional<Blueprint> optionalItemList = bluePrintRepository.findById(itemListId);
        if (optionalItemList.isPresent()) {
            Blueprint blueprint = optionalItemList.get();
            Item item = itemRepository.getReferenceById(itemId);
            blueprint.removeItem(item);
        }
    }

    @Override
    public Item updateItem(ItemDto item, long itemListId) {
        LOGGER.debug("Update item {} in itemList with ID {}", item, itemListId);
        Optional<Blueprint> optionalItemList = bluePrintRepository.findById(itemListId);
        if (optionalItemList.isPresent()) {
            Blueprint blueprint = optionalItemList.get();
            Item loadItem = itemRepository.getReferenceById(item.getId());
            loadItem = Item.builder()
                .blueprint(blueprint)
                .id(item.getId())
                .unit(item.getUnit())
                .amount(item.getAmount())
                .description(item.getDescription()).build();
            itemRepository.save(loadItem);
            return loadItem;
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", itemListId));
        }
    }
}
