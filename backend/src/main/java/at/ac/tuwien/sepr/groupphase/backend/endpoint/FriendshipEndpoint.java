package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.FriendshipMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        LOGGER.info("POST /api/v1/friendship body: {}", friendRequestDto);

        //System.out.println("-------------------------");
        //System.out.println(friendRequestDto.getReceiverEmail());
        //System.out.println("-------------------------");

        friendshipService.sendFriendRequest(friendRequestDto.getReceiverEmail());
    }

}
