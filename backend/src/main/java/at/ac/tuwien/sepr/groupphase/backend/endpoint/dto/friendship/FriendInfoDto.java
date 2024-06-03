package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class FriendInfoDto {
    private String email;
    private Double totalAmount;
    private Map<Long, List<Object>> groupAmounts;
}
