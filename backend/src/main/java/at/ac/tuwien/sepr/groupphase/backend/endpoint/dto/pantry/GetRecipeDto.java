package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRecipeDto {
    //TODO: Validate no duplicates and item id exists
    @NotNull
    List<Integer> itemIds;
}
