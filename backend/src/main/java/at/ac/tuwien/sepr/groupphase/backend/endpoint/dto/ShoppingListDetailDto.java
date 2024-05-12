package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListDetailDto {
    private Long id;
    private String name;
    private Float budget;
    private Long groupId;
    private List<ShoppingListItemDto> items;
}
