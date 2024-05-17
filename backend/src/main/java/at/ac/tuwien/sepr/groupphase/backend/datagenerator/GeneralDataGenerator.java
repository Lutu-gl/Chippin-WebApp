package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
@AllArgsConstructor
public class GeneralDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserDataGenerator userDataGenerator;
    private final GroupDataGenerator groupDataGenerator;
    private final FriendshipDataGenerator friendshipDataGenerator;
    private final PantryDataGenerator pantryDataGenerator;


    @PostConstruct
    public void generateData() {
        LOGGER.debug("generating all data");

        cleanData();
        userDataGenerator.generateData();
        groupDataGenerator.generateData();
        friendshipDataGenerator.generateData();
        pantryDataGenerator.generateData();
        LOGGER.debug("finished generating all data");

    }

    public void cleanData() {
        LOGGER.debug("cleaning all data");
        pantryDataGenerator.cleanData();
        friendshipDataGenerator.cleanData();
        groupDataGenerator.cleanData();
        userDataGenerator.cleanData();
        LOGGER.debug("finished cleaning all data");

    }

}
