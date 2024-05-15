package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BaseTest implements TestData {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ActivityRepository activityRepository;
    @Autowired
    protected ExpenseRepository expenseRepository;

    @BeforeEach
    public void beforeEach() {
        activityRepository.deleteAll();
        userRepository.deleteAll();
        expenseRepository.deleteAll();
    }


}
