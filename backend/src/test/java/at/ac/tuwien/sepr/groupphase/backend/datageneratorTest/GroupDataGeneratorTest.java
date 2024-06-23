package at.ac.tuwien.sepr.groupphase.backend.datageneratorTest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGenerator;
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
public class GroupDataGeneratorTest implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    UserRepository userRepository;
    GroupRepository groupRepository;

    @Override
    public void generateData() {
        LOGGER.debug("generating data for group");
        List<ApplicationUser> applicationUsers = userRepository.findAll();

        for (int i = 0; i < 5; i++) {
            Set<ApplicationUser> groupUsers = new HashSet<>(applicationUsers.subList(i % applicationUsers.size(), (i % applicationUsers.size()) + 6));

            GroupEntity group = GroupEntity.builder()
                .groupName("groupExample" + i)
                .users(groupUsers)
                .build();

            groupRepository.save(group);
        }

        GroupEntity pantryTestGroup1 = GroupEntity.builder()
            .groupName("PantryTestGroup1")
            .users(Set.of(
                userRepository.findByEmail("user1@example.com"),
                userRepository.findByEmail("user2@example.com")
            ))
            .build();
        groupRepository.save(pantryTestGroup1);

        GroupEntity pantryTestGroup2 = GroupEntity.builder()
            .groupName("PantryTestGroup2")
            .users(Set.of(
                userRepository.findByEmail("user1@example.com"),
                userRepository.findByEmail("user2@example.com")
            ))
            .build();
        groupRepository.save(pantryTestGroup2);

        GroupEntity pantryTestGroup3 = GroupEntity.builder()
            .groupName("PantryTestGroup3")
            .users(Set.of(
                userRepository.findByEmail("user1@example.com"),
                userRepository.findByEmail("user2@example.com")
            ))
            .build();
        groupRepository.save(pantryTestGroup3);

        GroupEntity importTestGroup = GroupEntity.builder()
            .groupName("ImportTestGroup")
            .users(Set.of(
                userRepository.findByEmail("importUser1@example.com"),
                userRepository.findByEmail("importUser2@example.com"),
                userRepository.findByEmail("importUser3@example.com"),
                userRepository.findByEmail("importUser4@example.com"),
                userRepository.findByEmail("importUser5@example.com"),
                userRepository.findByEmail("importUser6@example.com")
            ))
            .build();
        groupRepository.save(importTestGroup);

        GroupEntity exportTestGroup = GroupEntity.builder()
            .groupName("ExportTestGroup")
            .users(Set.of(
                userRepository.findByEmail("importUser1@example.com"),
                userRepository.findByEmail("importUser2@example.com"),
                userRepository.findByEmail("importUser3@example.com"),
                userRepository.findByEmail("importUser4@example.com"),
                userRepository.findByEmail("importUser5@example.com"),
                userRepository.findByEmail("importUser6@example.com")
            ))
            .build();
        groupRepository.save(exportTestGroup);
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for group");
        groupRepository.deleteAll();
    }
}
