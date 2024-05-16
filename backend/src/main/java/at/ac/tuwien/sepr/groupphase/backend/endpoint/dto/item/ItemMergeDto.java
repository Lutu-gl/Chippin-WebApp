package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ItemMergeDto {
    @NotNull
    ItemDto baseItem;
    @NotNull
    Long itemToDeleteId;
}
