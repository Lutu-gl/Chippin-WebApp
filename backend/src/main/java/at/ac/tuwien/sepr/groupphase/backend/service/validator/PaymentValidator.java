package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
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


    @Transactional
    public void validateForCreation(PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException {
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
        if (paymentDto.getPayerEmail().equals(paymentDto.getReceiverEmail())) {
            validationErrors.add("Payer and receiver must not be the same person");
            return false;
        }

        return true;
    }

    @Transactional
    protected boolean checkPayerAndReceiverAreInGroup(PaymentDto paymentDto, List<String> conflictErrors) {
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
        ApplicationUser byEmail = userRepository.findByEmail(paymentDto.getReceiverEmail());
        if (byEmail == null) {
            conflictErrors.add("Receiver does not exist");
            return false;
        }
        return true;
    }

    private boolean checkPayerExist(PaymentDto paymentDto, List<String> conflictErrors) {
        ApplicationUser byEmail = userRepository.findByEmail(paymentDto.getPayerEmail());
        if (byEmail == null) {
            conflictErrors.add("Payer does not exist");
            return false;
        }

        return true;
    }

    private boolean checkCreatorEmailEqualsPayerEmail(PaymentDto paymentDto, String creatorEmail, List<String> validationErrors) {
        if (!paymentDto.getPayerEmail().equals(creatorEmail)) {
            validationErrors.add("Creator email must be the same as payer email");
            return false;
        }

        return true;
    }

    private boolean checkGroupExists(PaymentDto paymentDto, List<String> confictErrors) {
        Long groupId = paymentDto.getGroupId();
        if (groupRepository.findById(groupId).isEmpty()) {
            confictErrors.add("Group does not exist");
            return false;
        }

        return true;
    }
}
