package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBeforeAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailWithUserInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.AlreadyRatedException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.RecipeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple test to see if getting the exchange rate getting works.
 * Do not use this in the default Test setting as the API Usage is limited per month
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecipeServiceTest extends BaseTestGenAndClearBeforeAfterEach {


    @Autowired
    private RecipeServiceImpl recipeServiceImpl;

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Test
    public void givenUser_whenLikingAlreadyDislikedRecipe_RemoveDislike() throws AlreadyRatedException {
        long recipeId = recipeRepository.findAll().getFirst().getId();

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail("user1@example.com");

        recipeServiceImpl.dislikeRecipe(recipeId, user);

        RecipeDetailDto recipe = recipeServiceImpl.likeRecipe(recipeId, user);

        assertAll(
            () -> assertTrue(recipe.getLikedByUsers().stream().anyMatch(o -> o.getId().equals(user.getId()))),
            () -> assertFalse(recipe.getDislikedByUsers().stream().anyMatch(o -> o.getId().equals(user.getId()))),
            () -> assertEquals(1, recipe.getLikes()),
            () -> assertEquals(0, recipe.getDislikes())
        );

    }

    @Test
    public void givenUser_whenDislikingAlreadyLikedRecipe_RemoveLike() throws AlreadyRatedException {
        long recipeId = recipeRepository.findAll().getFirst().getId();

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail("user1@example.com");

        recipeServiceImpl.likeRecipe(recipeId, user);

        RecipeDetailDto recipe = recipeServiceImpl.dislikeRecipe(recipeId, user);

        assertAll(
            () -> assertTrue(recipe.getDislikedByUsers().stream().anyMatch(o -> o.getId().equals(user.getId()))),
            () -> assertFalse(recipe.getLikedByUsers().stream().anyMatch(o -> o.getId().equals(user.getId()))),
            () -> assertEquals(1, recipe.getDislikes()),
            () -> assertEquals(0, recipe.getLikes())
        );

    }

    @Test
    public void givenUserAndRecipe_whenLikingAlreadyLikedRecipe_throwsAlreadyRatedException() throws AlreadyRatedException {
        long recipeId = recipeRepository.findAll().getFirst().getId();

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail("user1@example.com");

        recipeServiceImpl.likeRecipe(recipeId, user);

        assertThrows(Exception.class, () -> recipeServiceImpl.likeRecipe(recipeId, user));

    }

    @Test
    public void givenUserAndRecipe_whenDislikingAlreadyDislikedRecipe_throwsAlreadyRatedException() throws AlreadyRatedException {
        long recipeId = recipeRepository.findAll().getFirst().getId();

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail("user1@example.com");

        recipeServiceImpl.dislikeRecipe(recipeId, user);

        assertThrows(Exception.class, () -> recipeServiceImpl.dislikeRecipe(recipeId, user));

    }

    @Test
    public void givenUserAndRecipeLiked_whengetByIdWithInfo_ReturnsRatingInfo() throws AlreadyRatedException {
        long recipeId = recipeRepository.findAll().getFirst().getId();

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail("user1@example.com");

        recipeServiceImpl.dislikeRecipe(recipeId, user);

        RecipeDetailWithUserInfoDto recipe = recipeServiceImpl.getByIdWithInfo(recipeId, user);

        assertAll(
            () -> assertTrue(recipe.isDislikedByUser()),
            () -> assertFalse(recipe.isLikedByUser()),
            () -> assertEquals(recipeId, recipe.getId())
        );
    }

    @Test
    @Rollback
    public void givenRecipeId_WhenDeleteRecipe_RecipeDoesntExistAnymore() throws AlreadyRatedException {
        Recipe recipe = recipeRepository.findAll().getFirst();

        recipeServiceImpl.likeRecipe(recipe.getId(), recipe.getOwner());

        recipeServiceImpl.deleteRecipe(recipe.getId());

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail(recipe.getOwner().getEmail());

        assertFalse(user.getRecipes().stream().anyMatch(o -> o.getId().equals(recipe.getId())));

        assertFalse(user.getLikedRecipes().stream().anyMatch(o -> o.getId().equals(recipe.getId())));

        assertThrows(NotFoundException.class, () -> recipeServiceImpl.getById(recipe.getId()));
    }
}
