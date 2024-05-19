package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListListDto {
    private Long id;
    private String name;
    private Long groupId;
    private int itemCount;
    private int checkedItemCount;
}
