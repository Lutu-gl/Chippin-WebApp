package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class BudgetValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetValidator(UserRepository userRepository, GroupRepository groupRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.budgetRepository = budgetRepository;
    }

    public void validateForCreation(BudgetCreateDto budget, long groupId) throws ConflictException {

        List<String> conflictErrors = new ArrayList<>();

        checkGroupExists(groupId, conflictErrors);

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Budget creation failed because of conflict", conflictErrors);
        }
    }

    public void validateForUpdate(BudgetDto budget, long groupId) throws ConflictException {
        List<String> conflictErrors = new ArrayList<>();

        checkBudgetExists(budget, conflictErrors);

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Budget update failed because of conflict", conflictErrors);
        }
    }

    private void checkBudgetExists(BudgetDto budget, List<String> conflictErrors) {
        if (budgetRepository.findById(budget.getId()).isEmpty()) {
            conflictErrors.add("Budget does not exist");
        }
    }

    private boolean checkGroupExists(Long groupId, List<String> confictErrors) {
        if (groupRepository.findById(groupId).isEmpty()) {
            confictErrors.add("Group does not exist");
            return false;
        }

        return true;
    }
}