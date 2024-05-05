package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemDetailDto> listOfItemsToListOfItemDetailDto(List<Item> items);

    Item itemDetailDtoToItem(ItemDetailDto itemDetailDto);

    ItemDto itemToItemDto(Item item);
}
