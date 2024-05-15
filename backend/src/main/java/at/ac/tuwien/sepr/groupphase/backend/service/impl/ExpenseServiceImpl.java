package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ExpenseRepository expenseRepository;
    private final ActivityRepository activityRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ExpenseCreateDto createExpense(ExpenseCreateDto expenseCreateDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.debug("parameters {}", expenseCreateDto);
        expenseValidator.validateForCreation(expenseCreateDto);

        Expense expense = expenseMapper.expenseCreateDtoToExpenseEntity(expenseCreateDto);
        expense.setDate(LocalDateTime.now());
        if (expense.getCategory() == null) {
            expense.setCategory(Category.Other);
        }

        ApplicationUser user = userRepository.findByEmail(creatorEmail);

        Expense expenseSaved = expenseRepository.save(expense);
        Activity activityForExpense = Activity.builder()
            .category(ActivityCategory.EXPENSE)
            .expense(expenseSaved)
            .timestamp(LocalDateTime.now())
            .group(expenseSaved.getGroup())
            .user(user)
            .build();
        activityForExpense = activityRepository.save(activityForExpense);

        return expenseMapper.expenseEntityToExpenseCreateDto(expenseSaved);
    }
}
