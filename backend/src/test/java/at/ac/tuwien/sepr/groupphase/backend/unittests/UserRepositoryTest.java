package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")

public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }
    @Test
    public void testFindUserById() {
        ApplicationUser user = createUserWithGroups();

        Optional<ApplicationUser> foundUser = userRepository.findById(user.getId());
        assertEquals(user, foundUser.orElse(null));
    }
    @Test
    public void testFindGroupsByUserEmail() {
        ApplicationUser user = createUserWithGroups();

        System.out.println(user.getId());
        System.out.println(user.getEmail());
        System.out.println(user.getGroups().size());
        for (GroupEntity group : user.getGroups()) {
            System.out.println(">>" + group);
        }

        Set<GroupEntity> foundGroups = userRepository.findGroupsByUserEmail(user.getEmail());
        System.out.println(foundGroups);


        assertAll(
            () -> assertNotNull(foundGroups, "Groups should not be null"),
            () -> assertFalse(foundGroups.isEmpty(), "Groups should not be empty"),
            () -> assertTrue(foundGroups.containsAll(user.getGroups()), "Groups should contain all assigned groups")
        );
    }

    private ApplicationUser createUserWithGroups() {
        GroupEntity group1 = new GroupEntity();
        group1.setGroupName("Developers");
        group1 = groupRepository.save(group1);

        GroupEntity group2 = new GroupEntity();
        group2.setGroupName("Managers");
        group2 = groupRepository.save(group2);

        ApplicationUser user = ApplicationUser.builder()
            .email("alice@test.com")
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .admin(true)
            .build();

        Set<GroupEntity> groups = new HashSet<>();
        groups.add(group1);
        groups.add(group2);
        user.setGroups(groups);

        userRepository.save(user);
        return user;
    }
}