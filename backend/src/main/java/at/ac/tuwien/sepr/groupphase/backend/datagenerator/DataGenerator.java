package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.exception.AlreadyRatedException;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;

/**
 * Interface for data generators.
 * When implementing a new data generator, add it to GeneralDataGenerator.
 */
public interface DataGenerator {
    void generateData() throws UserAlreadyExistsException, AlreadyRatedException;

    void cleanData();
}
