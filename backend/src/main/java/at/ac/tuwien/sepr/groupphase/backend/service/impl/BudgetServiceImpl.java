package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BudgetRepository budgetRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Budget> findAllByGroupId(long groupId) {
        LOGGER.debug("Finding all budgets for group ID {}", groupId);
        return budgetRepository.findByGroupId(groupId);
    }

    @Override
    @Transactional
    public Budget createBudget(BudgetCreateDto budget, long groupId) {
        LOGGER.debug("Creating a new budget for group ID {}", groupId);
        GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found with ID: " + groupId));

        Budget budgetEnt = new Budget();
        budgetEnt.setName(budget.getName());
        budgetEnt.setAmount(budget.getAmount());
        budgetEnt.setGroup(group);

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
    @Transactional
    public void deleteBudget(long groupId, long budgetId) {
        LOGGER.debug("Deleting budget ID {} for group ID {}", budgetId, groupId);
        Budget budget = budgetRepository.findByIdAndGroupId(budgetId, groupId)
            .orElseThrow(() -> new NotFoundException("Budget not found with ID: " + budgetId + " for group ID: " + groupId));

        budgetRepository.delete(budget);
    }
}
