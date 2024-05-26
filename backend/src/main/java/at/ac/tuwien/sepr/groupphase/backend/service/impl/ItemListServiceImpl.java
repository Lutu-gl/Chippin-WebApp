package at.ac.tuwien.sepr.groupphase.backend.service.impl;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemList;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemListService;
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
public class ItemListServiceImpl implements ItemListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final ItemListRepository itemListRepository;

    @Override
    @Transactional
    public List<Item> findAllItems(long itemListId) {
        LOGGER.debug("Find all items in itemList with id {}", itemListId);
        Optional<ItemList> itemList = itemListRepository.findById(itemListId);
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
        Optional<ItemList> itemList = itemListRepository.findById(itemListId);
        if (itemList.isPresent()) {
            LOGGER.debug("Found itemList: {}", itemList.get());
            return itemList.get().getName();
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", itemListId));
        }
    }

    @Override
    @Transactional
    public List<Item> findItemsByDescription(String description, long itemListId) {
        LOGGER.debug("Find all items in itemList with id {} matching the description \"{}\"", itemListId, description);
        Optional<ItemList> itemList = itemListRepository.findById(itemListId);
        if (itemList.isPresent()) {
            LOGGER.debug("Found itemList: {}", itemList.get());
            return itemRepository.findByDescriptionContainingIgnoreCaseAndItemListIsOrderById(description, itemList.get());
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", itemListId));
        }
    }

    @Override
    @Transactional
    public Item addItemToItemList(Item item, long itemListId) {
        LOGGER.debug("Add item {} to itemList with ID {}", item, itemListId);
        Optional<ItemList> optionalItemList = itemListRepository.findById(itemListId);
        if (optionalItemList.isPresent()) {
            ItemList itemList = optionalItemList.get();
            itemList.addItem(item);
            return itemRepository.save(item);
        } else {
            throw new NotFoundException(String.format("Could not find itemList with id %s", itemListId));
        }
    }

    @Override
    @Transactional
    public void deleteItem(long itemListId, long itemId) {
        LOGGER.debug("Delete item {} in itemList with ID {}", itemId, itemListId);
        Optional<ItemList> optionalItemList = itemListRepository.findById(itemListId);
        if (optionalItemList.isPresent()) {
            ItemList itemList = optionalItemList.get();
            Item item = itemRepository.getReferenceById(itemId);
            itemList.removeItem(item);
        }
    }

    @Override
    public Item updateItem(ItemDto item, long itemListId) {
        LOGGER.debug("Update item {} in itemList with ID {}", item, itemListId);
        Optional<ItemList> optionalItemList = itemListRepository.findById(itemListId);
        if (optionalItemList.isPresent()) {
            ItemList itemList = optionalItemList.get();
            Item loadItem = itemRepository.getReferenceById(item.getId());
            loadItem = Item.builder()
                .itemList(itemList)
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
