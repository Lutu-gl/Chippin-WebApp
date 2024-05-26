package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget;


import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BudgetDto {
    private Long id;
    private String name;
    private Category category;
    private double alreadySpent;
    private double amount;
}
