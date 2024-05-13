package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/{groupId}/shoppinglist")
@RequiredArgsConstructor
@Slf4j
public class ShoppingListEndpoint {

    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingListService shoppingListService;

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @PostMapping
    public ShoppingListDetailDto createShoppingListForGroup(@PathVariable Long groupId, @Valid @RequestBody ShoppingListCreateDto shoppingListCreateDto) {
        log.debug("request body: {}", shoppingListCreateDto);
        var shoppingList = shoppingListService.createShoppingList(shoppingListCreateDto, groupId);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @GetMapping()
    public List<ShoppingListListDto> getShoppingListsForGroup(@PathVariable Long groupId) {
        var shoppingLists = shoppingListService.getShoppingListsForGroup(groupId);
        return shoppingListMapper.listOfShoppingListsToListOfShoppingListListDto(shoppingLists);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @GetMapping("/{shoppingListId}")
    public ShoppingListDetailDto getShoppingList(@PathVariable Long groupId, @PathVariable Long shoppingListId) {
        log.debug("Getting shopping list with id {}", shoppingListId);
        var shoppingList = shoppingListService.getShoppingList(shoppingListId);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @DeleteMapping("/{shoppingListId}")
    public void deleteShoppingList(@PathVariable Long groupId, @PathVariable Long shoppingListId) {
        shoppingListService.deleteShoppingList(shoppingListId);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.isGroupMember(#groupId)")
    @PutMapping("/{shoppingListId}")
    public ShoppingListDetailDto updateShoppingList(@PathVariable Long groupId, @PathVariable Long shoppingListId,
                                                    @Valid @RequestBody ShoppingListUpdateDto shoppingListUpdateDto) {
        var shoppingList = shoppingListService.updateShoppingList(shoppingListId, shoppingListUpdateDto);
        return shoppingListMapper.shoppingListToShoppingListDetailDto(shoppingList);
    }
}


