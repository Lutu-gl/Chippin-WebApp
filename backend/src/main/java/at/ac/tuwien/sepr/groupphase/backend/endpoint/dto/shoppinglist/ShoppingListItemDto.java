package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ShoppingListItemDto {
    @NotNull
    private Long id;
    @NotNull
    @Valid
    private ItemDto item;
    @NotNull
    private Long addedById;
    @NotNull
    private Long checkedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
