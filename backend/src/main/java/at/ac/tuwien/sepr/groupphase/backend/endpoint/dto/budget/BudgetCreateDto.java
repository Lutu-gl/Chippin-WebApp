package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget;

import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BudgetCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 60)
    private String name;
    @PositiveOrZero
    private double amount;

    private Category category = Category.Other;
}
