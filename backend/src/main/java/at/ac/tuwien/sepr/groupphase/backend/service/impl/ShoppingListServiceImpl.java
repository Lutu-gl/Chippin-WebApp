package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingListServiceImpl implements ShoppingListService {


    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingListRepository shoppingListRepository;
    private final GroupRepository groupRepository;


    @Override
    @Transactional
    public ShoppingList createShoppingList(ShoppingListCreateDto shoppingListCreateDto, Long groupId) {
        log.debug("Creating shopping list for group {}", groupId);
        ShoppingList shoppingList = shoppingListMapper.shoppingListCreateDtoToShoppingList(shoppingListCreateDto);
        // Add group to shopping list
        var group = groupRepository.findById(groupId).orElseThrow(
            () -> new NotFoundException("Group with id " + groupId + " not found")
        );
        log.debug("Setting group of shopping list to: {}", group);
        shoppingList.setGroup(group);
        shoppingList.setItems(List.of());
        ShoppingList savedList = shoppingListRepository.save(shoppingList);
        log.debug("Shopping list created with id {}", savedList.getId());
        return savedList;
    }

    @Override
    public ShoppingList getShoppingList(Long id) {
        log.debug("Getting shopping list with id {}", id);
        var shoppingList = shoppingListRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Shopping list with id " + id + " not found")
        );
        log.debug("Found shopping list: {}", shoppingList);
        return shoppingList;
    }

    @Override
    public void deleteShoppingList(Long id) {
        log.debug("Deleting shopping list with id {}", id);
        shoppingListRepository.deleteById(id);
        log.debug("Shopping list deleted");
    }

    @Override
    public ShoppingList addItem(Long shoppingListId, ItemCreateDto itemCreateDto) {
        return null;
    }

    @Override
    public ShoppingList buyItem(Long shoppingListId, Long itemid) {
        return null;
    }

    @Override
    public ShoppingList unbuyItem(Long shoppingListId, Long itemId) {
        return null;
    }

    @Override
    public ShoppingList deleteItem(Long shoppingListId, Long itemId) {
        return null;
    }

    @Override
    public List<ShoppingList> getShoppingListsForGroup(Long groupId) {
        log.debug("Getting shopping lists for group {}", groupId);
        List<ShoppingList> shoppingLists = shoppingListRepository.findAllByGroupId(groupId);
        log.debug("Found {} shopping lists for group {}", shoppingLists.size(), groupId);
        return shoppingLists;
    }

    @Override
    @Transactional
    public ShoppingList updateShoppingList(Long shoppingListId, ShoppingListUpdateDto shoppingList) {
        log.debug("Updating shopping list with id {}", shoppingListId);
        var shoppingListEntity = shoppingListRepository.findById(shoppingListId).orElseThrow(
            () -> new NotFoundException("Shopping list with id " + shoppingListId + " not found")
        );
        shoppingListEntity.setName(shoppingList.getName());
        var savedShoppingList = shoppingListRepository.save(shoppingListEntity);
        log.debug("Shopping list updated: {}", savedShoppingList);
        return savedShoppingList;
    }
}
