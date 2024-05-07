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

    @Query("SELECT u.groups FROM ApplicationUser u WHERE u.id = :id")
    Set<GroupEntity> findGroupsByUserId(Long id);

    @Query("SELECT u FROM ApplicationUser u WHERE u.id = :id")
    ApplicationUser findByUserId(Long id);
}
