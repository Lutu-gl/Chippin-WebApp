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
import java.util.List;
import java.util.Set;

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
            for (int i = 0; i < users.size(); i++) {
                ApplicationUser user = (ApplicationUser) users.toArray()[i];
                ApplicationUser user2 = (ApplicationUser) users.toArray()[(i + 1) % users.size()];

                Friendship friendship = Friendship.builder()
                    .sender(user)
                    .receiver(user2)
                    .sentAt(LocalDateTime.now())
                    .friendshipStatus(FriendshipStatus.ACCEPTED)
                    .build();

                Friendship friendship2 = Friendship.builder()
                    .sender(user2)
                    .receiver(user)
                    .sentAt(LocalDateTime.now())
                    .friendshipStatus(FriendshipStatus.ACCEPTED)
                    .build();

                friendshipRepository.save(friendship);
                friendshipRepository.save(friendship2);
            }
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for friendships");
        friendshipRepository.deleteAll();
    }
}