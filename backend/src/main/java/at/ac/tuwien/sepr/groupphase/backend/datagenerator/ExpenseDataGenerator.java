package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.github.javafaker.Faker;
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
import java.util.Locale;
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

    private static String[] expenseNames = {
        "To Engel Hotel", "Larcher Restaurant", "BurgerNKings Snack Bar", "Kebab House",
        "Pizzeria Restaurant", "McDonalds", "Subway", "KFC", "Burger King", "Pizza Hut",
        "Gas Station", "Cinema", "Shopping Mall", "Rent", "Electricity Bill", "Mobile Phone Bill",
        "Internet Bill", "Gym", "Insurance", "Car Repair", "Books",
        "Clothing", "Electronics", "Gifts", "Travel", "Hotel", "Train Ticket", "Flight Ticket",
        "Bus Ride", "Taxi", "Dining Out", "Supermarket", "Pharmacy", "Doctor Visit", "Hospital",
        "Yoga Studio", "Pet Supplies", "Hardware Store", "Craft Materials", "Garden Supplies",
        "Office Supplies", "Bakery", "Coffee Shop", "Tea House", "Vegan Restaurant",
        "Butcher Shop", "Dairy Farm", "Organic Market", "Art Gallery", "Museum Ticket",
        "Concert Ticket", "Theater Ticket", "Opera Ticket", "Parking Fee", "Toll Fee",
        "Ferry Ticket", "Amusement Park", "Zoo Entry", "Aquarium Visit", "Mountain Cable Car",
        "Ski Pass", "Snowboard Rental", "Surfing Lessons", "Diving Gear Rental", "Beach Resort",
        "Spa Day", "Massage Therapy", "Hair Salon", "Nail Salon", "Barbershop",
        "Laptop Repair", "Smartphone Accessories", "Software License", "Cloud Storage Fee", "VPN Subscription",
        "Streaming Service", "Book Club Membership", "Fitness Membership", "Bike Repair", "Vehicle Inspection",
        "Home Renovation", "Furniture Store", "Electrical Store", "Toy Store", "Pet Grooming"
    };


    @Override
    @Transactional
    public void generateData() {
        LOGGER.debug("generating data for expense");
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();
        Random random = new Random();
        random.setSeed(12345);
        Faker faker = new Faker(Locale.getDefault(), random);


        Category[] categories = Category.values();

        for (GroupEntity group : groups) {
            List<ApplicationUser> usersInGroup = new ArrayList<>(group.getUsers());
            usersInGroup.sort(Comparator.comparing(ApplicationUser::getEmail));

            if (usersInGroup.size() < 3) {
                continue;
            }
            if (group.getGroupName().equals("Chippin")) {
                //generateDataForChippin(group, usersInGroup, random);
                generateDataForChippinExtended(group, usersInGroup, random, categories);
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

                int amountExpense = 10 + random.nextInt(291);
                double doubleAmountExpense = (double) amountExpense;

                double part1 = Math.round(doubleAmountExpense * random.nextDouble() * 100.0) / 100.0;
                double part2 = Math.round((doubleAmountExpense - part1) * random.nextDouble() * 100.0) / 100.0;
                double part3 = Math.round((doubleAmountExpense - part1 - part2) * 100.0) / 100.0;

                // Generate random splits that sum to 1

                Map<ApplicationUser, Double> participants = new HashMap<>();

                participants.put(participantsList.get(0), part1 / doubleAmountExpense);
                participants.put(participantsList.get(1), part2 / doubleAmountExpense);
                participants.put(participantsList.get(2), part3 / doubleAmountExpense);


                Expense expense = Expense.builder()
                    .name(expenseNames[random.nextInt(expenseNames.length)])
                    .category(categories[random.nextInt(categories.length)])
                    .amount(doubleAmountExpense) // random amount between 100.0 and 300.0, rounded to 2 decimal places
                    .date(LocalDateTime.now().minus(random.nextInt(10), ChronoUnit.DAYS)) // random date within last 10 days
                    .payer(payer)
                    .group(group)
                    .participants(participants)
                    .deleted(false)
                    .archived(false)
                    .build();

                expenseRepository.save(expense);
            }
        }
    }

    private void generateDataForChippinExtended(GroupEntity group, List<ApplicationUser> usersInGroup, Random random, Category[] categories) {
        if (group.getGroupName().equals("Chippin")) {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
            LocalDateTime endDate = LocalDateTime.now().minusDays(10);


            random.setSeed(12345);
            while (startDate.isBefore(endDate)) {
                ApplicationUser payer = usersInGroup.get(random.nextInt(usersInGroup.size()));

                Set<ApplicationUser> uniqueParticipants = new HashSet<>();
                while (uniqueParticipants.size() < 3) {
                    uniqueParticipants.add(usersInGroup.get(random.nextInt(usersInGroup.size())));
                }

                List<ApplicationUser> participantsList = new ArrayList<>(uniqueParticipants);
                participantsList.sort(Comparator.comparing(ApplicationUser::getEmail));
                if (!participantsList.contains(payer)) {
                    participantsList.set(random.nextInt(3), payer);
                }

                int amountExpense = 10 + random.nextInt(291);
                double doubleAmountExpense = (double) amountExpense;

                double part1 = Math.round(doubleAmountExpense * random.nextDouble() * 100.0) / 100.0;
                double part2 = Math.round((doubleAmountExpense - part1) * random.nextDouble() * 100.0) / 100.0;
                double part3 = Math.round((doubleAmountExpense - part1 - part2) * 100.0) / 100.0;


                Map<ApplicationUser, Double> participants = new HashMap<>();

                participants.put(participantsList.get(0), part1 / doubleAmountExpense);
                participants.put(participantsList.get(1), part2 / doubleAmountExpense);
                participants.put(participantsList.get(2), part3 / doubleAmountExpense);


                Category category = categories[random.nextInt(categories.length)];
                String name = expenseNames[random.nextInt(expenseNames.length)];

                Expense expense = Expense.builder()
                    .name(name)
                    .category(category)
                    .amount(doubleAmountExpense)
                    .date(startDate)
                    .payer(payer)
                    .group(group)
                    .participants(participants)
                    .deleted(false)
                    .archived(false)
                    .build();

                expenseRepository.save(expense);

                startDate = startDate.plusDays(2);
            }
        }

    }

    private void generateDataForChippin(GroupEntity group, List<ApplicationUser> usersInGroup, Random random) {
        // spezial group where expenses are inserted manually to test (f.e. debt)
        if (group.getGroupName().equals("Chippin")) {
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
                .name(expenseNames[random.nextInt(expenseNames.length)])
                .category(Category.Food)
                .amount(100.0d)
                .date(LocalDateTime.now())
                .payer(usersInGroup.get(0))
                .group(group)
                .participants(participants)
                .deleted(false)
                .archived(false)
                .build();

            Expense expense2 = Expense.builder()
                .name(expenseNames[random.nextInt(expenseNames.length)])
                .category(Category.Food)
                .amount(100.0d)
                .date(LocalDateTime.now())
                .group(group)
                .payer(usersInGroup.get(0))
                .participants(participants2)
                .deleted(false)
                .archived(false)
                .build();

            Expense expense3 = Expense.builder()
                .name(expenseNames[random.nextInt(expenseNames.length)])
                .category(Category.Food)
                .amount(100.0d)
                .date(LocalDateTime.now())
                .group(group)
                .payer(usersInGroup.get(1))
                .participants(participants3)
                .deleted(false)
                .archived(false)
                .build();
            expenseRepository.save(expense);
            expenseRepository.save(expense2);
            expenseRepository.save(expense3);
        }
    }


    private double[] generateRandomSplits() {
        Random random = new Random();
        random.setSeed(12345);
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();
        double total = r1 + r2 + 1.0;

        double split1 = r1 / total;
        double split2 = r2 / total;
        double split3 = 1.0 / total;

        return new double[]{split1, split2, split3};
    }

    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for expense");
        expenseRepository.deleteAll();
    }


}
