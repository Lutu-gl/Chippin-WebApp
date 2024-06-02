package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListItemUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {ItemMapper.class, GroupMapper.class, UserMapper.class})
public interface ShoppingListMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    ShoppingList shoppingListCreateDtoToShoppingList(ShoppingListCreateDto shoppingListCreateDto);

    ShoppingListDetailDto shoppingListToShoppingListDetailDto(ShoppingList shoppingList);

    @Mapping(target = "addedById", source = "addedBy.id")
    @Mapping(target = "checkedById", source = "checkedBy.id")
    ShoppingListItemDto shoppingListItemToShoppingListItemDto(ShoppingListItem shoppingListItemDto);

    List<ShoppingListListDto> listOfShoppingListsToListOfShoppingListListDto(List<ShoppingList> shoppingLists);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.groupName")
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


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkedBy", ignore = true)
    @Mapping(target = "item", source = "itemCreateDto")
    @Mapping(target = "addedBy", source = "user")
    ShoppingListItem itemCreateDtoAndUserToShoppingListItem(ItemCreateDto itemCreateDto, ApplicationUser user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addedBy", ignore = true)
    @Mapping(target = "checkedBy", ignore = true)
    ShoppingListItem updateShoppingListItem(@MappingTarget ShoppingListItem shoppingListItem, ShoppingListItemUpdateDto shoppingListItemUpdateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "group", ignore = true)
    ShoppingList updateShoppingList(@MappingTarget ShoppingList shoppingListEntity, ShoppingListUpdateDto shoppingList);
}
