package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDetailDto {

    private long id;

    private List<ItemDto> ingredients;

    private String name;

    private String description;

    private Boolean isPublic;

    private int portionSize;

    private int likes = 0;

    private int dislikes = 0;

    //private ApplicationUser owner;


    public int getScore() {
        return likes - dislikes;
    }


}
