package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class ActivityRepositoryTest implements TestData {
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

    @Test
    @Rollback
    @Transactional
    public void testCreateActivitySuccess() {
        Activity activity = ACTIVITY.get(0);
        expenseRepository.save(activity.getExpense());

        Activity savedActivity = activityRepository.save(activity);

        activity.setId(savedActivity.getId());
        assertEquals(activity, savedActivity);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetByIdActivitySuccess() {
        Activity activity = ACTIVITY.get(0);
        Activity savedActivity = activityRepository.save(activity);

        Activity foundActivity = activityRepository.findById(savedActivity.getId()).orElse(null);
        assertEquals(activity, foundActivity);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetByIdNotFoundNull() {
        Activity foundActivity = activityRepository.findById(666L).orElse(null);
        assertNull(foundActivity);
    }
}
