package at.ac.tuwien.sepr.groupphase.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Item> ingredients = new ArrayList<>();

    @Column(nullable = false)
    private String name;


    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private int portionSize;

    @Builder.Default
    @Column(nullable = false)
    private int likes = 0;

    @Builder.Default
    @Column(nullable = false)
    private int dislikes = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private ApplicationUser owner;

    @ManyToMany(mappedBy = "likedRecipes")
    @Builder.Default
    @JsonIgnore
    private Set<ApplicationUser> likedByUsers = new HashSet<>();

    public void addIngredient(Item item) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        ingredients.add(item);
        item.setRecipe(this);

    }

    public void removeItem(Item item) {
        ingredients.remove(item);
        item.setRecipe(null);
    }


}
