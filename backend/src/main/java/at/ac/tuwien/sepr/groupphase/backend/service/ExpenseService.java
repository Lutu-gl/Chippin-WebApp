package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface ExpenseService {
    /**
     * Creates a new expense.
     *
     * @param expenseCreateDto the expense to be created
     * @param creatorEmail     the email of the user creating the expense
     * @throws ValidationException if the expense is not valid
     * @throws ConflictException   if the expense cannot be created
     * @throws NotFoundException   if the group or payer does not exist
     */
    ExpenseCreateDto createExpense(ExpenseCreateDto expenseCreateDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException;


}
