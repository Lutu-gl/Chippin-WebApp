package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class GroupCreateDto {
    private Long id;

    @NotNull(message = "Group name must be given")
    @NotBlank(message = "Group name must not be empty")
    @Size(max = 255, message = "Group name is too long")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Invalid letters in the name (no special characters allowed)")
    private String groupName;

    @NotNull(message = "Group must have at least one member")
    @NotEmpty(message = "Group must have at least one member")
    private Set<String> members; // Email of the members
}
