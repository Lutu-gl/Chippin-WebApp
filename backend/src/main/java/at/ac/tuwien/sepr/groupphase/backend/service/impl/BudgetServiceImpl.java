package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BudgetMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BudgetRepository budgetRepository;
    private final GroupRepository groupRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public List<Budget> findAllByGroupId(long groupId) {
        List<Budget> budgets = budgetRepository.findByGroupId(groupId);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        budgets.forEach(budget -> {
            if (budget.getTimestamp().isBefore(oneMonthAgo)) {
                LOGGER.info("Resetting budget ID {} as it is older than one month", budget.getId());
                resetBudget(budget);
            }
        });

        return budgets;

    }

    @Override
    @Transactional
    public Budget resetBudget(Budget budget) {
        LOGGER.debug("Resetting budget ID {}", budget.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newTimestamp = budget.getTimestamp();

        while (newTimestamp.isBefore(now)) {
            newTimestamp = newTimestamp.plusMonths(1);
        }

        if (newTimestamp.isAfter(now)) {
            newTimestamp = newTimestamp.minusMonths(1);
        }

        budget.setAlreadySpent(0.00);
        budget.setTimestamp(newTimestamp);

        return budgetRepository.save(budget);
    }


    @Override
    @Transactional
    public Budget createBudget(BudgetCreateDto budget, long groupId) {
        LOGGER.debug("Creating a new budget for group ID {}", groupId);
        GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found with ID: " + groupId));

        Budget budgetEnt = budgetMapper.budgetCreateDtoToBudget(budget);
        if (budget.getCategory() == null) {
            budgetEnt.setCategory(Category.Other);
        } else {
            budgetEnt.setCategory(budget.getCategory());
        }

        budgetEnt.setTimestamp(LocalDateTime.now());
        budgetEnt.setGroup(group);
        budgetEnt.setAlreadySpent(0.00);

        return budgetRepository.save(budgetEnt);
    }

    @Override
    @Transactional
    public Budget updateBudget(BudgetDto budgetDto, long groupId) {
        LOGGER.debug("Updating budget ID {} for group ID {}", budgetDto.getId(), groupId);
        Budget budget = budgetRepository.findByIdAndGroupId(budgetDto.getId(), groupId)
            .orElseThrow(() -> new NotFoundException("Budget not found with ID: " + budgetDto.getId() + " for group ID: " + groupId));

        budget.setName(budgetDto.getName());
        budget.setAmount(budgetDto.getAmount());
        return budgetRepository.save(budget);
    }

    @Override
    public void addUsedAmount(long groupId, double amount, Category category) {
        LOGGER.debug("Adding used amount of {} to all budgets for group ID {} and category {}", amount, groupId, category);

        List<Budget> budgets = this.findAllByGroupId(groupId);
        if (budgets.isEmpty()) {
            return;
        }

        budgets.forEach(budget -> {
            if (budget.getCategory() == category) {
                double newUsedAmount = budget.getAlreadySpent() + amount;
                budget.setAlreadySpent(newUsedAmount);
                budgetRepository.save(budget);
            }
        });
    }

    @Override
    @Transactional
    public void removeUsedAmount(long groupId, double amount, Category category) {
        LOGGER.debug("Removing used amount of {} from all budgets for group ID {} and category {}", amount, groupId, category);

        List<Budget> budgets = this.findAllByGroupId(groupId);
        if (budgets.isEmpty()) {
            return;
        }

        budgets.forEach(budget -> {
            if (budget.getCategory() == category) {
                double newUsedAmount = budget.getAlreadySpent() - amount;
                budget.setAlreadySpent(newUsedAmount);
                budgetRepository.save(budget);
            }
        });

    }


    @Override
    @Transactional
    public void deleteBudget(long groupId, long budgetId) {
        LOGGER.debug("Deleting budget ID {} for group ID {}", budgetId, groupId);
        Budget budget = budgetRepository.findByIdAndGroupId(budgetId, groupId)
            .orElseThrow(() -> new NotFoundException("Budget not found with ID: " + budgetId + " for group ID: " + groupId));

        budgetRepository.delete(budget);
    }
}
