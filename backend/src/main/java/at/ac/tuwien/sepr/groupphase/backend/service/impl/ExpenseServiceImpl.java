package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.BudgetService;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ExpenseRepository expenseRepository;
    private final ActivityRepository activityRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    @Override
    @Transactional
    public ExpenseDetailDto getById(Long expenseId, String requesterEmail) throws NotFoundException {
        LOGGER.debug("parameters {}, {}", expenseId, requesterEmail);

        ApplicationUser user = userRepository.findByEmail(requesterEmail);
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("Expense not found"));
        if (!expense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this expense");
        }

        return expenseMapper.expenseEntityToExpenseDetailDto(expense);
    }

    @Override
    @Transactional
    public ExpenseCreateDto createExpense(ExpenseCreateDto expenseCreateDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.debug("parameters {}, {}", expenseCreateDto, creatorEmail);
        expenseValidator.validateForCreation(expenseCreateDto);

        Expense expense = expenseMapper.expenseCreateDtoToExpenseEntity(expenseCreateDto);
        expense.setDate(LocalDateTime.now());
        expense.setDeleted(false);
        if (expense.getCategory() == null) {
            expense.setCategory(Category.Other);
        }

        ApplicationUser user = userRepository.findByEmail(creatorEmail);

        budgetService.addUsedAmount(expenseCreateDto.getGroupId(), expense.getAmount(), expense.getCategory());

        Expense expenseSaved = expenseRepository.save(expense);
        Activity activityForExpense = Activity.builder().category(ActivityCategory.EXPENSE).expense(expenseSaved).timestamp(LocalDateTime.now()).group(expenseSaved.getGroup()).user(user).build();

        activityRepository.save(activityForExpense);

        return expenseMapper.expenseEntityToExpenseCreateDto(expenseSaved);
    }

    @Override
    @Transactional
    public ExpenseCreateDto updateExpense(Long expenseId, ExpenseCreateDto expenseCreateDto, String updaterEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.debug("parameters {}", expenseCreateDto);
        Expense existingExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        expenseCreateDto.setGroupId(existingExpense.getGroup().getId());
        expenseValidator.validateForCreation(expenseCreateDto);

        Expense expense = expenseMapper.expenseCreateDtoToExpenseEntity(expenseCreateDto);
        expense.setId(expenseId);
        expense.setDate(existingExpense.getDate());
        if (expense.getCategory() == null) {
            expense.setCategory(Category.Other);
        }

        if ((existingExpense.getAmount() != expense.getAmount()) || existingExpense.getCategory() != expense.getCategory()) {
            budgetService.removeUsedAmount(existingExpense.getGroup().getId(), existingExpense.getAmount(), existingExpense.getCategory());
            budgetService.addUsedAmount(expenseCreateDto.getGroupId(), expense.getAmount(), expense.getCategory());
        }

        Expense expenseSaved = expenseRepository.save(expense);


        ApplicationUser user = userRepository.findByEmail(updaterEmail);
        Activity activityForExpenseUpdate = Activity.builder().category(ActivityCategory.EXPENSE_UPDATE).expense(expenseSaved).timestamp(LocalDateTime.now()).group(expenseSaved.getGroup()).user(user).build();

        activityRepository.save(activityForExpenseUpdate);

        return expenseMapper.expenseEntityToExpenseCreateDto(expenseSaved);
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId, String deleterEmail) throws NotFoundException, ConflictException {
        LOGGER.debug("parameters {} {}", expenseId, deleterEmail);
        Expense existingExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        ApplicationUser user = userRepository.findByEmail(deleterEmail);

        if (!existingExpense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to delete this expense");
        }

        if (existingExpense.isDeleted()) {
            throw new ConflictException("Invalid delete operation", List.of("Expense is already marked as deleted"));
        }

        expenseRepository.markExpenseAsDeleted(existingExpense);

        budgetService.removeUsedAmount(existingExpense.getGroup().getId(), existingExpense.getAmount(), existingExpense.getCategory());

        Activity activityForExpenseDelete = Activity.builder().category(ActivityCategory.EXPENSE_DELETE).timestamp(LocalDateTime.now()).expense(existingExpense).group(existingExpense.getGroup()).user(user).build();

        activityRepository.save(activityForExpenseDelete);
    }

    @Override
    @Transactional
    public ExpenseCreateDto recoverExpense(Long expenseId, String recoverEmail) throws NotFoundException, ConflictException {
        LOGGER.debug("parameters {} {}", expenseId, recoverEmail);
        Expense existingExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        GroupEntity existingGroup = existingExpense.getGroup();
        ApplicationUser user = userRepository.findByEmail(recoverEmail);

        if (!existingGroup.getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to recover this expense");
        }

        if (!existingExpense.isDeleted()) {
            throw new ConflictException("Invalid recover operation", List.of("Expense is not marked as deleted"));
        }

        expenseRepository.markExpenseAsRecovered(existingExpense);

        existingExpense.setDeleted(false);

        Activity activityForExpenseRecover = Activity.builder().category(ActivityCategory.EXPENSE_RECOVER).timestamp(LocalDateTime.now()).expense(existingExpense).group(existingGroup).user(user).build();

        activityRepository.save(activityForExpenseRecover);

        return expenseMapper.expenseEntityToExpenseCreateDto(existingExpense);
    }
}
