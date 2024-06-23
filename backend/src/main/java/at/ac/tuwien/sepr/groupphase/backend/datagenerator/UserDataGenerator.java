package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Random;

@Component
@AllArgsConstructor
public class UserDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

    @Override
    public void generateData() {
        LOGGER.trace("generating data for user");
        Random random = new Random();
        random.setSeed(12345);
        final Faker faker = new Faker(Locale.getDefault(), random);


        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("emil@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("rafael@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("luca@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("lukas@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("max@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("sebastian@chippin.com")
            .admin(false)
            .build());

        // random users
        for (int i = 0; i < 25; i++) {
            userRepository.save(ApplicationUser.builder()
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .email(faker.internet().emailAddress())
                .admin(false)
                .build());
        }
    }

    @Override
    public void cleanData() {
        LOGGER.trace("cleaning data for user");
        userRepository.deleteAll();
    }

}