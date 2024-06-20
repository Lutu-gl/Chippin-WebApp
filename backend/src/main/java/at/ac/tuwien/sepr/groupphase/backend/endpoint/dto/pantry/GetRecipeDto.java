package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetRecipeDto {
    @NotNull
    @JsonProperty("itemIds")
    Long[] itemIds;
}
