package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class PantryItemMergeDto {
    @NotNull
    PantryItemDto result;
    @NotNull
    Long itemToDeleteId;
}
