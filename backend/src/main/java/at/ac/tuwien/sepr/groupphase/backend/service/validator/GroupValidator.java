package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private final GroupRepository groupRepository;
    private final FriendshipRepository friendshipRepository;


    @Autowired
    public GroupValidator(UserRepository userRepository, GroupRepository groupRepository, FriendshipRepository friendshipRepository) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.friendshipRepository = friendshipRepository;
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
        checkAtLeastTwoMembers(group, validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for creation failed", validationErrors);
        }

        List<String> confictErrors = new ArrayList<>();

        checkOwnerMemberOfGroupAndNotEmptyGroup(group, ownerEmail, confictErrors);
        checkGroupMembersExist(group, confictErrors);

        if (!confictErrors.isEmpty()) {
            throw new ConflictException("group creation failed because of conflict", confictErrors);
        }
    }

    private boolean checkAtLeastTwoMembers(GroupCreateDto group, List<String> errors) {
        if (group.getMembers().size() < 2) {
            errors.add("Group must have at least two members.");
            return false;
        }

        return true;
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

    private boolean checkOwnerMemberOfGroupAndNotEmptyGroup(GroupCreateDto group, String ownerEmail, List<String> conflictErrors) {
        if (group.getMembers() == null || group.getMembers().isEmpty()) {
            conflictErrors.add("No members provided in the group.");
            return false;
        }

        // Check if ownerEmail is included in the members list
        if (!group.getMembers().contains(ownerEmail)) {
            conflictErrors.add("Owner must be a member of the group.");
            return false;
        }
        return true;
    }

    public void validateForUpdate(GroupCreateDto groupCreateDto, String ownerEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.trace("validateForUpdate({})", groupCreateDto);

        // Check if the group exists
        if (!groupRepository.findById(groupCreateDto.getId()).isPresent()) {
            throw new NotFoundException("Group to update does not exist");
        }

        List<String> validationErrors = new ArrayList<>();

        Set<ConstraintViolation<GroupCreateDto>> violations = validator.validate(groupCreateDto);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<GroupCreateDto> violation : violations) {
                validationErrors.add(violation.getMessage());
            }
        }

        checkAtLeastTwoMembers(groupCreateDto, validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of group for update failed", validationErrors);
        }

        List<String> conflictErrors = new ArrayList<>();

        checkOwnerMemberOfGroupAndNotEmptyGroup(groupCreateDto, ownerEmail, conflictErrors);
        checkGroupMembersExist(groupCreateDto, conflictErrors);
        //TODO: checkNobodyDeletedWithExpenses(groupCreateDto, conflictErrors);


        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("Update of group failed because of conflict", conflictErrors);
        }
    }

    public boolean validateFriendsWithEveryone(GroupEntity savedGroup) {
        for (int i = 0; i < savedGroup.getUsers().size(); i++) {
            for (int j = i + 1; j < savedGroup.getUsers().size(); j++) {
                ApplicationUser user = (ApplicationUser) savedGroup.getUsers().toArray()[i];
                ApplicationUser user2 = (ApplicationUser) savedGroup.getUsers().toArray()[j];

                if (!friendshipRepository.areFriends(user, user2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
