package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for all group related inputs.
 */
@Component
public class ExpenseValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;


    @Autowired
    public ExpenseValidator(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public void validateForCreation(ExpenseCreateDto expense) throws ValidationException, ConflictException {
        List<String> validationErrors = new ArrayList<>();

        checkPercentagesAddsUpTo1(expense, validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of expense for creation failed", validationErrors);
        }

        List<String> conflictErrors = new ArrayList<>();

        checkParticipantsExist(expense, conflictErrors);
        if (checkGroupExists(expense, conflictErrors)) {
            checkPayerAndParticipantsAreInGroup(expense, conflictErrors);
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("expense creation failed because of conflict", conflictErrors);
        }
    }

    private boolean checkPayerAndParticipantsAreInGroup(ExpenseCreateDto expense, List<String> confictErrors) {
        Long groupId = expense.getGroupId();
        String payer = expense.getPayerEmail();
        if (userRepository.findByEmail(payer) == null) {
            confictErrors.add("Payer with email " + payer + " does not exist");
            return false;
        }

        if (groupRepository.findById(groupId).get().getUsers().stream().noneMatch(user -> user.getEmail().equals(payer))) {
            confictErrors.add("Payer with email " + payer + " is not in group");
            return false;
        }
        Map<String, Double> participants = expense.getParticipants();
        for (String email : participants.keySet()) {
            if (groupRepository.findById(groupId).get().getUsers().stream().noneMatch(user -> user.getEmail().equals(email))) {
                confictErrors.add("Participant with email " + email + " is not in group");
                return false;
            }
        }

        return true;
    }

    private boolean checkGroupExists(ExpenseCreateDto expense, List<String> confictErrors) {
        Long groupId = expense.getGroupId();
        if (groupRepository.findById(groupId).isEmpty()) {
            confictErrors.add("Group does not exist");
            return false;
        }

        return true;
    }

    private boolean checkParticipantsExist(ExpenseCreateDto expense, List<String> confictErrors) {
        Map<String, Double> participants = expense.getParticipants();
        for (String email : participants.keySet()) {
            if (userRepository.findByEmail(email) == null) {
                confictErrors.add("User with email " + email + " does not exist");
                return false;
            }
        }

        return true;
    }

    private boolean checkPercentagesAddsUpTo1(ExpenseCreateDto expense, List<String> validationErrors) {
        Map<String, Double> participants = expense.getParticipants();
        double sum = 0;
        for (Double value : participants.values()) {
            sum += value;
        }

        if (Math.abs(sum - 1) > 0.000001) {
            validationErrors.add("Percentages do not add up to 1");
            return false;
        }
        return true;
    }
}
