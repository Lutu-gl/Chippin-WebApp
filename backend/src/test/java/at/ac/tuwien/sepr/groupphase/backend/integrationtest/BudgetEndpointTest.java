package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BudgetEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SecurityProperties securityProperties;


    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    @BeforeEach
    public void beforeEach() {
        budgetRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    @Transactional
    public void whenCreateBudget_withValidData_thenStatus201() throws Exception {

        GroupEntity group1 = GroupEntity.builder()
            .groupName("Developers")
            .users(new HashSet<>())
            .build();

        groupRepository.save(group1);


        BudgetCreateDto budgetDto = BudgetCreateDto.builder()
            .name("Fun Activities")
            .amount(150)
            .build();

        String body = objectMapper.writeValueAsString(budgetDto);

        mockMvc.perform(post("/api/v1/group/1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Fun Activities"))
            .andExpect(jsonPath("$.amount").value(150))
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    public void testDeleteNonExistingBudget_thenStatus404() throws Exception {
        long groupId = 1;  // Annahme, dass diese Gruppe existiert
        long nonExistingBudgetId = 9999;  // Eine ID, die sicher nicht in der Datenbank existiert

        mockMvc.perform(delete("/api/v1/group/" + groupId + "/budget/" + nonExistingBudgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testAddAndGet4Budgets_thenStatus200() throws Exception {
        int groupId = 1;

        GroupEntity group1 = GroupEntity.builder()
            .groupName("Developers")
            .users(new HashSet<>())
            .build();

        groupRepository.save(group1);


        saveBudget("Education", 1000, group1);
        saveBudget("Research", 2000, group1);
        saveBudget("Development", 3000, group1);
        saveBudget("Operations", 4000, group1);

        mockMvc.perform(get("/api/v1/group/" + group1.getId() + "/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[0].name").value("Education"))
            .andExpect(jsonPath("$[1].name").value("Research"))
            .andExpect(jsonPath("$[2].name").value("Development"))
            .andExpect(jsonPath("$[3].name").value("Operations"));

    }


    private void saveBudget(String name, double amount, GroupEntity group) {

        Budget budget = Budget.builder()
            .name(name)
            .amount(amount)
            .group(group)
            .build();

        budgetRepository.save(budget);
    }
}
