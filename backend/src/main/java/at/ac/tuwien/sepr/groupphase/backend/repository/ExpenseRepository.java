package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Expense e SET e.deleted = true WHERE e = :expense")
    void markExpenseAsDeleted(Expense expense);

    @Transactional
    @Modifying
    @Query("UPDATE Expense e SET e.deleted = false WHERE e = :expense")
    void markExpenseAsRecovered(Expense expense);
}
