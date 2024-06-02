package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private Long id;
    @NotNull
    @Size(min = 2, max = 60, message = "The shopping list name must be between 2 and 60 characters long")
    private String name;
    @NotNull
    private Long groupId;
    @Min(0)
    @Max(1000)
    private int itemCount;
    @Min(0)
    @Max(1000)
    private int checkedItemCount;
}
