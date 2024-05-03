package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PantryDetailDto {
    private List<ItemDetailDto> items;
}
