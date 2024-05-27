package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Component
@AllArgsConstructor
public class ExpenseDataGenerator implements DataGenerator {
    ExpenseRepository expenseRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    @Transactional
    public void generateData() {
        LOGGER.debug("generating data for expense");
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();

        Random random = new Random();
        String[] expenseNames = {
            "Zum Engel Hotel", "Restaurante Larcher", "BurgerNKings Imbiss", "Kebab Haus",
            "Pizzeria Ristorante", "McDonalds", "Subway", "KFC", "Burger King", "Pizza Hut"
        };
        Category[] categories = {
            Category.Food, Category.Travel, Category.Other, Category.Transportation, Category.Entertainment
        };

        for (GroupEntity group : groups) {
            if (group.getGroupName().equals("PantryTestGroup1") ||
                group.getGroupName().equals("PantryTestGroup2") ||
                group.getGroupName().equals("PantryTestGroup3")){
                continue;
            }
            List<ApplicationUser> usersInGroup = new ArrayList<>(group.getUsers());
            usersInGroup.sort(Comparator.comparing(ApplicationUser::getEmail));
            // spezial group where expenses are inserted manually to test (f.e. debt)
            if (group.getGroupName().equals("groupExample0")) {
                // example for debt testing:
                // user1 owes user0 50
                // user2 owes user0 30
                // user2 owes user1 80

                Map<ApplicationUser, Double> participants = new HashMap<>();
                participants.put(usersInGroup.get(0), 0.6);
                participants.put(usersInGroup.get(1), 0.4); // user 1 owes user 0 40

                Map<ApplicationUser, Double> participants2 = new HashMap<>();
                participants2.put(usersInGroup.get(0), 0.5);
                participants2.put(usersInGroup.get(1), 0.2);
                participants2.put(usersInGroup.get(2), 0.3); // user 1 owes user 0 60 and user 2 owes user 0 30

                Map<ApplicationUser, Double> participants3 = new HashMap<>();
                participants3.put(usersInGroup.get(0), 0.1);
                participants3.put(usersInGroup.get(1), 0.1);
                participants3.put(usersInGroup.get(2), 0.8); // user1

                Expense expense = Expense.builder()
                    .name("testExpense0")
                    .category(Category.Food)
                    .amount(100.0d)
                    .date(LocalDateTime.now())
                    .payer(usersInGroup.get(0))
                    .group(group)
                    .participants(participants)
                    .deleted(false)
                    .build();

                Expense expense2 = Expense.builder()
                    .name("testExpense1")
                    .category(Category.Food)
                    .amount(100.0d)
                    .date(LocalDateTime.now())
                    .group(group)
                    .payer(usersInGroup.get(0))
                    .participants(participants2)
                    .deleted(false)
                    .build();

                Expense expense3 = Expense.builder()
                    .name("testExpense2")
                    .category(Category.Food)
                    .amount(100.0d)
                    .date(LocalDateTime.now())
                    .group(group)
                    .payer(usersInGroup.get(1))
                    .participants(participants3)
                    .deleted(false)
                    .build();
                expenseRepository.save(expense);
                expenseRepository.save(expense2);
                expenseRepository.save(expense3);
                continue;
            }

            for (int i = 0; i < 3; i++) {
                ApplicationUser payer = usersInGroup.get(random.nextInt(usersInGroup.size()));

                Set<ApplicationUser> uniqueParticipants = new HashSet<>();
                while (uniqueParticipants.size() < 3) {
                    uniqueParticipants.add(usersInGroup.get(random.nextInt(usersInGroup.size())));
                }

                List<ApplicationUser> participantsList = new ArrayList<>(uniqueParticipants);
                if (!participantsList.contains(payer)) {
                    participantsList.set(random.nextInt(3), payer);
                }

                // Generate random splits that sum to 1
                double[] splits = generateRandomSplits();

                Map<ApplicationUser, Double> participants = new HashMap<>();
                for (int j = 0; j < 3; j++) {
                    participants.put(participantsList.get(j), splits[j]);
                }

                Expense expense = Expense.builder()
                    .name(expenseNames[random.nextInt(expenseNames.length)] + i)
                    .category(categories[random.nextInt(categories.length)])
                    .amount(Math.round((100.0 + (200.0 * random.nextDouble())) * 100.0) / 100.0) // random amount between 100.0 and 300.0, rounded to 2 decimal places
                    .date(LocalDateTime.now().minus(random.nextInt(10), ChronoUnit.DAYS)) // random date within last 10 days
                    .payer(payer)
                    .group(group)
                    .participants(participants)
                    .build();

                expenseRepository.save(expense);
            }
        }
    }

    private double[] generateRandomSplits() {
        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();
        double total = r1 + r2 + 1.0;

        double split1 = r1 / total;
        double split2 = r2 / total;
        double split3 = 1.0 / total;

        return new double[]{split1, split2, split3};
    }


    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for expense");
        expenseRepository.deleteAll();
    }


}
