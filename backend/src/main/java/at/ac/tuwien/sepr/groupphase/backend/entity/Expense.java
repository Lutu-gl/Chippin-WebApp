package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Expense {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Category category;

    @Positive
    private double amount;

    @NotNull
    private LocalDateTime date;

    @ManyToOne
    private ApplicationUser payer;

    @ManyToOne
    private GroupEntity group;


    @ElementCollection
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "amount")
    private Map<ApplicationUser, Double> participants = new HashMap<>();

    //@OneToMany
    //private Set<ExpenseParticipant> participants;
}