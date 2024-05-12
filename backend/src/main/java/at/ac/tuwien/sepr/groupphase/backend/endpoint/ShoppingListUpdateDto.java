package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotBlank
    private Float budget;
}
