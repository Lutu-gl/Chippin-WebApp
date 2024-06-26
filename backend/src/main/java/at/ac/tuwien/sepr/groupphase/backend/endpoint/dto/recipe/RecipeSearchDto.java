package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecipeSearchDto {

    @Size(max = 100, message = "Search cannot be longer than 100 characters")
    @NotNull
    private String details;


}
