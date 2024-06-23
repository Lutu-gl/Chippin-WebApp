package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BudgetMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetFrequency;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.BudgetService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.BudgetValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BudgetRepository budgetRepository;
    private final GroupRepository groupRepository;
    private final BudgetMapper budgetMapper;
    private final BudgetValidator budgetValidator;

    @Override
    @Transactional
    public List<Budget> findAllByGroupId(long groupId) {
        LOGGER.trace("Fetching all budgets for group ID {}", groupId);
        List<Budget> budgets = budgetRepository.findByGroupId(groupId);

        return budgets;

    }

    @Override
    @Scheduled(cron = "0 0 0 1 * ?") //first day of every month at midnight
    //@Scheduled(cron = "0 24 15 * * ?")
    @Transactional
    public void resetMonthlyBudgets() {
        LOGGER.trace("Resetting all monthly budgets");
        List<Budget> budgets = budgetRepository.findByResetFrequency(ResetFrequency.MONTHLY);

        for (Budget budget : budgets) {
            if (budget.getTimestamp() == null) {
                continue;
            }
            LocalDateTime newTimestamp = budget.getTimestamp().plusMonths(1);
            budget.setAlreadySpent(0.00);
            budget.setTimestamp(newTimestamp);
            budgetRepository.save(budget);
        }
    }

    @Scheduled(cron = "0 0 0 * * MON") // Every Monday at midnight
    @Transactional
    public void resetWeeklyBudgets() {
        LOGGER.trace("Resetting all weekly budgets");

        List<Budget> budgets = budgetRepository.findByResetFrequency(ResetFrequency.WEEKLY);

        for (Budget budget : budgets) {
            if (budget.getTimestamp() == null) {
                continue;
            }
            LocalDateTime newTimestamp = budget.getTimestamp().plusWeeks(1);
            budget.setAlreadySpent(0.00);
            budget.setTimestamp(newTimestamp);
            budgetRepository.save(budget);
        }
    }


    @Override
    @Transactional
    public Budget createBudget(BudgetCreateDto budget, long groupId) throws ConflictException {
        LOGGER.trace("Creating new budget for group ID {}", groupId);
        budgetValidator.validateForCreation(budget, groupId);

        GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found with ID: " + groupId));

        Budget budgetEnt = budgetMapper.budgetCreateDtoToBudget(budget);
        budgetEnt.setResetFrequency(budget.getResetFrequency());
        if (budget.getCategory() == null) {
            budgetEnt.setCategory(Category.Other);
        } else {
            budgetEnt.setCategory(budget.getCategory());
        }

        budgetEnt.setGroup(group);
        budgetEnt.setAlreadySpent(0.00);

        if (budget.getResetFrequency() == ResetFrequency.MONTHLY) {
            LocalDateTime firstDayOfNextMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            budgetEnt.setTimestamp(firstDayOfNextMonth);
        } else if (budget.getResetFrequency() == ResetFrequency.WEEKLY) {
            LocalDateTime nextMonday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
            budgetEnt.setTimestamp(nextMonday);
        }

        return budgetRepository.save(budgetEnt);
    }

    @Override
    @Transactional
    public Budget updateBudget(BudgetDto budgetDto, long groupId) throws ConflictException {
        LOGGER.trace("Updating budget ID {} for group ID {}", budgetDto.getId(), groupId);
        budgetValidator.validateForUpdate(budgetDto, groupId);
        Budget budget = budgetRepository.findByIdAndGroupId(budgetDto.getId(), groupId)
            .orElseThrow(() -> new NotFoundException("Budget not found with ID: " + budgetDto.getId() + " for group ID: " + groupId));

        budget.setName(budgetDto.getName());
        budget.setAmount(budgetDto.getAmount());

        if (budget.getResetFrequency() != budgetDto.getResetFrequency()) {
            budget.setResetFrequency(budgetDto.getResetFrequency());
            if (budget.getResetFrequency() == ResetFrequency.MONTHLY) {
                LocalDateTime firstDayOfNextMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                budget.setTimestamp(firstDayOfNextMonth);
            } else if (budget.getResetFrequency() == ResetFrequency.WEEKLY) {
                LocalDateTime nextMonday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
                budget.setTimestamp(nextMonday);
            }
        }
        if (budget.getCategory() != budgetDto.getCategory()) {
            budget.setCategory(budgetDto.getCategory());
            budget.setAlreadySpent(0);
        }
        return budgetRepository.save(budget);
    }

    @Override
    public void addUsedAmount(long groupId, double amount, Category category, LocalDateTime expenseDate) {
        LOGGER.trace("Adding used amount of {} to all budgets for group ID {} and category {}", amount, groupId, category);

        List<Budget> budgets = this.findAllByGroupId(groupId);
        if (budgets.isEmpty()) {
            return;
        }

        LocalDateTime firstDayOfCurrentMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime firstDayOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
        budgets.forEach(budget -> {
            if (budget.getCategory() == category) {
                if ((budget.getResetFrequency() == ResetFrequency.MONTHLY && firstDayOfCurrentMonth.isBefore(expenseDate))
                    || (budget.getResetFrequency() == ResetFrequency.WEEKLY && firstDayOfWeek.isBefore(expenseDate))) {
                    double newUsedAmount = budget.getAlreadySpent() + amount;
                    budget.setAlreadySpent(newUsedAmount);
                    budgetRepository.save(budget);
                }
            }
        });
    }

    @Override
    @Transactional
    public void removeUsedAmount(long groupId, double amount, Category category, LocalDateTime expenseDate) {
        LOGGER.trace("Removing used amount of {} from all budgets for group ID {} and category {}", amount, groupId, category);

        List<Budget> budgets = this.findAllByGroupId(groupId);
        if (budgets.isEmpty()) {
            return;
        }

        LocalDateTime firstDayOfCurrentMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime firstDayOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);

        budgets.forEach(budget -> {
            if (budget.getCategory() == category) {
                if ((budget.getResetFrequency() == ResetFrequency.MONTHLY && firstDayOfCurrentMonth.isBefore(expenseDate))
                    || (budget.getResetFrequency() == ResetFrequency.WEEKLY && firstDayOfWeek.isBefore(expenseDate))) {
                    double newUsedAmount = budget.getAlreadySpent() - amount;
                    budget.setAlreadySpent(newUsedAmount);
                    budgetRepository.save(budget);
                }
            }
        });

    }


    @Override
    @Transactional
    public void deleteBudget(long groupId, long budgetId) {
        LOGGER.trace("Deleting budget ID {} for group ID {}", budgetId, groupId);
        Budget budget = budgetRepository.findByIdAndGroupId(budgetId, groupId)
            .orElseThrow(() -> new NotFoundException("Budget not found with ID: " + budgetId + " for group ID: " + groupId));

        budgetRepository.delete(budget);
    }

    @Override
    @Transactional
    public Budget findByGroupIdAndBudgetId(long groupId, long budgetId) {
        LOGGER.trace("Fetching budget ID {} for group ID {}", budgetId, groupId);
        return budgetRepository.findByIdAndGroupId(budgetId, groupId)
            .orElseThrow(() -> new NotFoundException("Budget not found with ID: " + budgetId + " for group ID: " + groupId));
    }
}
