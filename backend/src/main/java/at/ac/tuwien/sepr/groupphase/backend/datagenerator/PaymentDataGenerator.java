package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
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
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class PaymentDataGenerator implements DataGenerator {
    private static final LocalDateTime fixedDateTime = LocalDateTime.of(2024, 6, 23, 13, 0);
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ExpenseRepository expenseRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;
    PaymentRepository paymentRepository;


    @Override
    @Transactional
    public void generateData() {
        LOGGER.trace("generating data for payment");
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();

        Random random = new Random();
        random.setSeed(12345);

        for (GroupEntity group : groups) {
            if (group.getGroupName().equals("PantryTestGroup1")
                || group.getGroupName().equals("PantryTestGroup2")
                || group.getGroupName().equals("PantryTestGroup3")) {
                continue;
            }
            List<ApplicationUser> usersInGroup = new ArrayList<>(group.getUsers());
            usersInGroup.sort(Comparator.comparing(ApplicationUser::getEmail));
            // special group where expenses are inserted manually to test (f.e. debt)
            if (group.getGroupName().equals("Chippin")) {

                Payment payment =
                    Payment.builder()
                        .payer(usersInGroup.get(1))
                        .receiver(usersInGroup.get(0))
                        .amount(20.0)
                        .date(fixedDateTime.minus(8, java.time.temporal.ChronoUnit.DAYS))
                        .group(group)
                        .deleted(false)
                        .archived(false)
                        .build();

                Payment paymentDeleted = Payment.builder()
                    .payer(usersInGroup.get(0))
                    .receiver(usersInGroup.get(1))
                    .amount(50.0)
                    .date(fixedDateTime.minus(10, java.time.temporal.ChronoUnit.DAYS))
                    .group(group)
                    .deleted(true)
                    .archived(false)
                    .build();

                paymentRepository.save(paymentDeleted);
                paymentRepository.save(payment);
                continue;
            }
            ApplicationUser user1 = usersInGroup.get(random.nextInt(usersInGroup.size()));
            ApplicationUser user2 = usersInGroup.get(random.nextInt(usersInGroup.size()));
            while (user1.equals(user2)) {
                user2 = usersInGroup.get(random.nextInt(usersInGroup.size()));
            }
            double amount = 0;
            while (amount <= 0) {
                amount = Math.round((random.nextDouble() * 100 + 1) * 100) / 100.0;
            }

            Payment payment = Payment.builder()
                .payer(user1)
                .receiver(user2)
                .amount(amount)
                .date(fixedDateTime)
                .group(group)
                .deleted(false)
                .archived(false)
                .build();

            paymentRepository.save(payment);
        }
    }

    @Override
    public void cleanData() {
        LOGGER.trace("cleaning data for payment");
        paymentRepository.deleteAll();
    }


}
