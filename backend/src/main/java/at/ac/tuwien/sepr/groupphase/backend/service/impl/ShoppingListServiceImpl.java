package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;


    @Override
    @Transactional
    public ShoppingList createShoppingList(ShoppingListCreateDto shoppingListCreateDto, Long ownerId) {
        log.debug("Creating shopping list {} for user {}", shoppingListCreateDto, ownerId);
        ShoppingList shoppingList = shoppingListMapper.shoppingListCreateDtoToShoppingList(shoppingListCreateDto);
        // Add owner to shopping list
        shoppingList.setOwner(userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found")));

        // Add group to shopping list
        if (shoppingListCreateDto.getGroup() != null) {
            var group = groupRepository.findById(shoppingListCreateDto.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("Group with id " + shoppingListCreateDto.getGroup().getId() + " not found"));
            log.debug("Setting group of shopping list to: {}", group);
            shoppingList.setGroup(group);
        }

        shoppingList.setItems(List.of());
        ShoppingList savedList = shoppingListRepository.save(shoppingList);
        log.debug("Shopping list created with id {}", savedList.getId());
        return savedList;
    }

    @Override
    public ShoppingList getShoppingList(Long id) {
        log.debug("Getting shopping list with id {}", id);
        var shoppingList = shoppingListRepository.findById(id).orElseThrow(() -> new NotFoundException("Shopping list with id " + id + " not found"));
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
    @Transactional
    public ShoppingListItem addItemForUser(Long shoppingListId, ItemCreateDto itemCreateDto, Long userId) {
        log.debug("Adding item {} to shopping list with id {}", itemCreateDto, shoppingListId);
        var shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow(
            () -> new NotFoundException("Shopping list with id " + shoppingListId + " not found")
        );
        // Get current user
        var user = userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("User with id " + userId + " not found")
        );
        var item = shoppingListMapper.itemCreateDtoAndUserToShoppingListItem(itemCreateDto, user);
        shoppingList.getItems().add(item);
        var savedShoppingList = shoppingListRepository.save(shoppingList);
        log.debug("Item added to shopping list: {}", savedShoppingList);
        return shoppingList.getItems().getLast();
    }

    @Override
    @Transactional
    public Long deleteItem(Long shoppingListId, Long itemId) {
        log.debug("Deleting item with id {} from shopping list with id {}", itemId, shoppingListId);
        var shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow(
            () -> new NotFoundException("Shopping list with id " + shoppingListId + " not found")
        );
        var item = shoppingList.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found in shopping list with id " + shoppingListId));
        shoppingList.getItems().remove(item);
        shoppingListRepository.save(shoppingList);
        shoppingListItemRepository.delete(item);
        log.debug("Item deleted");
        return itemId;
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
        var shoppingListEntity =
            shoppingListRepository.findById(shoppingListId).orElseThrow(() -> new NotFoundException("Shopping list with id " + shoppingListId + " not found"));
        shoppingListMapper.updateShoppingList(shoppingListEntity, shoppingList);
        // Add group to shopping list
        if (shoppingList.getGroup() != null) {
            var group = groupRepository.findById(shoppingList.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("Group with id " + shoppingList.getGroup().getId() + " not found"));
            log.debug("Setting group of shopping list to: {}", group);
            shoppingListEntity.setGroup(group);
        }
        var savedShoppingList = shoppingListRepository.save(shoppingListEntity);
        log.debug("Shopping list updated: {}", savedShoppingList);
        return savedShoppingList;
    }

    @Override
    public List<ShoppingList> getShoppingListsForUser(Long userId) {
        log.debug("Getting shopping lists for user {}", userId);
        var ownedShoppingLists = shoppingListRepository.findAllByOwnerId(userId);
        log.debug("Found {} shopping lists where user is owner", ownedShoppingLists.size());
        var groupShoppingLists = shoppingListRepository.findByGroup_Users_Id(userId);
        log.debug("Found {} shopping lists where user is in group", groupShoppingLists.size());

        // Add group shopping lists to owned shopping lists if not already present
        groupShoppingLists.stream()
            .filter(sl -> ownedShoppingLists.stream().noneMatch(osl -> osl.getId().equals(sl.getId())))
            .forEach(ownedShoppingLists::add);

        return ownedShoppingLists;
    }

    @Override
    @Transactional
    public ShoppingListItem updateItemForUser(Long shoppingListId, Long itemId, ShoppingListItemUpdateDto shoppingListItemUpdateDto, Long userId) {
        log.debug("Updating item with id {} in shopping list with id {} for user with id {}", itemId, shoppingListId, userId);
        var shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow(
            () -> new NotFoundException("Shopping list with id " + shoppingListId + " not found")
        );
        var shoppingListItem = shoppingList.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found in shopping list with id " + shoppingListId));
        // Update the item inside the shopping-list-item
        var updatedItem = shoppingListMapper.updateShoppingListItem(shoppingListItem, shoppingListItemUpdateDto);
        // Add checkedBy if item is checked
        if (shoppingListItemUpdateDto.isChecked()) {
            updatedItem.setCheckedBy(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " not found")
            ));
        } else {
            updatedItem.setCheckedBy(null);
        }
        var savedShoppingList = shoppingListRepository.save(shoppingList);
        log.debug("Item updated: {}", savedShoppingList);
        return updatedItem;
    }
}
