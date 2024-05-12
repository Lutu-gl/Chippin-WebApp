package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AcceptFriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.FriendshipMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidFriendRequest;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/friendship")
public class FriendshipEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FriendshipService friendshipService;
    private final FriendshipMapper friendshipMapper;

    public FriendshipEndpoint(FriendshipService friendshipService, FriendshipMapper friendshipMapper) {
        this.friendshipService = friendshipService;
        this.friendshipMapper = friendshipMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Send a friend request", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.CREATED)
    public void sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        LOGGER.info("POST /api/v1/friendship body: {}", friendRequestDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        try {
            friendshipService.sendFriendRequest(senderEmail, friendRequestDto.getReceiverEmail());
        } catch (InvalidFriendRequest e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/friend-requests")
    public Collection<String> getIncomingFriendRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return friendshipService.getIncomingFriendRequest(email);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/friends")
    public Collection<String> getFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return friendshipService.getFriends(email);
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/accept")
    public void acceptFriendRequest(@RequestBody AcceptFriendRequestDto acceptFriendRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            friendshipService.acceptFriendRequest(acceptFriendRequestDto.getSenderEmail(), email);
        } catch (InvalidFriendRequest e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/reject/{sender-email}")
    public void rejectFriendRequest(@PathVariable(name = "sender-email") String senderEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            friendshipService.rejectFriendRequest(senderEmail, email);
        } catch (InvalidFriendRequest e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
