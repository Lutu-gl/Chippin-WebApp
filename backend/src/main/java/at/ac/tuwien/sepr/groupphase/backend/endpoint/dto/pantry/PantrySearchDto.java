package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PantrySearchDto {

    @Pattern(regexp = "^[a-zA-Z0-9 ]*$")
    @Size(max = 50, message = "Search cannot be longer than 100 characters")
    @NotNull
    private String details;
}
