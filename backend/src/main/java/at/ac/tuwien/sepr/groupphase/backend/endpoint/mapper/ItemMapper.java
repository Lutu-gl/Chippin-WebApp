package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemDto> listOfItemsToListOfItemDto(List<Item> items);

    List<PantryItemDto> listOfPantryItemsToListOfPantryItemDto(List<PantryItem> pantryItems);

    List<Item> listOfItemCreateDtoToListOfItemEntity(List<ItemCreateDto> items);

    Item itemCreateDtoToItem(ItemCreateDto itemCreateDto);

    PantryItem pantryItemCreateDtoToPantryItem(PantryItemCreateDto pantryItemCreateDto);

    PantryItemDto pantryItemToPantryItemDto(PantryItem pantryItem);

    @Mapping(target = "pantry", source = "pantry")
    @Mapping(target = "lowerLimit", ignore = true)
    @Mapping(target = "id", ignore = true)
    PantryItem itemToPantryItem(Item item, Pantry pantry);

    ItemDto itemToItemDto(Item item);

    List<ItemDto> listOfPantryItemDtosToListOfItemDto(List<PantryItem> pantryItems);
}
