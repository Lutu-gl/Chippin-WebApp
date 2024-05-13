package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    /**
     * Find all budgets associated with a given group ID.
     *
     * @param groupId the ID of the group
     * @return a list of all budgets in the specified group
     */
    List<Budget> findByGroupId(Long groupId);

    Optional<Budget> findByIdAndGroupId(Long id, Long groupId);
}
