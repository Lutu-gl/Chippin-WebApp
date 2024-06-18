package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import com.github.javafaker.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    @Query("SELECT u FROM ApplicationUser u "
        + "LEFT JOIN FETCH u.dislikedRecipes "
        + "LEFT JOIN FETCH u.likedRecipes "
        + "WHERE u.id = :id")
    ApplicationUser findApplicationUserByIdWithLikeInfo(Long id);

    ApplicationUser findByEmail(String email);

    /**
     * Query to get find the groups the user is part of.
     *
     * @param email to identify which user.
     * @return Set of Groups the user is part of.
     */
    @Query("SELECT u.groups FROM ApplicationUser u WHERE u.email = :email")
    Set<GroupEntity> findGroupsByUserEmail(String email);

    /**
     * Query to get find the recipes the user has created.
     *
     * @param email to identify which user.
     * @return List of recipes from the user .
     */
    @Query("SELECT u.recipes FROM ApplicationUser u WHERE u.email = :email")
    List<Recipe> findRecipesByUserEmail(String email);

    List<ApplicationUser> findApplicationUserByLikedRecipesContains(Recipe recipe);

    List<ApplicationUser> findApplicationUserByDislikedRecipesContains(Recipe recipe);
}
