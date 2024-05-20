package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

/**
 * Interface for data generators.
 * When implementing a new data generator, add it to GeneralDataGenerator.
 */
public interface DataGenerator {
    void generateData();

    void cleanData();
}
