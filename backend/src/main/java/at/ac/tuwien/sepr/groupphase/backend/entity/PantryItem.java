package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
public class PantryItem extends Item {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pantry_id")
    @JsonBackReference
    private Pantry pantry;

    @Column
    @Min(value = 0, message = "The minimum lowerLimit is 0")
    @Max(value = 1000000, message = "The maximum lowerLimit is 1000000")
    private Long lowerLimit;
}
