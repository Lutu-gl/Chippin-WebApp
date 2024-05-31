package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pantry")
public class Pantry {
    @Id
    @Column(name = "group_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @OneToMany(mappedBy = "pantry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<PantryItem> items = new ArrayList<>();

    public void addItem(PantryItem item) {
        items.add(item);
        item.setPantry(this);
    }

    public void removeItem(PantryItem item) {
        items.remove(item);
        item.setPantry(null);
    }
}
