package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RecipeCreateDto {


    @NotNull(message = "Recipe must have at least one ingredient")
    @Size(min = 1, message = "Recipe must have at least one ingredient")
    @Size(max = 100, message = "Recipe cannot have more than 100 ingredients")
    @NotEmpty(message = "Recipe must have at least one ingredient")
    private List<ItemCreateDto> ingredients;

    @NotNull(message = "Recipe must have a name")
    @NotBlank(message = "Recipe must have a name")
    @Size(max = 255, message = "Name cannot be longer than 255 characters")
    @Size(min = 2, message = "Name must be at least 2 characters")
    private String name;

    @NotNull(message = "Recipe must have a description")
    @NotBlank(message = "Recipe must have a description")
    @Size(min = 2, message = "Description must be longer than 2 characters")
    private String description;

    @NotNull
    private boolean isPublic;
}
