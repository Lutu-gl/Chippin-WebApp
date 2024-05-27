package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ShoppingListEndpoint {


    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingListService shoppingListService;


    // POST /users/{userId}/shopping-lists (with an optional groupId in the request body)
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && (#shoppingListCreateDto.group == null "
        + "|| @securityService.isGroupMember(#shoppingListCreateDto.group.id))")
    @PostMapping("/users/{userId}/shopping-lists")
    public ShoppingListDetailDto createShoppingList(@PathVariable Long userId, @RequestBody ShoppingListCreateDto shoppingListCreateDto) {
        log.debug("request body: {}", shoppingListCreateDto);
        var shoppingList = shoppingListService.createShoppingList(shoppingListCreateDto, userId);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);

    }

    // GET /groups/{groupId}/shopping-lists
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @GetMapping("/groups/{groupId}/shopping-lists")
    public List<ShoppingListListDto> getShoppingListsForGroup(@PathVariable Long groupId) {
        var shoppingLists = shoppingListService.getShoppingListsForGroup(groupId);
        return shoppingListMapper.listOfShoppingListsToListOfShoppingListListDto(shoppingLists);
    }

    // GET /users/{userId}/shopping-lists
    // Gets all shopping lists that the user either owns or is a member of a group that the shopping list belongs to
    @Secured("ROLE_USER")
    @GetMapping("/users/{userId}/shopping-lists")
    @PreAuthorize("@securityService.hasCorrectId(#userId)")
    public List<ShoppingListListDto> getShoppingListsForUser(@PathVariable Long userId) {
        var shoppingLists = shoppingListService.getShoppingListsForUser(userId);
        return shoppingListMapper.listOfShoppingListsToListOfShoppingListListDto(shoppingLists);
    }

    // GET /shopping-lists/{shoppingListId}
    @Secured("ROLE_USER")
    @GetMapping("/shopping-lists/{shoppingListId}")
    @PreAuthorize("@securityService.canAccessShoppingList(#shoppingListId)")
    public ShoppingListDetailDto getShoppingList(@PathVariable Long shoppingListId) {
        log.debug("Getting shopping list with id {}", shoppingListId);
        var shoppingList = shoppingListService.getShoppingList(shoppingListId);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);
    }

    // DELETE /shopping-lists/{shoppingListId}
    @Secured("ROLE_USER")
    @DeleteMapping("/shopping-lists/{shoppingListId}")
    @PreAuthorize("@securityService.canAccessShoppingList(#shoppingListId)")
    public void deleteShoppingList(@PathVariable Long shoppingListId) {
        shoppingListService.deleteShoppingList(shoppingListId);
    }

    // PATCH /shopping-lists/{shoppingListId}
    @Secured("ROLE_USER")
    @PatchMapping("/shopping-lists/{shoppingListId}")
    @PreAuthorize("@securityService.canAccessShoppingList(#shoppingListId)")
    public ShoppingListDetailDto updateShoppingList(@PathVariable Long shoppingListId, @RequestBody ShoppingListUpdateDto shoppingListUpdateDto) {
        log.debug("request body: {}", shoppingListUpdateDto);
        var shoppingList = shoppingListService.updateShoppingList(shoppingListId, shoppingListUpdateDto);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);
    }

    // --- Item endpoints ---

    // POST users/{userId}/shopping-lists/{shoppingListId}/items
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && @securityService.canAccessShoppingList(#shoppingListId)")
    @PostMapping("users/{userId}/shopping-lists/{shoppingListId}/items")
    public ShoppingListItemDto addItem(@PathVariable Long userId, @PathVariable Long shoppingListId, @RequestBody ItemCreateDto itemCreateDto) {
        log.debug("request body: {}", itemCreateDto);
        var item = shoppingListService.addItemForUser(shoppingListId, itemCreateDto, userId);
        return shoppingListMapper.shoppingListItemToShoppingListItemDto(item);
    }

    // PATCH users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && @securityService.canAccessShoppingList(#shoppingListId)")
    @PatchMapping("users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}")
    public ShoppingListItemDto updateItem(@PathVariable Long userId, @PathVariable Long shoppingListId, @PathVariable Long itemId,
                                          @RequestBody ShoppingListItemUpdateDto shoppingListItemUpdateDto) {
        log.debug("request body: {}", shoppingListItemUpdateDto);
        var item = shoppingListService.updateItemForUser(shoppingListId, itemId, shoppingListItemUpdateDto, userId);
        return shoppingListMapper.shoppingListItemToShoppingListItemDto(item);
    }

    // DELETE users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && @securityService.canAccessShoppingList(#shoppingListId)")
    @DeleteMapping("users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}")
    public Long deleteItem(@PathVariable Long userId, @PathVariable Long shoppingListId, @PathVariable Long itemId) {
        return shoppingListService.deleteItem(shoppingListId, itemId);
    }

    // PUT /users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}/groups/{groupId}/pantry
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && @securityService.canAccessShoppingList(#shoppingListId)")
    @PutMapping("/users/{userId}/shopping-lists/{shoppingListId}/items/{itemId}/groups/{groupId}/pantry")
    public void moveItemToPantry(@PathVariable Long userId, @PathVariable Long shoppingListId, @PathVariable Long itemId, @PathVariable Long groupId) {
        log.debug("Moving item with id {} to pantry for group with id {}", itemId, groupId);
        shoppingListService.moveItemToPantry(shoppingListId, itemId);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && @securityService.canAccessShoppingList(#shoppingListId)")
    @PutMapping("/users/{userId}/shopping-lists/{shoppingListId}/pantry")
    public void moveItemsToPantry(@PathVariable Long userId, @PathVariable Long shoppingListId) {
        log.debug("Moving all checked items to pantry for shopping list with id {}", shoppingListId);
        shoppingListService.moveItemsToPantry(shoppingListId);
    }

}


