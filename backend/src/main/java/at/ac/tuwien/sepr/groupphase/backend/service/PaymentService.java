package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface PaymentService {

    /**
     * Service to create a payment.
     *
     * @param paymentDto   payment to be created
     * @param creatorEmail email of the user who made the request (must be the payer)
     * @return the created payment
     * @throws ValidationException if the payment is not valid
     * @throws ConflictException   if the payment cannot be created due to a conflict
     */
    PaymentDto createPayment(PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException;

    /**
     * Service to get a payment by id.
     *
     * @param id           id of the payment
     * @param creatorEmail email of the user who made the request
     * @return the payment
     * @throws NotFoundException if the payment is not found
     */
    PaymentDto getById(long id, String creatorEmail) throws NotFoundException;


    /**
     * Updates an existing payment.
     *
     * @param paymentId    the id of the payment to update
     * @param paymentDto   the updated payment
     * @param creatorEmail the email of the user updating the payment
     * @return the updated payment
     * @throws ValidationException if the payment is not valid
     * @throws ConflictException   if the payment cannot be updated
     * @throws NotFoundException   if the payment does not exist
     */
    PaymentDto updatePayment(Long paymentId, PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException;


    /**
     * Deletes an existing payment.
     *
     * @param paymentId    the id of the payment to delete
     * @param creatorEmail the email of the user deleting the payment
     * @throws NotFoundException if the payment is not found
     * @throws ConflictException if the payment was already deleted
     */
    void deletePayment(Long paymentId, String creatorEmail) throws NotFoundException, ConflictException;

    /**
     * Recovers a deleted payment.
     *
     * @param paymentId    the id of the payment to recover
     * @param creatorEmail the email of the user recovering the payment
     * @return the recovered payments
     * @throws ConflictException if the payment was not deleted
     * @throws NotFoundException if the payment is not found
     */
    PaymentDto recoverPayment(Long paymentId, String creatorEmail) throws ConflictException, NotFoundException;
}
