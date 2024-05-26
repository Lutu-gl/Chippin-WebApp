package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpenseEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    @Test
    @Transactional
    @Rollback
    @WithMockUser("test@email.com")
    public void testGetByIdValid() throws Exception {
        ExpenseDetailDto newTestExpense = ExpenseDetailDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("test@email.com")
            .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
            .build();

        when(expenseService.getById(anyLong(), anyString())).thenReturn(newTestExpense);

        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/expense/1"))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        ExpenseDetailDto result = objectMapper.readerFor(ExpenseDetailDto.class)
            .readValue(body);

        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));

    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser("user1@examle.com")
    public void testCreateExpenseValid() throws Exception {
        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("test@email.com")
            .groupId(1L)
            .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
            .build();

        when(expenseService.createExpense(any(), anyString())).thenReturn(newTestExpense);

        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/expense")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTestExpense)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        ExpenseCreateDto result = objectMapper.readerFor(ExpenseCreateDto.class)
            .readValue(body);

        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(1L, result.getGroupId());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser("test@email.com")
    public void testUpdateExpenseValid() throws Exception {
        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("test@email.com")
            .groupId(1L)
            .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
            .build();

        when(expenseService.updateExpense(anyLong(), any(), anyString())).thenReturn(newTestExpense);

        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/expense/1")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTestExpense)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        ExpenseCreateDto result = objectMapper.readerFor(ExpenseCreateDto.class)
            .readValue(body);

        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(1L, result.getGroupId());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser("test@email.com")
    public void testDeleteExpenseValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/expense/1"))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        verify(expenseService, times(1)).deleteExpense(1L, "test@email.com");
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser("test@email.com")
    public void testRecoverExpenseValid() throws Exception {
        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("test@email.com")
            .groupId(1L)
            .participants(Map.of("test@email.com", 0.6, "user1@email.com", 0.4))
            .build();

        when(expenseService.recoverExpense(anyLong(), anyString())).thenReturn(newTestExpense);

        byte[] body = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/expense/recover/1"))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        ExpenseCreateDto result = objectMapper.readerFor(ExpenseCreateDto.class)
            .readValue(body);

        assertNotNull(result);
        assertEquals("NewTestExpense", result.getName());
        assertEquals(Category.Other, result.getCategory());
        assertEquals(10.0, result.getAmount());
        assertEquals("test@email.com", result.getPayerEmail());
        assertEquals(1L, result.getGroupId());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().containsKey("test@email.com"));
        assertTrue(result.getParticipants().containsKey("user1@email.com"));

        verify(expenseService, times(1)).recoverExpense(1L, "test@email.com");
    }
}
