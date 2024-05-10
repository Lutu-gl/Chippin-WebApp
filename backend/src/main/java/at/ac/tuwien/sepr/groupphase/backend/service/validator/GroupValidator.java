package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.nodes.CollectionNode;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validator for all group related inputs.
 */
@Component
public class GroupValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Validator validator;
    private final UserRepository userRepository;

    @Autowired
    public GroupValidator(UserRepository userRepository) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        this.userRepository = userRepository;
    }

    public void validateForCreation(GroupCreateDto group, String ownerEmail) throws ValidationException, ConflictException {

        LOGGER.trace("validateForCreation({})", group);
        List<String> validationErrors = new ArrayList<>();

        Set<ConstraintViolation<GroupCreateDto>> violations = validator.validate(group);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<GroupCreateDto> violation : violations) {
                validationErrors.add(violation.getMessage());
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for creation failed", validationErrors);
        }

        List<String> confictErrors = new ArrayList<>();

        checkOwnerFriendsWithEveryOne(group, ownerEmail, confictErrors);
        checkGroupMembersExist(group, confictErrors);

        if (!confictErrors.isEmpty()) {
            throw new ConflictException("group creation failed because of conflict", confictErrors);
        }
    }

    private boolean checkGroupMembersExist(GroupCreateDto group, List<String> conflictErrors) {
        Set<String> memberEmails = group.getMembers();
        boolean allMembersExist = true;

        for (String email : memberEmails) {
            boolean exists = userRepository.findByEmail(email) != null;
            if (!exists) {
                conflictErrors.add("No user found with email: " + email);
                allMembersExist = false;
            }
        }

        return allMembersExist;
    }

    private boolean checkOwnerFriendsWithEveryOne(GroupCreateDto group, String ownerEmail, List<String> conflictErrors) {
        if (group.getMembers() == null || group.getMembers().isEmpty()) {
            conflictErrors.add("No members provided in the group.");
            return false;
        }

        // Check if ownerEmail is included in the members list
        if (!group.getMembers().contains(ownerEmail)) {
            conflictErrors.add("Owner must be a member of the group.");
            return false;
        }

        /* TODO: Check this when friends are implemented!
        for (String memberEmail : group.getMembers()) {
            if (!memberEmail.equals(ownerEmail)) { // No need to check if the owner is friends with themselves
                boolean areFriends = friendshipService.areFriends(ownerEmail, memberEmail);
                if (!areFriends) {
                    conflictErrors.add("Owner is not friends with member: " + memberEmail);
                    return false;
                }
            }
        }*/
        return true;
    }
}
