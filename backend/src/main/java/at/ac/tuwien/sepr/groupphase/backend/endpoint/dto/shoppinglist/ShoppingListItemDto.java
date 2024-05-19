package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;

public class ShoppingListItemDto {
    private Long id;
    private ItemDto item;
    private boolean bought;
}
