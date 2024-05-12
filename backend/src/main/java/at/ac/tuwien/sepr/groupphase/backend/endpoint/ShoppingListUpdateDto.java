package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class ShoppingListUpdateDto {
    private String name;
    private Float budget;
}
