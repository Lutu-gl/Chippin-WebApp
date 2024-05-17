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
    @Mapping(target = "isPublic", source = "isPublic")
    Recipe recipeCreateToRecipeEntity(RecipeCreateDto recipe);

    @Mapping(target = "ingredients", source = "ingredients")
    @Mapping(target = "isPublic", source = "isPublic")
    Recipe recipeDetailDtoToRecipeEntity(RecipeDetailDto recipe);

    @Mapping(target = "ingredients", source = "ingredients")
    @Mapping(target = "isPublic", source = "isPublic")
    RecipeDetailDto recipeEntityToRecipeDetailDto(Recipe recipe);


    List<RecipeListDto> recipeEntityListToListOfRecipeListDto(List<Recipe> recipes);

}

