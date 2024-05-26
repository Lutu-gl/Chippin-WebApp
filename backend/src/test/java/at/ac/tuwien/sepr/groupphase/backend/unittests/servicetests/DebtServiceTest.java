package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.DebtServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class DebtServiceTest extends BaseTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Spy
    private ShoppingListMapperImpl shoppingListMapper;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @InjectMocks
    private DebtServiceImpl debtService;

    @Test
    public void calculatingDebtOnGroupThatDoesntExistAndReturnsNothingNotFoundException() {
        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(new ArrayList<Object[]>());


        assertThrows(NotFoundException.class, () -> debtService.getById("user1@notfound.com", -666L));
    }

    @Test
    public void calculatingDebtOnGroupThatExistsReturnsCorrectForUser1Example_3ExpensesPos() {
        ArrayList<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"user2@example.com", new BigDecimal(50)});
        objects.add(new Object[]{"user3@example.com", new BigDecimal(30)});

        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(objects);
        GroupEntity groupExample0 = groupRepository.findByGroupName("groupExample0");

        DebtGroupDetailDto dto = debtService.getById("user1@example.com", groupExample0.getId());

        assertEquals(groupExample0.getId(), dto.getGroupId());
        assertEquals(50.0d, dto.getMembersDebts().get("user2@example.com"));
        assertEquals(30.0d, dto.getMembersDebts().get("user3@example.com"));
    }

    @Test
    public void calculatingDebtOnGroupThatExistsReturnsCorrectForUser2Example_3ExpensesNeg() {
        ArrayList<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"user1@example.com", new BigDecimal(-50)});
        objects.add(new Object[]{"user3@example.com", new BigDecimal(80)});

        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(objects);
        GroupEntity groupExample0 = groupRepository.findByGroupName("groupExample0");

        DebtGroupDetailDto dto = debtService.getById("user2@example.com", groupExample0.getId());

        assertEquals(groupExample0.getId(), dto.getGroupId());
        assertEquals(-50.0d, dto.getMembersDebts().get("user1@example.com"));
        assertEquals(80.0d, dto.getMembersDebts().get("user3@example.com"));
    }

    @Test
    public void calculatingDebtOnGroupThatExistsReturnsCorrectForUser3Example_2ExpensesNeg() {
        ArrayList<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"user1@example.com", new BigDecimal(-30)});
        objects.add(new Object[]{"user2@example.com", new BigDecimal(-80)});

        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(objects);
        GroupEntity groupExample0 = groupRepository.findByGroupName("groupExample0");

        DebtGroupDetailDto dto = debtService.getById("user3@example.com", groupExample0.getId());

        assertEquals(groupExample0.getId(), dto.getGroupId());
        assertEquals(-30.0d, dto.getMembersDebts().get("user1@example.com"));
        assertEquals(-80.0d, dto.getMembersDebts().get("user2@example.com"));
    }
}
