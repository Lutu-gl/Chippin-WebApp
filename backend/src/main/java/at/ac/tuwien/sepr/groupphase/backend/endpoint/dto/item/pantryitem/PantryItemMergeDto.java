package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Builder
@Setter
public class PantryItemMergeDto {
    @NotNull
    @Valid
    PantryItemDto result;
    @NotNull(message = "Item to delete must not be empty")
    Long itemToDeleteId;
}
