package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ShoppingListMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "group", ignore = true)
    ShoppingList shoppingListCreateDtoToShoppingList(ShoppingListCreateDto shoppingListCreateDto);

    @Mapping(target = "groupId", source = "group.id")
    ShoppingListDetailDto shoppingListToShoppingListDetailDto(ShoppingList shoppingList);

    ShoppingListItemDto shoppingListItemToShoppingListItemDto(ShoppingListItem shoppingListItemDto);

    List<ShoppingListListDto> listOfShoppingListsToListOfShoppingListListDto(List<ShoppingList> shoppingLists);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "budget", source = "budget")
    ShoppingListListDto shoppingListToListDto(ShoppingList shoppingList);

}
