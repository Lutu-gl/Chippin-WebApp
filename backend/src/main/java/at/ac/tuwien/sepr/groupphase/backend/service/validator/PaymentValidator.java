package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator for all payment related inputs.
 */
@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PaymentRepository paymentRepository;


    @Transactional
    public void validateForCreation(PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException {
        LOGGER.trace("validateForCreation({}, {})", paymentDto, creatorEmail);

        List<String> validationErrors = new ArrayList<>();


        checkCreatorEmailEqualsPayerEmail(paymentDto, creatorEmail, validationErrors);
        checkReceiverNotEqualPalyerEmail(paymentDto, validationErrors);


        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of expense for creation failed", validationErrors);
        }

        List<String> conflictErrors = new ArrayList<>();

        if (checkPayerExist(paymentDto, conflictErrors) && checkReceiverExists(paymentDto, conflictErrors) && checkGroupExists(paymentDto, conflictErrors)) {
            checkPayerAndReceiverAreInGroup(paymentDto, conflictErrors);
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("expense creation failed because of conflict", conflictErrors);
        }
    }

    private boolean checkReceiverNotEqualPalyerEmail(PaymentDto paymentDto, List<String> validationErrors) {
        LOGGER.trace("checkReceiverNotEqualPalyerEmail({})", paymentDto);

        if (paymentDto.getPayerEmail().equals(paymentDto.getReceiverEmail())) {
            validationErrors.add("Payer and receiver must not be the same person");
            return false;
        }

        return true;
    }

    @Transactional
    protected boolean checkPayerAndReceiverAreInGroup(PaymentDto paymentDto, List<String> conflictErrors) {
        LOGGER.trace("checkPayerAndReceiverAreInGroup({})", paymentDto);

        ApplicationUser payer = userRepository.findByEmail(paymentDto.getPayerEmail());
        ApplicationUser receiver = userRepository.findByEmail(paymentDto.getReceiverEmail());
        GroupEntity group = groupRepository.findById(paymentDto.getGroupId()).get();
        if (group == null) {
            conflictErrors.add("Group does not exist");
            return false;
        }

        if (!payer.getGroups().contains(group)) {
            conflictErrors.add("Payer is not in the group");
            return false;
        }
        if (!receiver.getGroups().contains(group)) {
            conflictErrors.add("Receiver is not in the group");
            return false;
        }


        return true;
    }

    private boolean checkReceiverExists(PaymentDto paymentDto, List<String> conflictErrors) {
        LOGGER.trace("checkReceiverExists({})", paymentDto);

        ApplicationUser byEmail = userRepository.findByEmail(paymentDto.getReceiverEmail());
        if (byEmail == null) {
            conflictErrors.add("Receiver does not exist");
            return false;
        }
        return true;
    }

    private boolean checkPayerExist(PaymentDto paymentDto, List<String> conflictErrors) {
        LOGGER.trace("checkPayerExist({})", paymentDto);

        ApplicationUser byEmail = userRepository.findByEmail(paymentDto.getPayerEmail());
        if (byEmail == null) {
            conflictErrors.add("Payer does not exist");
            return false;
        }

        return true;
    }

    private boolean checkCreatorEmailEqualsPayerEmail(PaymentDto paymentDto, String creatorEmail, List<String> validationErrors) {
        LOGGER.trace("checkCreatorEmailEqualsPayerEmail({}, {})", paymentDto, creatorEmail);

        if (!paymentDto.getPayerEmail().equals(creatorEmail)) {
            validationErrors.add("Creator email must be the same as payer email");
            return false;
        }

        return true;
    }

    private boolean checkGroupExists(PaymentDto paymentDto, List<String> confictErrors) {
        LOGGER.trace("checkGroupExists({})", paymentDto);

        Long groupId = paymentDto.getGroupId();
        if (groupRepository.findById(groupId).isEmpty()) {
            confictErrors.add("Group does not exist");
            return false;
        }

        return true;
    }

    @Transactional
    public void validateForUpdate(PaymentDto paymentDto, String creatorEmail, Payment existingPayment) throws ValidationException, ConflictException {
        LOGGER.trace("validateForUpdate({}, {}, {})", paymentDto, creatorEmail, existingPayment);

        List<String> validationErrors = new ArrayList<>();

        //checkCreatorEmailEqualsPayerEmail(paymentDto, creatorEmail, validationErrors);
        //checkCreatorEmailEqualsPayerOrReceiverEmail(paymentDto, creatorEmail, validationErrors);
        checkReceiverNotEqualPalyerEmail(paymentDto, validationErrors);
        checkOnlyAmountChanged(paymentDto, existingPayment, validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of expense for creation failed", validationErrors);
        }

        List<String> conflictErrors = new ArrayList<>();

        if (checkPayerExist(paymentDto, conflictErrors) && checkReceiverExists(paymentDto, conflictErrors) && checkGroupExists(paymentDto, conflictErrors)) {
            checkPayerAndReceiverAreInGroup(paymentDto, conflictErrors);
            checkPaymentNotArchived(existingPayment, conflictErrors);
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("expense creation failed because of conflict", conflictErrors);
        }
    }

    private boolean checkPaymentNotArchived(Payment paymentDto, List<String> conflictErrors) {
        LOGGER.trace("checkPaymentNotArchived({})", paymentDto);
        if (paymentDto.getArchived() != null && paymentDto.getArchived()) {
            conflictErrors.add("Payment is archived and cannot be updated");
            return false;
        }
        return true;
    }

    private void checkCreatorEmailEqualsPayerOrReceiverEmail(PaymentDto paymentDto, String creatorEmail, List<String> validationErrors) {
        LOGGER.trace("checkCreatorEmailEqualsPayerOrReceiverEmail({}, {})", paymentDto, creatorEmail);

        if (!paymentDto.getPayerEmail().equals(creatorEmail) && !paymentDto.getReceiverEmail().equals(creatorEmail)) {
            validationErrors.add("Creator email must be the same as payer or receiver email");
        }
    }

    private boolean checkOnlyAmountChanged(PaymentDto paymentDto, Payment existingPayment, List<String> validationErrors) {
        LOGGER.trace("checkOnlyAmountChanged({}, {})", paymentDto, existingPayment);
        if (!paymentDto.getPayerEmail().equals(existingPayment.getPayer().getEmail())) {
            validationErrors.add("Payer cannot be changed");
            return false;
        }
        if (!paymentDto.getReceiverEmail().equals(existingPayment.getReceiver().getEmail())) {
            validationErrors.add("Receiver cannot be changed");
            return false;
        }

        return true;
    }
}
