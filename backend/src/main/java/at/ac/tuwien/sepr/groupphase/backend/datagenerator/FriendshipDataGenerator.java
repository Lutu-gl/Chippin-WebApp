package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FriendshipDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    UserRepository userRepository;
    GroupRepository groupRepository;
    FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public void generateData() {
        LOGGER.debug("generating data for friendships");
        List<GroupEntity> groups = groupRepository.findAll();

        for (GroupEntity group : groups) {
            Set<ApplicationUser> users = group.getUsers();
            List<ApplicationUser> sortedUsers = users.stream()
                .sorted(Comparator.comparing(ApplicationUser::getEmail))
                .collect(Collectors.toList());

            for (int i = 0; i < sortedUsers.size(); i++) {
                for (int j = i + 1; j < sortedUsers.size(); j++) {
                    ApplicationUser user = sortedUsers.get(i);
                    ApplicationUser user2 = sortedUsers.get(j);

                    Friendship friendship = Friendship.builder()
                        .sender(user)
                        .receiver(user2)
                        .sentAt(LocalDateTime.now())
                        .friendshipStatus(FriendshipStatus.ACCEPTED)
                        .build();

                    friendshipRepository.save(friendship);
                }
            }
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for friendships");
        friendshipRepository.deleteAll();
    }
}