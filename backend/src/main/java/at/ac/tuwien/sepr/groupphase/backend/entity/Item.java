package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(min = 2, max = 60)
    @NotBlank
    private String description;

    @Column
    @PositiveOrZero
    private double amount;

    @Column
    @NotNull
    private Unit unit;

    @ManyToOne
    private Pantry pantry;
}

