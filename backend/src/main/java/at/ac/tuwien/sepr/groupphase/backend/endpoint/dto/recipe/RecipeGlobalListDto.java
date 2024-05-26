package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RecipeGlobalListDto {

    private long id;

    private String name;

    private int likes;

    private int dislikes;

    private boolean likedByUser;

    private boolean dislikedByUser;
}
