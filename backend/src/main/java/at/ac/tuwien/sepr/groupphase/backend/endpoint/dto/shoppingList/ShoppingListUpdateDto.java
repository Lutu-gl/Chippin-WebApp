package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ShoppingListUpdateDto {
    private String name;

    // TODO add categories

}
