package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class PaymentDto {
    private Long id;

    @NotNull(message = "Amount must be given")
    @Positive(message = "Amount must be positive")
    private double amount;

    @NotNull(message = "Payer email must be given")
    @Email(message = "Invalid email")
    private String payerEmail;

    @NotNull(message = "Payer email must be given")
    @Email(message = "Invalid email")
    private String receiverEmail;

    @NotNull(message = "Group id must be given")
    private Long groupId;

    private LocalDateTime date;
    
    private Boolean deleted;

    private Boolean archived;
}
