package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.FriendInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.InvalidFriendRequest;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DebtService;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final DebtService debtService;
    private final GroupMapper groupMapper;

    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, UserService userService, DebtService debtService, GroupMapper groupMapper) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
        this.debtService = debtService;
        this.groupMapper = groupMapper;
    }

    @Override
    public Collection<String> getFriends(String email) {
        LOGGER.trace("getFriends({})", email);

        ApplicationUser user = userService.findApplicationUserByEmail(email);
        return friendshipRepository
            .findFriendsOfUser(user)
            .stream()
            .map(ApplicationUser::getEmail)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<FriendInfoDto> getFriendsWithDebtInfos(String email) {
        LOGGER.trace("getFriendsWithDebtInfos({})", email);

        ApplicationUser user = userService.findApplicationUserByEmail(email);
        List<ApplicationUser> friends = friendshipRepository.findFriendsOfUser(user);
        Set<GroupEntity> groups = userService.getGroupsByUserEmail(user.getEmail());
        Map<Long, Map<String, Double>> debtsPerGroup = new HashMap<>();
        for (GroupEntity group : groups) {
            DebtGroupDetailDto debts = debtService.getById(user.getEmail(), group.getId());
            debtsPerGroup.put(group.getId(), debts.getMembersDebts());
        }

        List<FriendInfoDto> friendsInfos = new ArrayList<>();
        for (ApplicationUser friend : friends) {
            FriendInfoDto friendInfoDto = FriendInfoDto.builder().email(friend.getEmail()).totalAmount(0.0).groupAmounts(new HashMap<>()).build();

            for (GroupEntity group : groups) {
                if (group.getUsers().stream().anyMatch(u -> u.getEmail().equals(friend.getEmail()))) {

                    Double debt = debtsPerGroup.get(group.getId()).get(friend.getEmail());
                    friendInfoDto.setTotalAmount(friendInfoDto.getTotalAmount() + debt);
                    friendInfoDto.getGroupAmounts().put(groupMapper.groupEntityToGroupDto(group), debt);
                }
            }
            friendsInfos.add(friendInfoDto);
        }

        return friendsInfos;
    }

    @Override
    public Collection<String> getIncomingFriendRequest(String email) {
        LOGGER.trace("getIncomingFriendRequest({})", email);

        ApplicationUser user = userService.findApplicationUserByEmail(email);
        return friendshipRepository
            .findIncomingFriendRequestsOfUser(user)
            .stream()
            .map(ApplicationUser::getEmail)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getOutgoingFriendRequest(String email) {
        LOGGER.trace("getOutgoingFriendRequest({})", email);

        ApplicationUser user = userService.findApplicationUserByEmail(email);
        return friendshipRepository
            .findOutgoingFriendRequestsOfUser(user)
            .stream()
            .map(ApplicationUser::getEmail)
            .collect(Collectors.toList());
    }

    @Override
    public void sendFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest {
        LOGGER.trace("sendFriendRequest({}, {})", senderEmail, receiverEmail);

        if (senderEmail.equals(receiverEmail)) {
            throw new InvalidFriendRequest("You can not send a friend request to yourself!");
        }

        // get authenticated application user
        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);

        // get the receiver application user
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

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
        LOGGER.trace("acceptFriendRequest({}, {})", senderEmail, receiverEmail);

        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        if (!friendshipRepository.acceptFriendRequest(sender, receiver)) {
            throw new InvalidFriendRequest("There was no pending friend request from this sender!");
        }
    }

    @Override
    public void rejectFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest {
        LOGGER.trace("rejectFriendRequest({}, {})", senderEmail, receiverEmail);

        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        if (!friendshipRepository.rejectFriendRequest(sender, receiver)) {
            throw new InvalidFriendRequest("There was no pending friend request from this sender!");
        }
    }

    @Override
    public void retractFriendRequest(String senderEmail, String receiverEmail) throws InvalidFriendRequest {
        LOGGER.trace("retractFriendRequest({}, {})", senderEmail, receiverEmail);

        ApplicationUser sender = userService.findApplicationUserByEmail(senderEmail);
        ApplicationUser receiver = userService.findApplicationUserByEmail(receiverEmail);

        if (!friendshipRepository.rejectFriendRequest(sender, receiver)) {
            throw new InvalidFriendRequest("There was no pending friend request to this receiver!");
        }
    }
}
