package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ShoppingListItemUpdateDto {
    private ItemUpdateDto item;
    private boolean checked;
}
