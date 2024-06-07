package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PantryItemDto {
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
    @Min(value = 0, message = "The minimum lowerLimit is 0")
    @Max(value = 1000000, message = "The maximum lowerLimit is 1000000")
    private Long lowerLimit;

    public PantryItemDto(Long id, String description, int amount, Unit unit, Long lowerLimit) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.lowerLimit = lowerLimit;
    }
}
