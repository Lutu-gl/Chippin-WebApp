package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;

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
     * Delete a budget in a group.
     *
     * @param groupId  the ID of the group where the budget exists
     * @param budgetId the ID of the budget to be deleted
     */
    void deleteBudget(long groupId, long budgetId);
}
