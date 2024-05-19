package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppingList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class ShoppingListCreateDto {
    @NotBlank
    @NotNull
    private String name;

    private Long groupId;

    // TODO add categories
}