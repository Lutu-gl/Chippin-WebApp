package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findByEmail(String email);

    /**
     * Query to get find the groups the user is part of.
     *
     * @param email to identify which user.
     * @return Set of Groups the user is part of.
     */
    @Query("SELECT u.groups FROM ApplicationUser u WHERE u.email = :email")
    Set<GroupEntity> findGroupsByUserEmail(String email);
}
