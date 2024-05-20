package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "itemCount", source = "items", qualifiedByName = "calculateItemCount")
    @Mapping(target = "checkedItemCount", source = "items", qualifiedByName = "calculateCheckedItemCount")
    ShoppingListListDto shoppingListToListDto(ShoppingList shoppingList);

    @Named("calculateItemCount")
    public static int calculateItemCount(List<ShoppingListItem> items) {
        return items.size();
    }

    @Named("calculateCheckedItemCount")
    public static int calculateCheckedItemCount(List<ShoppingListItem> items) {
        return (int) items.stream().filter(item -> item.getCheckedBy() != null).count();
    }

}
