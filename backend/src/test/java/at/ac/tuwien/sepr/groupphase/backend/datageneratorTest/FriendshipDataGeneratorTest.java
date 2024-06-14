package at.ac.tuwien.sepr.groupphase.backend.datageneratorTest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGenerator;
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
public class FriendshipDataGeneratorTest implements DataGenerator {
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
                for (int j = i + 1; j < users.size(); j++) {
                    ApplicationUser user = (ApplicationUser) users.toArray()[i];
                    ApplicationUser user2 = (ApplicationUser) users.toArray()[j];

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

        ApplicationUser user1 = userRepository.findByEmail("importUser1@example.com");
        for (int i = 1; i < 6; i++) {
            friendshipRepository.save(Friendship.builder()
                .sender(user1)
                .receiver(userRepository.findByEmail("importUser" + (i + 1) + "@example.com"))
                .friendshipStatus(FriendshipStatus.ACCEPTED)
                .sentAt(LocalDateTime.now())
                .build());
        }

    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for friendships");
        friendshipRepository.deleteAll();
    }
}