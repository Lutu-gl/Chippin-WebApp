package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class GroupDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    UserRepository userRepository;
    GroupRepository groupRepository;

    @Override
    public void generateData() {
        LOGGER.debug("generating data for group");
        List<ApplicationUser> applicationUsers = userRepository.findAll();

        for (int i = 0; i < 20; i++) {
            Set<ApplicationUser> groupUsers = new HashSet<>(applicationUsers.subList(i % applicationUsers.size(), (i % applicationUsers.size()) + 6));

            GroupEntity group = GroupEntity.builder()
                .groupName("groupExample" + i)
                .users(groupUsers)
                .build();

            groupRepository.save(group);
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for group");
        groupRepository.deleteAll();
    }
}
