package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
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
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;

    @Override
    @Transactional
    public ExpenseCreateDto createExpense(ExpenseCreateDto expenseCreateDto) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.debug("parameters {}", expenseCreateDto);
        expenseValidator.validateForCreation(expenseCreateDto);

        Expense expense = expenseMapper.expenseCreateDtoToExpenseEntity(expenseCreateDto);
        expense.setDate(LocalDateTime.now());
        if (expense.getCategory() == null) {
            expense.setCategory(Category.Other);
        }

        Expense expenseSaved = expenseRepository.save(expense);

        return expenseMapper.expenseEntityToExpenseCreateDto(expenseSaved);
    }
}
