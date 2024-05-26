package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;



import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemListDetailDto {

    private List<ItemDto> items;

    String name;
}
