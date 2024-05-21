package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class DebtGroupDetailDto {
    private String userEmail;

    private Long groupId;

    private Map<String, Double> membersDebts;
}
