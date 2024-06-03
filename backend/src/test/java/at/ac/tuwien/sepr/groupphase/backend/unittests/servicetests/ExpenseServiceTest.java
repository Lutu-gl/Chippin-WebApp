package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.BudgetServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ExpenseServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseMapper expenseMapper;
    @Mock
    private ExpenseValidator expenseValidator;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Mock
    private BudgetServiceImpl budgetService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback
    public void testGetExpenseByIdSuccess() throws Exception {
        // Mock-Konfigurationen
        ApplicationUser mockUserEntity = ApplicationUser.builder().id(1L).email("test@email.com").build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpenseEntity = Expense.builder().group(mockGroupEntity).participants(new HashMap<>()).build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpenseEntity));
        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);
        when(expenseMapper.expenseEntityToExpenseDetailDto(any(Expense.class)))
            .thenReturn(ExpenseDetailDto.builder()
                .name("NewTestExpense")
                .category(Category.Other)
                .amount(10.0)
                .payerEmail("test@email.com")
                .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
                .build());

        // Execution
        ExpenseDetailDto result = expenseService.getById(1L, "test@email.com");

        // Verification
        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));

        verify(expenseRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(expenseMapper, times(1)).expenseEntityToExpenseDetailDto(any(Expense.class));

    }

    @Test
    @Transactional
    @Rollback
    public void testCreateExpenseSuccess() throws Exception {
        // Mock-Konfigurationen
        ApplicationUser mockUserEntity = ApplicationUser.builder().id(1L).email("test@email.com").build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpenseEntity = Expense.builder().group(mockGroupEntity).build();

        when(expenseMapper.expenseCreateDtoToExpenseEntity(any(ExpenseCreateDto.class))).thenReturn(mockExpenseEntity);
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpenseEntity);
        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);
        when(expenseMapper.expenseEntityToExpenseCreateDto(any(Expense.class)))
            .thenReturn(ExpenseCreateDto.builder()
                .name("NewTestExpense")
                .category(Category.Other)
                .amount(10.0)
                .payerEmail("test@email.com")
                .groupId(1L)
                .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
                .build());

        ExpenseCreateDto expenseCreateDto = ExpenseCreateDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("test@email.com")
            .groupId(1L)
            .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
            .build();

        // Execution
        ExpenseCreateDto result = expenseService.createExpense(expenseCreateDto, "test@email.com");

        // Verification
        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(1L, result.getGroupId());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));
        verify(expenseValidator, times(1)).validateForCreation(expenseCreateDto);
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateExpenseSuccess() throws Exception {
        // Mock-Konfigurationen
        ApplicationUser mockUserEntity = ApplicationUser.builder().id(1L).email("test@email.com").build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpenseEntity = Expense.builder().group(mockGroupEntity).build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpenseEntity));
        when(expenseMapper.expenseCreateDtoToExpenseEntity(any(ExpenseCreateDto.class))).thenReturn(mockExpenseEntity);
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpenseEntity);
        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);
        when(expenseMapper.expenseEntityToExpenseCreateDto(any(Expense.class)))
            .thenReturn(ExpenseCreateDto.builder()
                .name("NewTestExpense")
                .category(Category.Other)
                .amount(10.0)
                .payerEmail("test@email.com")
                .groupId(1L)
                .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
                .build());

        ExpenseCreateDto expenseCreateDto = ExpenseCreateDto.builder()
            .id(1L)
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("test@email.com")
            .groupId(1L)
            .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
            .build();

        // Execution
        ExpenseCreateDto result = expenseService.updateExpense(1L, expenseCreateDto, "test@email.com");

        // Verification
        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(1L, result.getGroupId());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));
        verify(expenseValidator, times(1)).validateForCreation(expenseCreateDto);
        verify(activityRepository, times(1)).save(any());

    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteExpenseSuccess() throws Exception {
        // Mock-Konfigurationen
        ApplicationUser mockUserEntity = ApplicationUser.builder().id(1L).email("test@email.com").build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpenseEntity = Expense.builder()
            .group(mockGroupEntity)
            .deleted(false)
            .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpenseEntity));
        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);

        // Execution
        expenseService.deleteExpense(1L, "test@email.com");

        // Verification
        verify(expenseRepository, times(1)).markExpenseAsDeleted(mockExpenseEntity);
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    @Transactional
    @Rollback
    public void testRecoverDeletedExpenseSuccess() throws Exception {
        // Mock-Konfigurationen
        ApplicationUser mockUserEntity = ApplicationUser.builder().id(1L).email("test@email.com").build();

        GroupEntity mockGroupEntity = GroupEntity.builder()
            .id(1L)
            .users(Set.of(mockUserEntity))
            .build();

        Expense mockExpenseEntity = Expense.builder()
            .deleted(true)
            .group(mockGroupEntity)
            .build();

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(mockExpenseEntity));
        when(userRepository.findByEmail(anyString())).thenReturn(mockUserEntity);
        when(expenseMapper.expenseEntityToExpenseCreateDto(any(Expense.class)))
            .thenReturn(ExpenseCreateDto.builder()
                .name("NewTestExpense")
                .category(Category.Other)
                .amount(10.0)
                .payerEmail("test@email.com")
                .groupId(1L)
                .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
                .build());

        // Execution
        ExpenseCreateDto result = expenseService.recoverExpense(1L, "test@email.com");

        // Verification
        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(1L, result.getGroupId());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));

        verify(expenseRepository, times(1)).markExpenseAsRecovered(mockExpenseEntity);
        verify(activityRepository, times(1)).save(any());
    }
}
