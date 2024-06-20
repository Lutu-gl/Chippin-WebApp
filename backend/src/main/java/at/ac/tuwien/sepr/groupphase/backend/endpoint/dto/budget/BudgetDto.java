package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget;


import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetFrequency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BudgetDto {
    private Long id;
    private String name;
    private Category category;
    private ResetFrequency resetFrequency;
    private LocalDateTime timestamp;
    private double alreadySpent;
    private double amount;
}
