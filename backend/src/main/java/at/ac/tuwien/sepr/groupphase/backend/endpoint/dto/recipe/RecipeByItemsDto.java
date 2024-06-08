package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RecipeByItemsDto {
    private long id;

    private String name;

    private List<ItemDto> ingredients;

    private List<PantryItemDto> itemsInPantry;
}
