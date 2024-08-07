package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListDetailDto {
    @NotNull
    private Long id;
    @NotNull
    @Valid
    private UserDetailsDto owner;
    @NotNull
    @Size(min = 2, max = 60, message = "The shopping list name must be between 2 and 60 characters long")
    private String name;
    @NotNull
    @Valid
    private GroupDetailDto group;
    @NotNull
    @Valid
    private List<ShoppingListItemDto> items;
    private Set<Category> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
