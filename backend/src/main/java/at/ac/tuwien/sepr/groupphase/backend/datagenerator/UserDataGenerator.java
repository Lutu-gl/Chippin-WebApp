package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@AllArgsConstructor
public class UserDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

    @Override
    public void generateData() {
        LOGGER.debug("generating data for user");
        for (int i = 0; i < 100; i++) {
            userRepository.save(ApplicationUser.builder()
                .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
                .email("user" + (i + 1) + "@example.com")
                .admin(false)
                .build());
        }
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for user");
        userRepository.deleteAll();
    }

}
