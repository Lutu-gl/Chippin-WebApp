package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.GeneralDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "generateData"})
public abstract class BaseTest implements TestData {
    @Autowired
    GeneralDataGenerator generalDataGenerator;

    @BeforeEach
    public void generateDb() {
        generalDataGenerator.generateData();
    }

    @AfterEach
    public void tearDown() {
        generalDataGenerator.cleanData();
    }
}
