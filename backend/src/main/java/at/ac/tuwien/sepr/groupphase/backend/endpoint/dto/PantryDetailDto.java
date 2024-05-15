package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryDetailDto {
    private List<ItemDto> items;
}
