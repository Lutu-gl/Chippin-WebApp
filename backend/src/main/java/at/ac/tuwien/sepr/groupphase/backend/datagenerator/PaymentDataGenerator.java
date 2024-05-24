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
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ExpenseRepository expenseRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;
    PaymentRepository paymentRepository;


    @Override
    @Transactional
    public void generateData() {
        LOGGER.debug("generating data for payment");
        List<ApplicationUser> users = userRepository.findAll();
        List<GroupEntity> groups = groupRepository.findAll();

        Random random = new Random();

        for (GroupEntity group : groups) {
            List<ApplicationUser> usersInGroup = new ArrayList<>(group.getUsers());
            usersInGroup.sort(Comparator.comparing(ApplicationUser::getEmail));
            // spezial group where expenses are inserted manually to test (f.e. debt)
            if (group.getGroupName().equals("groupExample0")) {

                Payment payment = Payment.builder()
                    .payer(usersInGroup.get(1))
                    .receiver(usersInGroup.get(0))
                    .amount(20.0)
                    .date(LocalDateTime.now())
                    .group(group)
                    .build();

                Payment paymentDeleted = Payment.builder()
                    .payer(usersInGroup.get(0))
                    .receiver(usersInGroup.get(1))
                    .amount(420.0)
                    .date(LocalDateTime.now())
                    .group(group)
                    .deleted(true)
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
                .date(LocalDateTime.now())
                .group(group)
                .build();

            paymentRepository.save(payment);
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for payment");
        paymentRepository.deleteAll();
    }


}
