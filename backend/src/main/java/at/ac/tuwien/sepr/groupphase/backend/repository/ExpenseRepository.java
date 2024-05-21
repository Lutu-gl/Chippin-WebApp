package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * Calculates the balances for a user in a group with its members.
     *
     * @param email   email of the user who wants to see the balances.
     * @param groupId group id of the group for which the balances should be calculated.
     * @return a list of objects containing the email of the user and the total amount of money the user owes or is owed by other users in the group.
     */
    // Idea is: First calc how user gets from other members, then how much user owes to other members
    // Then union both results and the right join is for listing all users in the group (except the user itself) also with amount 0
    @Query(value = "SELECT ug.email em, COALESCE(SUM(am), 0) total_amount\n"
        + "FROM (\n"
        + "    SELECT u.email em, SUM(p.amount * e.amount) am\n"
        + "    FROM expense e \n"
        + "    JOIN expense_participants p ON e.id = p.expense_id \n"
        + "    JOIN application_user u ON p.user_id = u.id \n"
        + "    WHERE e.group_id = :groupId AND e.deleted = false\n"
        + "      AND e.payer_id = (SELECT id FROM application_user WHERE email = :email)\n"
        + "      AND e.payer_id != p.user_id\n"
        + "    GROUP BY u.email\n"
        + "    UNION ALL\n"
        + "    SELECT (SELECT email FROM application_user WHERE id = e.payer_id) em, SUM(-p.amount * e.amount) am\n"
        + "    FROM expense e \n"
        + "    JOIN expense_participants p ON e.id = p.expense_id \n"
        + "    WHERE e.group_id = :groupId AND e.deleted = false\n"
        + "      AND p.user_id = (SELECT id FROM application_user WHERE email = :email)\n"
        + "      AND e.payer_id != p.user_id\n"
        + "    GROUP BY e.payer_id\n"
        + ") result\n"
        + "RIGHT JOIN (\n"
        + "    SELECT u.email email\n"
        + "    FROM application_user u\n"
        + "    JOIN user_group ug ON u.id = ug.user_id and u.id != (SELECT id FROM application_user WHERE email = :email)\n"
        + "    WHERE ug.group_id = :groupId\n"
        + ") ug ON result.em = ug.email\n"
        + "GROUP BY ug.email", nativeQuery = true)
    List<Object[]> calculateBalancesForUser(@Param("email") String email, @Param("groupId") Long groupId);
}