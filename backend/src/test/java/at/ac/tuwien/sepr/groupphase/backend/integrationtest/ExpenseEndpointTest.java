package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ExpenseEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @AfterEach
    public void afterEach() {
        expenseRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        expenseRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    @Transactional
    public void whenCreateExpense_withValidData_thenStatus201() throws Exception {
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

        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("EXuser1@example.com")
            .groupId(saved.getId())
            .participants(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4))
            .build();


        String body = objectMapper.writeValueAsString(newTestExpense);

        String res = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        ExpenseCreateDto result = objectMapper.readerFor(ExpenseCreateDto.class)
            .readValue(res);

        newTestExpense.setId(result.getId());
        assertEquals(newTestExpense, result);
    }

    @Test
    @Rollback
    @Transactional
    public void whenCreateExpense_withInvalidData_thenStatus409() throws Exception {
        expenseRepository.deleteAll();

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

        groupRepository.save(group);

        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("EXuser1@example.com")
            .groupId(1L)
            .participants(Map.of("EXuser1@example.com", 0.6, "user2@email.com", 0.4))
            .build();


        String body = objectMapper.writeValueAsString(newTestExpense);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict());
    }

    @Test
    @Rollback
    @Transactional
    public void whenCreateExpense_withInvalidData_thenStatus400() throws Exception {
        expenseRepository.deleteAll();

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

        groupRepository.save(group);

        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("!!!!")
            .category(Category.Other)
            .amount(10.0)
            .payerEmail("EXuser1@example.com")
            .groupId(1L)
            .participants(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4))
            .build();


        String body = objectMapper.writeValueAsString(newTestExpense);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isBadRequest());
    }

}
