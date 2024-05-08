package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

@Profile("generateData")
@Component
public class UserAndGroupDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_USERS_TO_GENERATE = 10;

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public UserAndGroupDataGenerator(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @PostConstruct
    private void generateData() {
        if (groupRepository.findAll().size() > 0 || userRepository.findAll().size() > 0) {
            LOGGER.debug("Groups already generated");
            return;
        }
        LOGGER.debug("generating {} message entries with 2 groups", NUMBER_OF_USERS_TO_GENERATE);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupName("WG-TUW");
        group1 = groupRepository.save(group1);

        GroupEntity group2 = new GroupEntity();
        group2.setGroupName("WG-EMPTY");
        group2 = groupRepository.save(group2);

        Set<GroupEntity> groups = new HashSet<>();
        groups.add(group1);
        //groups.add(group2);
        ApplicationUser user1 = ApplicationUser.builder()
            .email("alice@example.com")
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .admin(true)
            .build();

        ApplicationUser user2 = ApplicationUser.builder()
            .email("bob@example.com")
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .admin(false)
            .build();

        user1.setGroups(groups);
        user2.setGroups(groups);

        userRepository.save(user1);
        userRepository.save(user2);

        for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE - 2; i++) {
            ApplicationUser user = ApplicationUser.builder()
                .email((i < 5 ? "user" : "admin") + (i == 5 || i == 0 ? "" : i) + "@email.com")
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")   // precomputed> password is the value
                .admin(i >= 5)
                .build();
            userRepository.save(user);
            LOGGER.debug("Saved user {}", user);
        }
    }
}
