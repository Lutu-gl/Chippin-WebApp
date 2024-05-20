package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListDetailDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ShoppingListEndpoint {


    //TODO refactor this to support the following endpoints:
    // POST /shopping-lists/{shoppingListId}/items
    // PATCH /shopping-lists/{shoppingListId}/items/{itemId}
    // DELETE /shopping-lists/{shoppingListId}/items/{itemId}

    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingListService shoppingListService;


    // POST /users/{userId}/shopping-lists (with an optional groupId in the request body)
    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.hasCorrectId(#userId) && (#shoppingListCreateDto.groupId == null "
        + "|| @securityService.isGroupMember(#shoppingListCreateDto.groupId))")
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
    //@PreAuthorize("@securityService.canAccessShoppingList(#shoppingListId)")
    public ShoppingListDetailDto updateShoppingList(@PathVariable Long shoppingListId, @RequestBody ShoppingListUpdateDto shoppingListUpdateDto) {
        log.debug("request body: {}", shoppingListUpdateDto);
        var shoppingList = shoppingListService.updateShoppingList(shoppingListId, shoppingListUpdateDto);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);
    }
}


