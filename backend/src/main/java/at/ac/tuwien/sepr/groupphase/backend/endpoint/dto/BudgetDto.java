package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BudgetDto {
    private Long id;
    private String name;
    private double amount;
}
