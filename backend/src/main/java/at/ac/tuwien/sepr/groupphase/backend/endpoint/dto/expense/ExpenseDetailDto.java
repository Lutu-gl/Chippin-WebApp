package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
public class ExpenseDetailDto {
    private Long id;
    private String name;
    private Category category;
    private double amount;
    private String payerEmail;
    private LocalDateTime date;
    private GroupCreateDto group;
    private Map<String, Double> participants;
    private Boolean deleted;
}
