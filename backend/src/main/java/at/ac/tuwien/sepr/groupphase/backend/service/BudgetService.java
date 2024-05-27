package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;

import java.util.List;

public interface BudgetService {

    /**
     * Find all budgets by group ID.
     *
     * @param groupId the ID of the group
     * @return a list of all budgets in the group
     */
    List<Budget> findAllByGroupId(long groupId);

    /**
     * Create a new budget in a group.
     *
     * @param budget  the budget to be created
     * @param groupId the ID of the group where the budget will be created
     * @return the created budget
     */
    Budget createBudget(BudgetCreateDto budget, long groupId);

    /**
     * Update an existing budget in a group.
     *
     * @param budget  the budget to update
     * @param groupId the ID of the group where the budget exists
     * @return the updated budget
     */
    Budget updateBudget(BudgetDto budget, long groupId);

    /**
     * Add the amount of an expense.
     *
     * @param groupId  the group
     * @param amount   the amount to be added
     * @param category the category of budgets
     */
    void addUsedAmount(long groupId, double amount, Category category);

    /**
     * Remove the amount of an expense which got deleted.
     *
     * @param groupId  the group
     * @param amount   the amount to be removed
     * @param category the category of budgets
     */
    void removeUsedAmount(long groupId, double amount, Category category);

    /**
     * Delete a budget in a group.
     *
     * @param groupId  the ID of the group where the budget exists
     * @param budgetId the ID of the budget to be deleted
     */
    void deleteBudget(long groupId, long budgetId);

    /**
     * Find a specific Budget.
     *
     * @param groupId  the group of the budget
     * @param budgetId the id of the budget
     * @return the corresponding budget
     */
    Budget findByGroupIdAndBudgetId(long groupId, long budgetId);

    Budget resetBudget(Budget budget);
}
