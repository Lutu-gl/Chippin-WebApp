package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity.ActivitySearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ActivityMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ActivityServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ActivityServiceTest {
    @Mock
    ActivityRepository activityRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GroupRepository groupRepository;

    @Mock
    ActivityMapper activityMapper;

    @InjectMocks
    ActivityServiceImpl activityService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback
    public void testGetActivityById() throws Exception {
        ApplicationUser mockUserEntity = ApplicationUser.builder()
            .id(1L)
            .email("test@email.com")
            .build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .groupName("TestGroup")
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpense = Expense.builder()
            .id(1L)
            .name("TestExpense")
            .group(mockGroupEntity)
            .date(LocalDateTime.now())
            .payer(mockUserEntity)
            .category(Category.Other)
            .build();

        Activity mockActivity1 = Activity.builder()
            .id(1L)
            .category(ActivityCategory.EXPENSE)
            .expense(mockExpense)
            .group(mockGroupEntity)
            .user(mockUserEntity)
            .build();
        ActivityDetailDto dtoMockActivity1 = ActivityDetailDto.builder()
            .id(1L)
            .category(ActivityCategory.EXPENSE)
            .groupId(1L)
            .expenseId(1L)
            .userId(1L)
            .build();

        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);
        when(activityRepository.findById(1L)).thenReturn(Optional.ofNullable(mockActivity1));
        when(activityMapper.activityEntityToActivityDetailDto(mockActivity1)).thenReturn(dtoMockActivity1);

        ActivityDetailDto activityDetailDto = activityService.getById(1L, "test@email.com");
        assertNotNull(activityDetailDto);
        assertEquals("User test@email.com created expense TestExpense in group TestGroup", activityDetailDto.getDescription());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetExpenseActivitiesByGroupId() throws Exception {
        ApplicationUser mockUserEntity = ApplicationUser.builder()
            .id(1L)
            .email("test@email.com")
            .build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .groupName("TestGroup")
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpense = Expense.builder()
            .id(1L)
            .name("TestExpense")
            .group(mockGroupEntity)
            .date(LocalDateTime.now())
            .payer(mockUserEntity)
            .category(Category.Other)
            .build();

        Activity mockActivity1 = Activity.builder()
            .category(ActivityCategory.EXPENSE)
            .expense(mockExpense)
            .group(mockGroupEntity)
            .user(mockUserEntity)
            .build();
        ActivityDetailDto dtoMockActivity1 = ActivityDetailDto.builder()
            .category(ActivityCategory.EXPENSE)
            .groupId(1L)
            .expenseId(1L)
            .userId(1L)
            .build();

        Activity mockActivity2 = Activity.builder()
            .category(ActivityCategory.EXPENSE_UPDATE)
            .expense(mockExpense)
            .group(mockGroupEntity)
            .user(mockUserEntity)
            .build();
        ActivityDetailDto dtoMockActivity2 = ActivityDetailDto.builder()
            .category(ActivityCategory.EXPENSE_UPDATE)
            .groupId(1L)
            .expenseId(1L)
            .userId(1L)
            .build();

        Activity mockActivity3 = Activity.builder()
            .category(ActivityCategory.EXPENSE_DELETE)
            .expense(mockExpense)
            .group(mockGroupEntity)
            .user(mockUserEntity)
            .build();
        ActivityDetailDto dtoMockActivity3 = ActivityDetailDto.builder()
            .category(ActivityCategory.EXPENSE_DELETE)
            .groupId(1L)
            .expenseId(1L)
            .userId(1L)
            .build();

        Activity mockActivity4 = Activity.builder()
            .category(ActivityCategory.EXPENSE_RECOVER)
            .expense(mockExpense)
            .group(mockGroupEntity)
            .user(mockUserEntity)
            .build();
        ActivityDetailDto dtoMockActivity4 = ActivityDetailDto.builder()
            .category(ActivityCategory.EXPENSE_RECOVER)
            .groupId(1L)
            .expenseId(1L)
            .userId(1L)
            .build();

        //Activity mockActivity
        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);
        when(groupRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockGroupEntity));
        when(activityRepository.findExpenseActivitiesByGroup(mockGroupEntity, null, null)).thenReturn(
          Set.of(
              mockActivity1,
              mockActivity2,
              mockActivity3,
              mockActivity4
          )
        );
        when(activityMapper.activityEntityToActivityDetailDto(mockActivity1)).thenReturn(dtoMockActivity1);
        when(activityMapper.activityEntityToActivityDetailDto(mockActivity2)).thenReturn(dtoMockActivity2);
        when(activityMapper.activityEntityToActivityDetailDto(mockActivity3)).thenReturn(dtoMockActivity3);
        when(activityMapper.activityEntityToActivityDetailDto(mockActivity4)).thenReturn(dtoMockActivity4);

        Collection<ActivityDetailDto> activities =  activityService.getExpenseActivitiesByGroupId(1L, "test@email.com", ActivitySearchDto.builder().build());

        assertEquals(4, activities.size());
        List<String> descriptions = activities.stream().map(ActivityDetailDto::getDescription).toList();
        assertTrue(descriptions.contains("User test@email.com created expense TestExpense in group TestGroup"));
        assertTrue(descriptions.contains("User test@email.com deleted expense TestExpense in group TestGroup"));
        assertTrue(descriptions.contains("User test@email.com recovered expense TestExpense in group TestGroup"));
        assertTrue(descriptions.contains("User test@email.com updated expense TestExpense in group TestGroup"));

    }

    @Test
    @Transactional
    @Rollback
    public void testGetPaymentActivitiesByGroupId() throws Exception {

        ApplicationUser mockUserEntity = ApplicationUser.builder()
            .id(1L)
            .email("test@email.com")
            .build();

        ApplicationUser mockUserEntity2 = ApplicationUser.builder()
            .id(2L)
            .email("test2@email.com")
            .build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .groupName("TestGroup")
            .users(Set.of(mockUserEntity))
            .build();

        Payment mockPayment = Payment.builder()
            .id(1L)
            .group(mockGroupEntity)
            .amount(10)
            .payer(mockUserEntity)
            .receiver(mockUserEntity2)
            .build();

        Activity mockActivity = Activity.builder()
            .category(ActivityCategory.PAYMENT)
            .payment(mockPayment)
            .group(mockGroupEntity)
            .user(mockUserEntity)
            .build();
        ActivityDetailDto dtoMockActivity = ActivityDetailDto.builder()
            .category(ActivityCategory.PAYMENT)
            .paymentId(1L)
            .groupId(1L)
            .userId(1L)
            .build();

        when(userRepository.findByEmail("test@email.com")).thenReturn(mockUserEntity);
        when(groupRepository.findById(1L)).thenReturn(Optional.ofNullable(mockGroupEntity));
        when(activityRepository.findPaymentActivitiesByGroup(mockGroupEntity, null, null)).thenReturn(Set.of(mockActivity));
        when(activityMapper.activityEntityToActivityDetailDto(mockActivity)).thenReturn(dtoMockActivity);

        Collection<ActivityDetailDto> activities = activityService.getPaymentActivitiesByGroupId(1L, "test@email.com", ActivitySearchDto.builder().build());

        assertEquals(1, activities.size());
        List<String> descriptions = activities.stream().map(ActivityDetailDto::getDescription).toList();
        assertTrue(descriptions.contains("test@email.com payed test2@email.com in group TestGroup"));
    }
}
