package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exceptionhandler.FatalException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.GroupValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupValidator validator;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final GroupMapper groupMapper;


    @Override
    @Transactional
    public GroupCreateDto create(GroupCreateDto groupCreateDto, String ownerEmail) throws ValidationException, ConflictException {
        LOGGER.trace("create({}, {})", groupCreateDto, ownerEmail);

        validator.validateForCreation(groupCreateDto, ownerEmail);

        GroupEntity groupEntity = groupMapper.groupCreateDtoToGroupEntity(groupCreateDto);
        if (groupCreateDto.getMembers() != null) {
            Set<ApplicationUser> users = groupCreateDto.getMembers().stream()
                .map(userRepository::findByEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            groupEntity.setUsers(users);
        }

        GroupEntity savedGroup = groupRepository.save(groupEntity);
        makeFriendsWithEveryMember(savedGroup);

        if (!validator.validateFriendsWithEveryone(savedGroup)) {
            throw new FatalException("Not all users are friends with each other after group creation");
        }

        return groupMapper.groupEntityToGroupCreateDto(savedGroup);
    }

    @Override
    @Transactional
    public GroupCreateDto update(GroupCreateDto groupCreateDto, String ownerEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.trace("update({}, {})", groupCreateDto, ownerEmail);

        validator.validateForUpdate(groupCreateDto, ownerEmail);

        GroupEntity groupEntity = groupMapper.groupCreateDtoToGroupEntity(groupCreateDto);
        if (groupCreateDto.getMembers() != null) {
            Set<ApplicationUser> users = groupCreateDto.getMembers().stream()
                .map(userRepository::findByEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            groupEntity.setUsers(users);
        }

        GroupEntity savedGroup = groupRepository.save(groupEntity);
        makeFriendsWithEveryMember(savedGroup);

        if (!validator.validateFriendsWithEveryone(savedGroup)) {
            throw new FatalException("Not all users are friends with each other after group creation");
        }
        
        return groupMapper.groupEntityToGroupCreateDto(savedGroup);
    }

    @Override
    @Transactional
    public GroupCreateDto getById(long id) throws NotFoundException {
        LOGGER.trace("getById({})", id);

        GroupEntity groupEntity = groupRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No group found with this id"));

        return groupMapper.groupEntityToGroupCreateDto(groupEntity);
    }

    private void makeFriendsWithEveryMember(GroupEntity group) {
        for (int i = 0; i < group.getUsers().size(); i++) {
            for (int j = i + 1; j < group.getUsers().size(); j++) {
                ApplicationUser user = (ApplicationUser) group.getUsers().toArray()[i];
                ApplicationUser user2 = (ApplicationUser) group.getUsers().toArray()[j];

                if (!friendshipRepository.areFriends(user, user2)) {
                    Friendship friendship = Friendship.builder()
                        .sender(user)
                        .receiver(user2)
                        .friendshipStatus(FriendshipStatus.ACCEPTED)
                        .sentAt(LocalDateTime.now())
                        .build();

                    friendshipRepository.save(friendship);
                }
            }
        }
    }
}
