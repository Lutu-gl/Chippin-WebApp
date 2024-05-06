package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemCreateDto> listOfItemsToListOfItemCreateDto(List<Item> items);

    Item itemCreateDtoToItem(ItemCreateDto itemCreateDto);

    ItemDto itemToItemDto(Item item);
}
