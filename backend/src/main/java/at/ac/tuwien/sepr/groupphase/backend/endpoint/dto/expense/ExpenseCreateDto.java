package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense;

import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ExpenseCreateDto {

    private Long id;

    @NotNull(message = "Group name must be given")
    @NotBlank(message = "Group name must not be empty")
    @Size(max = 255, message = "Group name is too long")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Invalid letters in the name (no special characters allowed)")
    private String name;

    private Category category = Category.Other;

    @NotNull(message = "Amount must be given")
    @Positive(message = "Amount must be positive")
    private double amount;

    @NotNull(message = "Payer email must be given")
    @Email(message = "Invalid email")
    private String payerEmail;

    @NotNull(message = "Group id must be given")
    private Long groupId;

    @NotNull(message = "Participants must be given")
    private Map<String, Double> participants;
}
