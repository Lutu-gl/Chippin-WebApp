package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class ShoppingListCreateDto {
    private String name;
    private Float budget;
}