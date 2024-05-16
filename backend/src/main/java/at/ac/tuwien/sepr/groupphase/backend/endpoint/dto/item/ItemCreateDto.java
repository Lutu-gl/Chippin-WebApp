package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ItemCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 60)
    private String description;
    @PositiveOrZero
    private int amount;
    @NotNull
    private Unit unit;
}
