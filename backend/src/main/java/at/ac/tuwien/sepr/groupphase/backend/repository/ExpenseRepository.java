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

    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId and e.archived = false")
    List<Expense> findAllByGroupIdNotArchived(@Param("groupId") Long groupId);

    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId")
    List<Expense> findAllByGroupId(@Param("groupId") Long groupId);


    /**
     * Calculates and returns the consolidated financial balances of expenses and payments
     * for a specified user within a given group. This method aggregates both the expenses
     * incurred by the user as a participant and the payments made or received by the user
     * within the group. It ensures that all transactions, including those with no financial
     * activity, are represented with a default balance of zero if no transactions exist.
     * Here is an example of what the query returns:
     * Group has 3 members: luca, emil, max
     * luca owes emil 10€
     * emil owes max 5€
     * max owes luca 3€
     * the query returns the following if the user luca is selected for the parameter email
     * [emil, -10], [max, 3]
     * if the query is called with emil as the email parameter, the result is:
     * [luca, 10], [max, -5]
     *
     * @param email   The email address of the user for whom the balances are being calculated. This parameter identifies the user within the system and is used to filter the transactions related to this user.
     * @param groupId The ID of the group within which the financial balances are calculated. This parameter helps filter the transactions to those associated only with the specified group.
     * @return A list of {@code Object[]} where each array has 2 elements: the 1 element is the email of a user and the 2 element is the sum of the total amount representing the user's financial status(expenses/payments) within the group.
     */

    // In detail the query works as follows:
    // The first part of the query calculates the debt from expenses.
    //  It first calculates how much the user gets from other members and then how much the user owes to other members.
    // The second part of the query calculates the debt from payments.
    //  It first calculates how much the user receives from other members and then how much the user pays to other members.
    // Finally, the results of both parts are combined using a UNION ALL operation and then a RIGHT JOIN operation is performed to list all users in the group (except the user itself) with a balance of 0.
    @Query(value = "SELECT em, sum(total_amount) from\n" // part of the query calculates the expenses
        + "(SELECT ug.email em, COALESCE(SUM(am), 0) total_amount\n"
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
        + "GROUP BY ug.email\n"
        + "UNION ALL\n"
        // part of the query calculates the payments
        + "SELECT \n"
        + "    u.email em,\n"
        + "    COALESCE(SUM(p.amount), 0) AS total_amount\n"
        + "FROM (\n"
        + "    SELECT \n"
        + "        receiver.email,\n"
        + "        p.amount\n"
        + "    FROM PAYMENT p\n"
        + "    JOIN application_user payer ON p.payer_id = payer.id\n"
        + "    JOIN application_user receiver ON p.receiver_id = receiver.id\n"
        + "    WHERE payer.email = :email \n"
        + "    AND p.group_id = :groupId AND p.deleted = false\n"
        + "    UNION ALL\n"
        + "    SELECT \n"
        + "        payer.email,\n"
        + "        -p.amount\n"
        + "    FROM PAYMENT p\n"
        + "    JOIN application_user payer ON p.payer_id = payer.id\n"
        + "    JOIN application_user receiver ON p.receiver_id = receiver.id\n"
        + "    WHERE receiver.email = :email \n"
        + "    AND p.group_id = :groupId AND p.deleted = false\n"
        + ") AS p\n"
        + "JOIN application_user u ON p.email = u.email\n"
        + "GROUP BY u.email)\n"
        + "group by em;", nativeQuery = true)
    List<Object[]> calculateBalancesExpensesAndPaymentsForUser(@Param("email") String email, @Param("groupId") Long groupId);
}
