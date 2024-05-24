package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity;

import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ActivityDetailDto {
    private Long id;

    @NotBlank
    private String description;

    private ActivityCategory category;

    private LocalDateTime timestamp;

    @ManyToOne
    private Long expenseId;

    @ManyToOne
    private Long paymentId;

    @ManyToOne
    private Long groupId;

    @ManyToOne
    private Long userId;
}
