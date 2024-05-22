package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListDetailDto {
    private Long id;
    private UserDetailsDto owner;
    private String name;
    private Long groupId;
    private List<ShoppingListItemDto> items;
    private Set<Category> categories;

}
