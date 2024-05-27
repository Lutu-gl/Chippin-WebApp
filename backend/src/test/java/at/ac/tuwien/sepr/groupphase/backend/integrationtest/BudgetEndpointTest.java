package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.BudgetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    public void whenCreateBudget_withValidData_thenStatus201() throws Exception {

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("EXuser1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("EXuser2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        BudgetCreateDto budgetDto = BudgetCreateDto.builder()
            .name("Fun Activities")
            .amount(150)
            .build();

        String body = objectMapper.writeValueAsString(budgetDto);

        mockMvc.perform(post("/api/v1/group/" + saved.getId() + "/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Fun Activities"))
            .andExpect(jsonPath("$.amount").value(150))
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteNonExistingBudget_thenStatus404() throws Exception {
        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("EXuser1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("EXuser2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        long nonExistingBudgetId = 9999;

        mockMvc.perform(delete("/api/v1/group/" + saved.getId() + "/budget/" + nonExistingBudgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Rollback
    public void testAddAndGet4Budgets_thenStatus200() throws Exception {

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("EXuser1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("EXuser2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);


        saveBudget("Education", 1000, group);
        saveBudget("Research", 2000, group);
        saveBudget("Development", 3000, group);
        saveBudget("Operations", 4000, group);

        mockMvc.perform(get("/api/v1/group/" + saved.getId() + "/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
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
            .timestamp(LocalDateTime.now())
            .group(group)
            .build();

        budgetRepository.save(budget);
    }
}
