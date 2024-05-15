package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
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
    private String description;
    private double amount;
    private Unit unit;
}
