package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PantrySearchDto {
    private String details;
}
