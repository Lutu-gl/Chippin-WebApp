package at.ac.tuwien.sepr.groupphase.backend.datageneratorTest;

import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;

/**
 * Interface for data generators.
 * When implementing a new data generator, add it to GeneralDataGenerator.
 */
public interface DataGeneratorTest {
    void generateData() throws UserAlreadyExistsException;

    void cleanData();
}
