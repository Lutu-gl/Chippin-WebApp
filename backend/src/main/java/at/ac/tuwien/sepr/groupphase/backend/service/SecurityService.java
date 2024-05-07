package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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
        log.debug("Checking if the given id corresponds to the currently authenticated user with id: {}", id);
        log.debug("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (id == null) {
            return false;
        }
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return false;
        }
        return user.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
