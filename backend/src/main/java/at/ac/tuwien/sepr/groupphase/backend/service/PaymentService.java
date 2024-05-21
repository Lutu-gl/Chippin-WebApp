package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
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

}
