package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RecipeDetailDto {

    private long id;

    private List<ItemDto> ingredients;

    @NotNull(message = "Recipe must have a name")
    @NotBlank(message = "Recipe must have a name")
    @Size(max = 255, message = "Name cannot be longer than 255 characters")
    @Size(min = 2, message = "Name must be at least 2 characters")
    private String name;

    @NotNull(message = "Recipe must have a description")
    @NotBlank(message = "Recipe must have a description")
    @Size(min = 2, message = "Description must be at least 2 characters")
    private String description;

    private Boolean isPublic;

    @NotNull
    @Min(value = 1, message = "Portion size must be at least 1")
    @Max(value = 1000, message = "Recipe cannot feed more than 1000 people")
    private int portionSize;

    private int likes = 0;

    private int dislikes = 0;

    @NotNull
    private ApplicationUser owner;

    @Builder.Default
    private Set<ApplicationUser> likedByUsers = new HashSet<>();

    @Builder.Default
    private Set<ApplicationUser> dislikedByUsers = new HashSet<>();
}
