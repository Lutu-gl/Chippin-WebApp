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
import java.util.Arrays;
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
        LOGGER.trace("generating data for group");
        final Random random = new Random();
        random.setSeed(13);

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
        String[] groupNames = {
            "Berlin Trip", "WG Mitte", "Paris Trip", "London Excursion", "Rome Vacation",
            "WG Neukölln", "Madrid Expedition", "WG Kreuzberg", "Prague Visit", "WG Prenzlauer Berg",
            "Amsterdam Trip", "WG Charlottenburg", "Lisbon Trip", "WG Schoeneberg", "Barcelona Excursion",
            "WG Friedrichshain", "Dublin Vacation", "WG Treptow", "Vienna Expedition", "WG Lichtenberg",
            "Budapest Visit", "WG Marzahn", "Stockholm Trip", "WG Hellersdorf", "Copenhagen Trip",
            "WG Reinickendorf", "Oslo Excursion", "WG Spandau", "Helsinki Vacation", "WG Steglitz",
            "Riga Expedition", "WG Zehlendorf", "Tallinn Visit",
            "Brussels Adventure", "WG Wedding", "Munich Trip", "WG Moabit", "Glasgow Excursion",
            "WG Tempelhof", "Zurich Vacation", "WG Kreuzberg South", "Antwerp Expedition", "WG Friedrichshagen",
            "Geneva Trip", "WG Weissensee", "Athens Excursion", "WG Kopenick", "Malmo Visit",
            "WG Schonefeld", "Santorini Vacation", "WG Tiergarten", "Valencia Trip", "WG Gatow",
            "Milan Excursion", "WG Hohenschonhausen", "Porto Vacation", "WG Dahlem", "Florence Trip",
            "WG Pankow", "Venice Excursion", "WG Koepenick", "Naples Visit", "WG Tempelhof South",
            "Sofia Trip", "WG Neukoelln North", "Istanbul Vacation", "WG Grunewald", "Seville Excursion",
            "WG Moabit East", "Bratislava Visit", "WG Kreuzberg East", "Luxembourg Adventure", "WG Marzahn West",
            "Liverpool Journey", "WG Reinickendorf East", "Hamburg Expedition", "WG Spandau West", "Tokyo Trip",
            "WG Steglitz North", "Beijing Adventure", "WG Zehlendorf South", "Sydney Excursion", "WG Wedding North",
            "Moscow Visit", "WG Charlottenburg South", "Delhi Expedition", "WG Schoeneberg West", "Cairo Trip",
            "WG Friedrichshain East", "Toronto Vacation", "WG Treptow North", "Seoul Excursion", "WG Lichtenberg South",
            "Mexico City Visit", "WG Marzahn East", "New York Trip", "WG Hellersdorf West", "Sao Paulo Adventure",
            "WG Reinickendorf West", "Los Angeles Vacation", "WG Spandau North", "Chicago Trip", "WG Steglitz East"
        };


        List<ApplicationUser> wgUsers = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Collections.shuffle(applicationUsers, random);
            Set<ApplicationUser> groupUsers = new HashSet<>();

            // retrive random group name from groupNames and remove name from the list
            String groupName = groupNames[random.nextInt(groupNames.length)];
            groupNames = Arrays.stream(groupNames)
                .filter(name -> !name.equals(groupName))
                .toArray(String[]::new);

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
    }

    @Override
    public void cleanData() {
        LOGGER.trace("cleaning data for group");
        groupRepository.deleteAll();
    }
}
