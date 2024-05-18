package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class ShoppingListUpdateDto {
    @NotBlank
    @NotNull
    private String name;

    // TODO add categories

}
