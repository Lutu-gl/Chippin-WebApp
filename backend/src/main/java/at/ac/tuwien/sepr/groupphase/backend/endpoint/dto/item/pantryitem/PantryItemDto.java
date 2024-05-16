package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem;

import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PantryItemDto {
    private Long id;
    private String description;
    private double amount;
    private Unit unit;
    private Long lowerLimit;
}
