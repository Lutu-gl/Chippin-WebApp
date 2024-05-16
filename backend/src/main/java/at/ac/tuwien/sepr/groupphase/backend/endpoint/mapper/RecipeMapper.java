package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = ItemMapper.class)
public interface RecipeMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingredients", source = "ingredients")
    Recipe recipeCreateToRecipeEntity(RecipeCreateDto recipe);

    Recipe recipeDetailDtoToRecipeEntity(RecipeDetailDto recipe);

    RecipeDetailDto recipeEntityToRecipeDetailDto(Recipe recipe);


    List<RecipeListDto> recipeEntityListToListOfRecipeListDto(List<Recipe> recipes);

}

