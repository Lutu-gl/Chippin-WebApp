package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ActivitySearchDto {

    private String search;

    private LocalDateTime from;

    private LocalDateTime to;
}
