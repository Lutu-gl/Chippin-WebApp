package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BudgetMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetFrequency;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.BudgetServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.BudgetValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @Mock
    private BudgetValidator budgetValidator;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateBudgetSuccess() throws Exception {
        GroupEntity mockGroupEntity = new GroupEntity();
        BudgetCreateDto budgetCreateDto = BudgetCreateDto.builder()
            .name("Test Budget")
            .amount(500)
            .category(Category.Food)
            .resetFrequency(ResetFrequency.MONTHLY)
            .build();
        Budget mockBudget = new Budget();

        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(mockGroupEntity));
        when(budgetMapper.budgetCreateDtoToBudget(budgetCreateDto)).thenReturn(mockBudget);
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        Budget result = budgetService.createBudget(budgetCreateDto, 1L);

        assertNotNull(result);
        verify(budgetValidator, times(1)).validateForCreation(budgetCreateDto, 1L);
        verify(budgetRepository, times(1)).save(mockBudget);
    }

    @Test
    public void testUpdateBudgetSuccess() throws Exception {
        BudgetDto budgetDto = BudgetDto.builder()
            .id(1L)
            .name("Updated Budget")
            .amount(600)
            .category(Category.Entertainment)
            .resetFrequency(ResetFrequency.WEEKLY)
            .build();
        Budget mockBudget = new Budget();
        mockBudget.setId(1L);

        when(budgetRepository.findByIdAndGroupId(anyLong(), anyLong())).thenReturn(Optional.of(mockBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        Budget result = budgetService.updateBudget(budgetDto, 1L);

        assertNotNull(result);
        assertEquals("Updated Budget", result.getName());
        verify(budgetValidator, times(1)).validateForUpdate(budgetDto, 1L);
        verify(budgetRepository, times(1)).save(mockBudget);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateBudgetConflictException() throws Exception {
        BudgetCreateDto budgetCreateDto = BudgetCreateDto.builder().build();
        doThrow(new ConflictException("Conflict data", null)).when(budgetValidator).validateForCreation(any(), anyLong());

        assertThrows(ConflictException.class, () -> {
            budgetService.createBudget(budgetCreateDto, 1L);
        });
    }

    @Test
    public void testAddUsedAmount() {
        Budget mockBudget = new Budget();
        mockBudget.setCategory(Category.Food);
        mockBudget.setAlreadySpent(100.0);
        mockBudget.setTimestamp(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        mockBudget.setResetFrequency(ResetFrequency.MONTHLY);
        List<Budget> budgets = Arrays.asList(mockBudget);

        when(budgetRepository.findByGroupId(anyLong())).thenReturn(budgets);

        budgetService.addUsedAmount(1L, 50.0, Category.Food, LocalDateTime.now());

        verify(budgetRepository, times(1)).save(mockBudget);
        assertEquals(150.0, mockBudget.getAlreadySpent());
    }

    @Test
    public void testRemoveUsedAmount() {
        Budget mockBudget = new Budget();
        mockBudget.setCategory(Category.Food);
        mockBudget.setAlreadySpent(100.0);
        mockBudget.setTimestamp(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        mockBudget.setResetFrequency(ResetFrequency.MONTHLY);
        List<Budget> budgets = Arrays.asList(mockBudget);

        when(budgetRepository.findByGroupId(anyLong())).thenReturn(budgets);

        budgetService.removeUsedAmount(1L, 50.0, Category.Food, LocalDateTime.now());

        verify(budgetRepository, times(1)).save(mockBudget);
        assertEquals(50.0, mockBudget.getAlreadySpent());
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteBudget() {
        Budget mockBudget = new Budget();
        mockBudget.setId(1L);

        when(budgetRepository.findByIdAndGroupId(anyLong(), anyLong())).thenReturn(Optional.of(mockBudget));

        budgetService.deleteBudget(1L, 1L);

        verify(budgetRepository, times(1)).delete(mockBudget);
    }

    @Test
    public void testFindByGroupIdAndBudgetId() {
        Budget mockBudget = new Budget();
        mockBudget.setId(1L);

        when(budgetRepository.findByIdAndGroupId(anyLong(), anyLong())).thenReturn(Optional.of(mockBudget));

        Budget result = budgetService.findByGroupIdAndBudgetId(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
