package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@ToString
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Email
    private String email;

    @Column
    @NotBlank
    @ToString.Exclude
    private String password;

    @Column
    private Boolean admin;

    @ManyToMany(mappedBy = "users")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<GroupEntity> groups = new HashSet<>();

    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    //, cascade = CascadeType.ALL
    @Builder.Default
    private List<Recipe> recipes = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnore
    @JoinTable(
        name = "user_recipe_likes",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    )
    private Set<Recipe> likedRecipes = new HashSet<>();

    @ManyToMany()
    @Builder.Default
    @JsonIgnore
    @JoinTable(
        name = "user_recipe_dislikes",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    )
    private Set<Recipe> dislikedRecipes = new HashSet<>();

    public ApplicationUser addRecipe(Recipe recipe) {
        recipes.add(recipe);
        recipe.setOwner(this);

        return this;
    }

    public void removeRecipe(Recipe recipe) {
        recipes.remove(recipe);
        recipe.setOwner(null);
    }

    public ApplicationUser addRecipeLike(Recipe recipe) {
        if (!likedRecipes.contains(recipe)) {
            likedRecipes.add(recipe);
            recipe.addLiker(this);
        }
        return this;
    }

    public void removeLike(Recipe recipe) {

        Recipe toDelete = likedRecipes.stream().filter(o -> o.getId().equals(recipe.getId())).findFirst().get();
        likedRecipes.remove(toDelete);

    }

    public ApplicationUser addRecipeDislike(Recipe recipe) {
        if (!dislikedRecipes.contains(recipe)) {
            dislikedRecipes.add(recipe);
            recipe.addDisliker(this);
        }
        return this;
    }

    public void removeDisLike(Recipe recipe) {
        Recipe toDelete = dislikedRecipes.stream().filter(o -> o.getId().equals(recipe.getId())).findFirst().get();
        dislikedRecipes.remove(toDelete);
    }


}