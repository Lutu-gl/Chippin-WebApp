package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class FriendInfoDto {
    private String email;
    private Double totalAmount;
    private Map<GroupDetailDto, Double> groupAmounts;
}
