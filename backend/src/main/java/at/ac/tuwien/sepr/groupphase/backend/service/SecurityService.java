package at.ac.tuwien.sepr.groupphase.backend.service;

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

    /**
     * Checks if the given id corresponds to the currently authenticated user.
     *
     * @param id the id to check
     * @return true if the id corresponds to the currently authenticated user, false otherwise
     */
    public boolean hasCorrectId(Long id) {
        log.debug("Checking if the given id {} corresponds to the currently authenticated user", id);
        log.debug("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (id == null) {
            log.debug("Id is null");
            return false;
        }
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
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
}
