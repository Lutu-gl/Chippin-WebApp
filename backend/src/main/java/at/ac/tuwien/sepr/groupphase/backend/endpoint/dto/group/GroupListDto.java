package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupListDto {
    private Long id;
    private String groupName;
    private Long membersCount;
    private Map<String, Double> membersDebts;
}
