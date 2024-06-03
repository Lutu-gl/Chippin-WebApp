package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.AcceptFriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.FriendInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.FriendRequestDto;
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

import java.lang.invoke.MethodHandles;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/friendship")
public class FriendshipEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FriendshipService friendshipService;

    public FriendshipEndpoint(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Send a friend request", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.CREATED)
    public void sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) throws InvalidFriendRequest {
        LOGGER.trace("sendFriendRequest({})", friendRequestDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        friendshipService.sendFriendRequest(senderEmail, friendRequestDto.getReceiverEmail());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/friend-requests")
    public Collection<String> getIncomingFriendRequest() {
        LOGGER.trace("getIncomingFriendRequest()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return friendshipService.getIncomingFriendRequest(email);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/outgoing-friend-requests")
    public Collection<String> getOutgoingFriendRequest() {
        LOGGER.trace("getOutgoingFriendRequest()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return friendshipService.getOutgoingFriendRequest(email);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/friends")
    public Collection<String> getFriends() {
        LOGGER.trace("getFriends()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return friendshipService.getFriends(email);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/friends-with-debt-infos")
    public Collection<FriendInfoDto> getFriendsWithDebtInfos() {
        LOGGER.trace("getFriendsWithDebtInfos()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return friendshipService.getFriendsWithDebtInfos(email);
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/accept")
    public void acceptFriendRequest(@RequestBody AcceptFriendRequestDto acceptFriendRequestDto) throws InvalidFriendRequest {
        LOGGER.trace("acceptFriendRequest({})", acceptFriendRequestDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        friendshipService.acceptFriendRequest(acceptFriendRequestDto.getSenderEmail(), email);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/reject/{sender-email}")
    public void rejectFriendRequest(@PathVariable(name = "sender-email") String senderEmail) throws InvalidFriendRequest {
        LOGGER.trace("rejectFriendRequest({})", senderEmail);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        friendshipService.rejectFriendRequest(senderEmail, email);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/retract/{receiver-email}")
    public void retractFriendRequest(@PathVariable(name = "receiver-email") String receiverEmail) throws InvalidFriendRequest {
        LOGGER.trace("retractFriendRequest({})", receiverEmail);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        friendshipService.retractFriendRequest(email, receiverEmail);
    }
}
