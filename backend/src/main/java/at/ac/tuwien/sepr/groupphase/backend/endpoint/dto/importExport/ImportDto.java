package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importExport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ImportDto {
    private Long groupId;
    private String content;
}
