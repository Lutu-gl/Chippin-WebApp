package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.http.ResponseEntity;

public interface ExpenseService {

    /**
     * Get a specific expense by id.
     *
     * @param id the id of the expense
     * @param requesterEmail the email address of the user who requests the expense
     * @return the expense
     * @throws NotFoundException if the expense is not found
     */
    ExpenseDetailDto getById(Long id, String requesterEmail) throws NotFoundException;

    /**
     * Get the bill of a specific expense.
     *
     * @param id the id of the expense
     * @param requesterEmail the email address of the user who requests the bill
     * @return a response entity with the bill as byte array and the appropriate headers set
     * @throws NotFoundException if the expense is not found
     */
    ResponseEntity<byte[]> getBill(Long id, String requesterEmail) throws NotFoundException;

    /**
     * Creates a new expense.
     *
     * @param expenseCreateDto the expense to be created
     * @param creatorEmail     the email of the user creating the expense
     * @return the created expense
     * @throws ValidationException if the expense is not valid
     * @throws ConflictException   if the expense cannot be created
     * @throws NotFoundException   if the group or payer does not exist
     */
    ExpenseCreateDto createExpense(ExpenseCreateDto expenseCreateDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException;


    /**
     * Updates an existing expense.
     *
     * @param expenseId the id of the expense to update
     * @param expenseCreateDto the updated expense
     * @param updaterEmail the email of the user updating the expense
     * @return the updated expense
     * @throws ValidationException if the expense is not valid
     * @throws ConflictException if the expense cannot be updated
     * @throws NotFoundException if the expense does not exist
     */
    ExpenseCreateDto updateExpense(Long expenseId, ExpenseCreateDto expenseCreateDto, String updaterEmail) throws ValidationException, ConflictException, NotFoundException;

    /**
     * Deletes an existing expense.
     *
     * @param expenseId the id of the expense to delete
     * @param deleterEmail the email of the user deleting the expense
     * @throws NotFoundException if the expense is not found
     */
    void deleteExpense(Long expenseId, String deleterEmail) throws NotFoundException, ConflictException;

    /**
     * Recovers a deleted expense.
     *
     * @param expenseId the idd of expense to recover
     * @param recoverEmail the email of the user recovering the expense
     * @return the recovered expense
     * @throws NotFoundException if the expense is not found
     */
    ExpenseCreateDto recoverExpense(Long expenseId, String recoverEmail) throws NotFoundException, ConflictException;
}
