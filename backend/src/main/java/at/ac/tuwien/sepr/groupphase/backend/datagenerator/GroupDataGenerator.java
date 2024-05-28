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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
        final Random random = new Random();


        ApplicationUser user1 = userRepository.findByEmail("luca@chippin.com");
        ApplicationUser user2 = userRepository.findByEmail("max@chippin.com");
        ApplicationUser user3 = userRepository.findByEmail("lukas@chippin.com");
        ApplicationUser user4 = userRepository.findByEmail("rafael@chippin.com");
        ApplicationUser user5 = userRepository.findByEmail("emil@chippin.com");
        ApplicationUser user6 = userRepository.findByEmail("sebastian@chippin.com");

        Set<ApplicationUser> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);

        groupRepository.save(GroupEntity.builder()
            .groupName("Chippin")
            .users(users)
            .build());

        final List<ApplicationUser> applicationUsers = userRepository.findAll();
        final String[] groupNames = {
            "Berlin Reise", "WG-Mitte", "Paris Trip", "London Ausflug", "Rom Urlaub",
            "WG-Neukölln", "Madrid Expedition", "WG-Kreuzberg", "Prag Besuch", "WG-Prenzlauer Berg",
            "Amsterdam Reise", "WG-Charlottenburg", "Lissabon Trip", "WG-Schöneberg", "Barcelona Ausflug",
            "WG-Friedrichshain", "Dublin Urlaub", "WG-Treptow", "Wien Expedition", "WG-Lichtenberg",
            "Budapest Besuch", "WG-Marzahn", "Stockholm Reise", "WG-Hellersdorf", "Kopenhagen Trip",
            "WG-Reinickendorf", "Oslo Ausflug", "WG-Spandau", "Helsinki Urlaub", "WG-Steglitz",
            "Riga Expedition", "WG-Zehlendorf", "Tallinn Besuch"
        };
        List<ApplicationUser> wgUsers = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Collections.shuffle(applicationUsers);
            Set<ApplicationUser> groupUsers = new HashSet<>();


            String groupName = groupNames[random.nextInt(groupNames.length)];
            if (groupName.contains("WG")) {
                for (ApplicationUser user : applicationUsers) {
                    if (!wgUsers.contains(user) && groupUsers.size() < 6) {
                        groupUsers.add(user);
                        wgUsers.add(user);
                    }
                }
                if (groupUsers.size() < 3) {
                    continue;
                }
            } else {
                groupUsers.addAll(applicationUsers.subList(0, 6));
            }

            GroupEntity group = GroupEntity.builder()
                .groupName(groupName)
                .users(groupUsers)
                .build();

            groupRepository.save(group);
        }

        GroupEntity pantryTestGroup1 = GroupEntity.builder()
            .groupName(groupNames[random.nextInt(groupNames.length)])
            .users(Set.of(
                userRepository.findByEmail("emil@chippin.com"),
                userRepository.findByEmail("rafael@chippin.com")
            ))
            .build();
        groupRepository.save(pantryTestGroup1);

        GroupEntity pantryTestGroup2 = GroupEntity.builder()
            .groupName(groupNames[random.nextInt(groupNames.length)])
            .users(Set.of(
                userRepository.findByEmail("emil@chippin.com"),
                userRepository.findByEmail("rafael@chippin.com")
            ))
            .build();
        groupRepository.save(pantryTestGroup2);

        GroupEntity pantryTestGroup3 = GroupEntity.builder()
            .groupName(groupNames[random.nextInt(groupNames.length)])
            .users(Set.of(
                userRepository.findByEmail("emil@chippin.com"),
                userRepository.findByEmail("rafael@chippin.com")
            ))
            .build();
        groupRepository.save(pantryTestGroup3);
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for group");
        groupRepository.deleteAll();
    }
}
