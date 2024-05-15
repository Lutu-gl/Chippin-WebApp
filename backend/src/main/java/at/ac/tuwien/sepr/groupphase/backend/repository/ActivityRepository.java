package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Find all expense activities of group.
     *
     * @param group the group
     * @return Set of expense activities
     */
    @Query(
        "SELECT a FROM Activity a"
        + " WHERE "
        + " a.group = :group AND"
        + " (a.category = 'EXPENSE' OR a.category = 'EXPENSE_UPDATE' OR a.category = 'EXPENSE_DELETE')")
    Set<Activity> findExpenseActivitiesByGroup(GroupEntity group);
}
