package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Valid
    private ItemUpdateDto item;
    private boolean checked;
}
