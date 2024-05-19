package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListItem {

    @Id
    @Column
    private Long id;

    @JoinColumn
    @OneToOne
    private Item item;

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ApplicationUser addedBy;

    @JoinColumn
    @ManyToOne(fetch = FetchType.EAGER)
    private ApplicationUser checkedBy;

}
