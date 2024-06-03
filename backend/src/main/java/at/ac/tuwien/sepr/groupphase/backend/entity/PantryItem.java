package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Pantry pantry;

    @Column
    private Long lowerLimit;
}
