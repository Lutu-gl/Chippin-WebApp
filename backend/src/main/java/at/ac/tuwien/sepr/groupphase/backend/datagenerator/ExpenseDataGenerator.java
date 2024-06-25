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

    private static final LocalDateTime fixedDateTime = LocalDateTime.of(2024, 6, 23, 13, 0);
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static Map<String, Category> expenseCategories;

    @Override
    @Transactional
    public void generateData() {
        LOGGER.trace("generating data for expense");

        setUpHashMap();

        List<GroupEntity> groups = groupRepository.findAll();
        Random random = new Random(12);

        groups.sort(Comparator.comparing(GroupEntity::getGroupName));
        Category[] categories = Category.values();

        for (GroupEntity group : groups) {
            List<ApplicationUser> usersInGroup = new ArrayList<>(group.getUsers());
            usersInGroup.sort(Comparator.comparing(ApplicationUser::getEmail));

            if (usersInGroup.size() < 3) {
                continue;
            }

            // Chippin group
            if (group.getGroupName().equals("Chippin")) {
                generateDataForChippinExtended(group, usersInGroup, random, categories);
            } else {
                generateDataForGroupsExtended(group, usersInGroup, random, categories);
            }
        }
    }

    private void setUpHashMap() {
        this.expenseCategories = new HashMap<>();

        // Food
        expenseCategories.put("Larcher Restaurant", Category.Food);
        expenseCategories.put("BurgerNKings Snack Bar", Category.Food);
        expenseCategories.put("Kebab House", Category.Food);
        expenseCategories.put("Pizzeria Restaurant", Category.Food);
        expenseCategories.put("McDonalds", Category.Food);
        expenseCategories.put("Subway", Category.Food);
        expenseCategories.put("KFC", Category.Food);
        expenseCategories.put("Burger King", Category.Food);
        expenseCategories.put("Pizza Hut", Category.Food);
        expenseCategories.put("Dining Out", Category.Food);
        expenseCategories.put("Supermarket", Category.Food);
        expenseCategories.put("Bakery", Category.Food);
        expenseCategories.put("Coffee Shop", Category.Food);
        expenseCategories.put("Tea House", Category.Food);
        expenseCategories.put("Vegan Restaurant", Category.Food);
        expenseCategories.put("Butcher Shop", Category.Food);
        expenseCategories.put("Dairy Farm", Category.Food);
        expenseCategories.put("Organic Market", Category.Food);

        // Entertainment
        expenseCategories.put("Cinema", Category.Entertainment);
        expenseCategories.put("Art Gallery", Category.Entertainment);
        expenseCategories.put("Museum Ticket", Category.Entertainment);
        expenseCategories.put("Concert Ticket", Category.Entertainment);
        expenseCategories.put("Theater Ticket", Category.Entertainment);
        expenseCategories.put("Opera Ticket", Category.Entertainment);
        expenseCategories.put("Amusement Park", Category.Entertainment);
        expenseCategories.put("Zoo Entry", Category.Entertainment);
        expenseCategories.put("Aquarium Visit", Category.Entertainment);
        expenseCategories.put("Mountain Cable Car", Category.Entertainment);
        expenseCategories.put("Ski Pass", Category.Entertainment);
        expenseCategories.put("Snowboard Rental", Category.Entertainment);
        expenseCategories.put("Surfing Lessons", Category.Entertainment);
        expenseCategories.put("Diving Gear Rental", Category.Entertainment);

        // Health
        expenseCategories.put("Pharmacy", Category.Health);
        expenseCategories.put("Doctor Visit", Category.Health);
        expenseCategories.put("Hospital", Category.Health);
        expenseCategories.put("Yoga Studio", Category.Health);
        expenseCategories.put("Gym", Category.Health);
        expenseCategories.put("Massage Therapy", Category.Health);
        expenseCategories.put("Hair Salon", Category.Health);
        expenseCategories.put("Nail Salon", Category.Health);
        expenseCategories.put("Barbershop", Category.Health);

        // Travel
        expenseCategories.put("To Engel Hotel", Category.Travel);
        expenseCategories.put("Gas Station", Category.Travel);
        expenseCategories.put("Parking Fee", Category.Travel);
        expenseCategories.put("Toll Fee", Category.Travel);
        expenseCategories.put("Ferry Ticket", Category.Travel);
        expenseCategories.put("Hotel", Category.Travel);
        expenseCategories.put("Train Ticket", Category.Travel);
        expenseCategories.put("Flight Ticket", Category.Travel);
        expenseCategories.put("Bus Ride", Category.Travel);
        expenseCategories.put("Taxi", Category.Travel);
        expenseCategories.put("Beach Resort", Category.Travel);

        // Shopping
        expenseCategories.put("Shopping Mall", Category.Shopping);
        expenseCategories.put("Books", Category.Shopping);
        expenseCategories.put("Clothing", Category.Shopping);
        expenseCategories.put("Electronics", Category.Shopping);
        expenseCategories.put("Gifts", Category.Shopping);
        expenseCategories.put("Hardware Store", Category.Shopping);
        expenseCategories.put("Craft Materials", Category.Shopping);
        expenseCategories.put("Garden Supplies", Category.Shopping);
        expenseCategories.put("Office Supplies", Category.Shopping);
        expenseCategories.put("Laptop Repair", Category.Shopping);
        expenseCategories.put("Smartphone Accessories", Category.Shopping);
        expenseCategories.put("Software License", Category.Shopping);
        expenseCategories.put("Cloud Storage Fee", Category.Shopping);
        expenseCategories.put("VPN Subscription", Category.Shopping);
        expenseCategories.put("Streaming Service", Category.Shopping);
        expenseCategories.put("Home Renovation", Category.Shopping);
        expenseCategories.put("Furniture Store", Category.Shopping);
        expenseCategories.put("Electrical Store", Category.Shopping);
        expenseCategories.put("Toy Store", Category.Shopping);
        expenseCategories.put("Pet Grooming", Category.Shopping);

        // Other
        expenseCategories.put("Rent", Category.Other);
        expenseCategories.put("Electricity Bill", Category.Other);
        expenseCategories.put("Mobile Phone Bill", Category.Other);
        expenseCategories.put("Internet Bill", Category.Other);
        expenseCategories.put("Car Repair", Category.Other);
        expenseCategories.put("Book Club Membership", Category.Other);
        expenseCategories.put("Bike Repair", Category.Other);
        expenseCategories.put("Vehicle Inspection", Category.Other);
        expenseCategories.put("Pet Supplies", Category.Other);
    }

    private void generateDataForGroupsExtended(GroupEntity group, List<ApplicationUser> usersInGroup, Random random, Category[] categories) {
        LocalDateTime startDate = fixedDateTime.minusMonths(3);
        LocalDateTime endDate = fixedDateTime.minusDays(2);

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

            double doubleAmountExpense = 10 + random.nextInt(100);

            double part1 = Math.round(doubleAmountExpense * random.nextDouble() * 100.0) / 100.0;
            double part2 = Math.round((doubleAmountExpense - part1) * random.nextDouble() * 100.0) / 100.0;
            double part3 = Math.round((doubleAmountExpense - part1 - part2) * 100.0) / 100.0;

            Map<ApplicationUser, Double> participants = new HashMap<>();
            participants.put(participantsList.get(0), part1 / doubleAmountExpense);
            participants.put(participantsList.get(1), part2 / doubleAmountExpense);
            participants.put(participantsList.get(2), part3 / doubleAmountExpense);


            String name = expenseCategories.keySet().toArray()[random.nextInt(expenseCategories.size())].toString();
            Category category = expenseCategories.get(name);

            LocalDateTime expenseTime = startDate.plusHours(random.nextInt(2));
            LocalDateTime expenseTimeFinal = expenseTime.plusMinutes(random.nextInt(30));

            Expense expense = Expense.builder()
                .name(name)
                .category(category)
                .amount(doubleAmountExpense)
                .date(expenseTimeFinal)
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


    private void generateDataForChippinExtended(GroupEntity group, List<ApplicationUser> usersInGroup, Random random, Category[] categories) {
        if (group.getGroupName().equals("Chippin")) {
            LocalDateTime startDate = fixedDateTime.minusMonths(12);
            LocalDateTime endDate = fixedDateTime.minusDays(2);


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
                double doubleAmountExpense = 10 + random.nextInt(70);

                double part1 = Math.round(doubleAmountExpense * random.nextDouble() * 100.0) / 100.0;
                double part2 = Math.round((doubleAmountExpense - part1) * random.nextDouble() * 100.0) / 100.0;
                double part3 = Math.round((doubleAmountExpense - part1 - part2) * 100.0) / 100.0;


                Map<ApplicationUser, Double> participants = new HashMap<>();

                participants.put(participantsList.get(0), part1 / doubleAmountExpense);
                participants.put(participantsList.get(1), part2 / doubleAmountExpense);
                participants.put(participantsList.get(2), part3 / doubleAmountExpense);


                String name = expenseCategories.keySet().toArray()[random.nextInt(expenseCategories.size())].toString();
                Category category = expenseCategories.get(name);

                LocalDateTime expenseTime = startDate.plusHours(random.nextInt(2));
                LocalDateTime expenseTimeFinal = expenseTime.plusMinutes(random.nextInt(30));

                Expense expense = Expense.builder()
                    .name(name)
                    .category(category)
                    .amount(doubleAmountExpense)
                    .date(expenseTimeFinal)
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


    @Override
    public void cleanData() {
        LOGGER.trace("cleaning data for expense");
        expenseRepository.deleteAll();
    }


}
