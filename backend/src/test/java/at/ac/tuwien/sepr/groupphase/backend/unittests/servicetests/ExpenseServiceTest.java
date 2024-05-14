package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ExpenseServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateExpenseSuccess() throws Exception {
        // Mock-Konfigurationen
        Expense mockExpenseEntity = new Expense();
        ;
        when(expenseMapper.expenseCreateDtoToExpenseEntity(any(ExpenseCreateDto.class))).thenReturn(mockExpenseEntity);
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpenseEntity);
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
        ExpenseCreateDto result = expenseService.createExpense(expenseCreateDto);

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
    }
}
