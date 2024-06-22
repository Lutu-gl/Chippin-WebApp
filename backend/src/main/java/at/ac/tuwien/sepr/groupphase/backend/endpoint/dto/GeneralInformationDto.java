package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class GeneralInformationDto {
    private Long amountUsers;
    private Long amountExpenses;
    private Double expensesSum;
    private Long amountShoppingLists;
}
