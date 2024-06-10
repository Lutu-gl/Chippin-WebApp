package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importExport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
public class EmailSuggestionsAndContentDto {
    private Map<String, String> emailSuggestions;
    private List<String> content;
}
