package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UserDetailsDto {
    private Long id;
    private String email;
}
