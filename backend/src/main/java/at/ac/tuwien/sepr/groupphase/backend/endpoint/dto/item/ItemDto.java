package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ItemDto {
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 60, message = "The item name must be between 2 and 60 characters long")
    private String description;
    @Min(value = 0, message = "The minimum amount is 0")
    @Max(value = 1000000, message = "The maximum amount is 1000000")
    private int amount;
    @NotNull(message = "Unit must not be empty")
    private Unit unit;

}
