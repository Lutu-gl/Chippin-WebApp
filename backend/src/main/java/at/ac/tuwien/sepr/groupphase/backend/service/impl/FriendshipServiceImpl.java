package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidFriendRequest;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

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
    public Collection<String> getFriends(String email) {
        ApplicationUser user = userService.findApplicationUserByEmail(email);
        return friendshipRepository
            .findFriendsOfUser(user)
            .stream()
            .map(ApplicationUser::getEmail)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getIncomingFriendRequest(String email) {
        ApplicationUser user = userService.findApplicationUserByEmail(email);
        return friendshipRepository
            .findIncomingFriendRequestsOfUser(user)
            .stream()
            .map(ApplicationUser::getEmail)
            .collect(Collectors.toList());
    }

    @Override
    public void sendFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest {
        LOGGER.debug("Send friend request to {}", receiverEmail);

        if (senderEmail.equals(receiverEmail)) {
            throw new InvalidFriendRequest("You can not send a friend request to yourself!");
        }

        // get authenticated application user
        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);

        // get the receiver application user
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("------------------------");
        System.out.println(sender.getEmail());
        System.out.println(receiver.getEmail());
        System.out.println("------------------------");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");

        // check if receiver already sent a friend request
        // if so, accept that friend request and return
        if (friendshipRepository.pendingFriendRequestExists(receiver, sender)) {
            friendshipRepository.acceptFriendRequest(receiver, sender);
            return;
        }

        // check if there is already a pending or accepted friendship between the two users
        // if so, throw an exception
        if (friendshipRepository.anyFriendshipRelationBetweenUsersExists(sender, receiver)) {
            throw new InvalidFriendRequest("The receiver is already your friend or you have already sent a friend request!");
        }

        // create friendship entity
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);
        friendship.setSentAt(LocalDateTime.now());

        // persist it
        friendshipRepository.save(friendship);
    }

    @Override
    public void acceptFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest {
        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        if (!friendshipRepository.acceptFriendRequest(sender, receiver)) {
            throw new InvalidFriendRequest("There was no pending friend request from this sender!");
        }
    }

    @Override
    public void rejectFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest {
        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        if (!friendshipRepository.rejectFriendRequest(sender, receiver)) {
            throw new InvalidFriendRequest("There was no pending friend request from this sender!");
        }
    }
}
