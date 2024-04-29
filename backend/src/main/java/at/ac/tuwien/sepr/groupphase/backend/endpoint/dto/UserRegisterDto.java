package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class UserRegisterDto {

    @NotNull(message = "must not be null")
    @NotBlank(message = "must not be blank")
    @Email
    private String email;

    // Validate 8-64 characters, at least one uppercase letter, one lowercase letter and one number
    @NotNull(message = "must not be null")
    @NotBlank(message = "must not be blank")
    @Length(min = 8, max = 64, message = "must be between 8 and 64 characters")
    // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "must contain at least one uppercase letter, one lowercase letter and one number")
    @Pattern(regexp = "^(?=.*[a-z]).*$", message = "must contain at least one lowercase letter")
    @Pattern(regexp = "^(?=.*[A-Z]).*$", message = "must contain at least one uppercase letter")
    @Pattern(regexp = "^(?=.*\\d).*$", message = "must contain at least one number")
    private String password;

}
