package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * This service provides methods to check the validity of requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final UserRepository userRepository;
    private final ShoppingListRepository shoppingListRepository;

    /**
     * Checks if the given id corresponds to the currently authenticated user.
     *
     * @param userId the id to check
     * @return true if the id corresponds to the currently authenticated user, false otherwise
     */
    public boolean hasCorrectId(Long userId) {
        log.debug("Checking if the given id {} corresponds to the currently authenticated user", userId);
        log.debug("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userId == null) {
            log.debug("Id is null");
            return false;
        }
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.debug("Could not find user with id {}", userId);
            return false;
        }
        return user.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    /**
     * Checks if the given group id corresponds to a group the currently authenticated user is part of.
     *
     * @param groupId the group id to check
     * @return true if the group id corresponds to a group the currently authenticated user is part of, false otherwise
     */
    @Transactional
    public boolean isGroupMember(Long groupId) {
        log.debug("Checking if the given group id corresponds to the currently authenticated user");
        log.debug("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (groupId == null) {
            log.debug("Group id is null");
            return false;
        }
        var user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        if (user == null) {
            log.debug("Could not find current user");
            return false;
        }
        boolean isMember = user.getGroups().stream().anyMatch(group -> group.getId().equals(groupId));
        if (!isMember) {
            log.warn("User is not a member of the group with id {}", groupId);
        }
        return isMember;
    }

    @Transactional
    public boolean canAccessShoppingList(Long shoppingListId) {
        log.debug("Checking if the currently authenticated user can access the shopping list with id {}", shoppingListId);
        log.debug("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (shoppingListId == null) {
            log.debug("Shopping list id is null");
            return false;
        }
        var user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        if (user == null) {
            log.debug("Could not find current user");
            return false;
        }
        var shoppingList = shoppingListRepository.findById(shoppingListId).orElse(null);
        if (shoppingList == null) {
            log.debug("Could not find shopping list with id {}", shoppingListId);
            return false;
        }
        boolean isOwner = shoppingList.getOwner().getId().equals(user.getId());
        log.debug("User is owner: {}", isOwner);
        boolean isGroupMember =
            shoppingList.getGroup() != null && user.getGroups().stream().anyMatch(group -> group.getId().equals(shoppingList.getGroup().getId()));
        log.debug("User is a member of the group that the shopping list belongs to: {}", isGroupMember);
        boolean canEdit = isOwner || isGroupMember;
        if (!canEdit) {
            log.warn("User is not allowed to edit shopping list with id {}", shoppingListId);
        }
        return canEdit;
    }

}
