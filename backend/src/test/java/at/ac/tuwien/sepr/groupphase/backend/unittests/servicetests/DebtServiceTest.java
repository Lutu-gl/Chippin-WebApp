package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBevorAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class DebtServiceTest extends BaseTestGenAndClearBevorAfterEach {
    @Mock
    private ExpenseRepository expenseRepository;

    @Spy
    private ShoppingListMapperImpl shoppingListMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private DebtServiceImpl debtService;

    @Test
    public void calculatingDebtOnGroupThatDoesntExistAndReturnsNothingNotFoundException() {
        when(groupRepository.getById(anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> debtService.getById("user1@notfound.com", -666L));
    }

    @Test
    public void calculatingDebtOnGroupThatExistsReturnsCorrectForUser1Example_3ExpensesPos() {
        ArrayList<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"user2@example.com", new BigDecimal(50)});
        objects.add(new Object[]{"user3@example.com", new BigDecimal(30)});

        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(objects);

        GroupEntity groupExample0 = new GroupEntity();
        groupExample0.setId(1L);

        Set<ApplicationUser> users = new HashSet<>();
        users.add(ApplicationUser.builder().email("user1@example.com").build());
        users.add(ApplicationUser.builder().email("user2@example.com").build());
        users.add(ApplicationUser.builder().email("user3@example.com").build());
        groupExample0.setUsers(users);

        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupExample0));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(groupExample0));

        DebtGroupDetailDto dto = debtService.getById("user1@example.com", 1L);

        assertEquals(1L, dto.getGroupId());
        assertEquals(50.0, dto.getMembersDebts().get("user2@example.com"));
        assertEquals(30.0, dto.getMembersDebts().get("user3@example.com"));
    }

    @Test
    public void calculatingDebtOnGroupThatExistsReturnsCorrectForUser2Example_3ExpensesNeg() {
        ArrayList<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"user1@example.com", new BigDecimal(-50)});
        objects.add(new Object[]{"user3@example.com", new BigDecimal(80)});

        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(objects);
        when(groupRepository.existsById(anyLong())).thenReturn(true);

        GroupEntity groupExample0 = new GroupEntity();
        groupExample0.setId(2L);

        Set<ApplicationUser> users = new HashSet<>();
        users.add(ApplicationUser.builder().email("user1@example.com").build());
        users.add(ApplicationUser.builder().email("user2@example.com").build());
        users.add(ApplicationUser.builder().email("user3@example.com").build());
        groupExample0.setUsers(users);

        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupExample0));
        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupExample0));

        DebtGroupDetailDto dto = debtService.getById("user2@example.com", 2L);

        assertEquals(2L, dto.getGroupId());
        assertEquals(-50.0, dto.getMembersDebts().get("user1@example.com"));
        assertEquals(80.0, dto.getMembersDebts().get("user3@example.com"));
    }

    @Test
    public void calculatingDebtOnGroupThatExistsReturnsCorrectForUser3Example_2ExpensesNeg() {
        ArrayList<Object[]> objects = new ArrayList<>();
        objects.add(new Object[]{"user1@example.com", new BigDecimal(-30)});
        objects.add(new Object[]{"user2@example.com", new BigDecimal(-80)});

        when(expenseRepository.calculateBalancesExpensesAndPaymentsForUser(anyString(), anyLong())).thenReturn(objects);
        when(groupRepository.existsById(anyLong())).thenReturn(true);

        GroupEntity groupExample0 = new GroupEntity();
        groupExample0.setId(3L);

        Set<ApplicationUser> users = new HashSet<>();
        users.add(ApplicationUser.builder().email("user1@example.com").build());
        users.add(ApplicationUser.builder().email("user2@example.com").build());
        users.add(ApplicationUser.builder().email("user3@example.com").build());
        groupExample0.setUsers(users);

        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupExample0));
        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupExample0));

        DebtGroupDetailDto dto = debtService.getById("user3@example.com", 3L);

        assertEquals(3L, dto.getGroupId());
        assertEquals(-30.0, dto.getMembersDebts().get("user1@example.com"));
        assertEquals(-80.0, dto.getMembersDebts().get("user2@example.com"));
    }
}
