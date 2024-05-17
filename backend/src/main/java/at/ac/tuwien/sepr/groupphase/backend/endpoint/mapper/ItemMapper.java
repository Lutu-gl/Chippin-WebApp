package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemDto> listOfItemsToListOfItemDto(List<Item> items);

    List<PantryItemDto> listOfPantryItemsToListOfPantryItemDto(List<PantryItem> pantryItems);

    List<Item> listOfItemCreateDtoToListOfItemEntity(List<ItemCreateDto> items);

    Item itemCreateDtoToItem(ItemCreateDto itemCreateDto);

    PantryItem pantryItemCreateDtoToPantryItem(PantryItemCreateDto pantryItemCreateDto);

    ItemDto itemToItemDto(Item item);
}
