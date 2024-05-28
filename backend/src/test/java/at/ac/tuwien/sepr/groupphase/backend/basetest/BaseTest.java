package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.datageneratorTest.GeneralDataGeneratorTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// This is needed to run @BeforeAll and @AfterAll with non-static methods
public abstract class BaseTest implements TestData {
    @Autowired
    GeneralDataGeneratorTest generalDataGenerator;

    @BeforeAll
    public void generateDb() {
        generalDataGenerator.generateData();
    }

    @AfterAll
    public void tearDown() {
        generalDataGenerator.cleanData();
    }
}
