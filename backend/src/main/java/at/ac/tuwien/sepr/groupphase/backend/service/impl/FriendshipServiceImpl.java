package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FriendshipRepository friendshipRepository;
    private final UserService userService;

    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }


    @Override
    public void sendFriendRequest(String receiverEmail) {
        LOGGER.debug("Send friend request to {}", receiverEmail);

        // get email of authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = authentication.getName();
        // get authenticated application user
        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);

        // get the receiver application user
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        System.out.println(senderEmail);
        System.out.println(sender);
        System.out.println(receiverEmail);
        System.out.println(receiver);
        System.out.println("---------------------------");

        // create friendship entity
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);
        friendship.setSentAt(LocalDateTime.now());

        // persist it
        friendshipRepository.save(friendship);
    }
}
