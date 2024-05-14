package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BudgetMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BudgetEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetService budgetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BudgetMapper budgetMapper;

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateBudgetValid() throws Exception {
        BudgetCreateDto budgetDto = BudgetCreateDto.builder()
            .name("Rent")
            .amount(5000)
            .build();

        GroupEntity group1 = GroupEntity.builder()
            .groupName("Developers")
            .users(new HashSet<>())
            .build();


        Budget mockBudget = Budget.builder()
            .name("Rent")
            .amount(5000)
            .group(group1)
            .build();

        when(budgetService.createBudget(any(), anyLong())).thenReturn(mockBudget);

        String budgetJson = objectMapper.writeValueAsString(budgetDto);

        byte[] responseBody = mockMvc.perform(post("/api/v1/group/1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES))
                .content(budgetJson))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        BudgetDto resultBudgetDto = objectMapper.readValue(responseBody, BudgetDto.class);

        assertNotNull(resultBudgetDto, "Response should not be null");
        assertEquals("Rent", resultBudgetDto.getName(), "Budget name should match");
        assertEquals(5000, resultBudgetDto.getAmount(), "Budget amount should match");
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateNotExistingBudget() throws Exception {
        BudgetDto budgetDto = BudgetDto.builder()
            .name("Rent Updated")
            .amount(5500)
            .build();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found"))
            .when(budgetService).updateBudget(any(BudgetDto.class), anyLong());
        String budgetJson = objectMapper.writeValueAsString(budgetDto);

        mockMvc.perform(put("/api/v1/group/1/budget/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES))
                .content(budgetJson))
            .andExpect(status().isNotFound());
    }
}
