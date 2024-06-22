package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ExpenseEndpointTest extends BaseTest {
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

    ApplicationUser user1;
    ApplicationUser user2;

    @BeforeAll
    public void beforeAll() {
        user1 = new ApplicationUser();
        user1.setEmail("EXuser1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        user2 = new ApplicationUser();
        user2.setEmail("EXuser2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    @Rollback
    public void whenCreateExpense_withValidData_thenStatus201() throws Exception {
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

        String res = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expense")
                .param("name", "NewTestExpense")
                .param("category", "Other")
                .param("amount", "10.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", saved.getId().toString())
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4)))
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
    public void whenCreateExpense_withInvalidData_thenStatus409() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup2")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        groupRepository.save(group);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expense")
                .param("name", "NewTestExpense")
                .param("amount", "10.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", "1")
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "user2@email.com", 0.4)))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict());
    }

    @Test
    @Rollback
    public void whenCreateExpense_withInvalidData_thenStatus400() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup3")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        groupRepository.save(group);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expense")
                .param("name", "!!!!")
                .param("category", "Other")
                .param("amount", "10.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", "1")
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4)))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Rollback
    public void whenCreateExpense_withBill_thenStatus201() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        MockMultipartFile file = new MockMultipartFile("bill", "bill.png", "image/png", "This is an image".getBytes());

        byte[] res = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/expense")
                .file(file)
                .param("name", "NewTestExpense")
                .param("category", "Other")
                .param("amount", "10.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", saved.getId().toString())
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4)))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        ExpenseDetailDto result = objectMapper.readerFor(ExpenseDetailDto.class)
            .readValue(res);

        assertNotNull(result);
    }

    @Test
    @Rollback
    public void whenUpdateExpense_withValidData_thenStatus200() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup4")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        ExpenseCreateDto newTestExpense = ExpenseCreateDto.builder()
            .name("NewTestExpenseEdited")
            .category(Category.Other)
            .amount(20.0)
            .payerEmail("EXuser1@example.com")
            .groupId(saved.getId())
            .participants(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4))
            .build();

        Expense savedExpense = expenseRepository.save(Expense.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payer(user1)
            .group(saved)
            .date(LocalDateTime.now())
            .deleted(false)
            .archived(false)
            .participants(Map.of(user1, 0.6, user2, 0.4))
            .build());

        String res = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expense/" + savedExpense.getId())
                .param("name", "NewTestExpenseEdited")
                .param("category", "Other")
                .param("amount", "20.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", saved.getId().toString())
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4)))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        ExpenseCreateDto result = objectMapper.readerFor(ExpenseCreateDto.class)
            .readValue(res);

        newTestExpense.setId(result.getId());
        assertEquals(newTestExpense, result);
    }

    @Test
    @Rollback
    public void whenUpdateExpense_withInvalidData_thenStatus422() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup4")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        Expense savedExpense = expenseRepository.save(Expense.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payer(user1)
            .group(saved)
            .date(LocalDateTime.now())
            .deleted(false)
            .archived(false)
            .participants(Map.of(user1, 0.6, user2, 0.4))
            .build());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expense/" + savedExpense.getId())
                .param("name", "!!!!!!")
                .param("category", "Other")
                .param("amount", "20.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", saved.getId().toString())
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4)))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Rollback
    public void whenDeleteExpense_thenStatus200() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup4")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        Expense savedExpense = expenseRepository.save(Expense.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payer(user1)
            .group(saved)
            .date(LocalDateTime.now())
            .deleted(false)
            .archived(false)
            .participants(Map.of(user1, 0.6, user2, 0.4))
            .build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expense/" + savedExpense.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        Expense expense = expenseRepository.findById(savedExpense.getId()).orElse(null);
        assertNotNull(expense);
        assertTrue(expense.isDeleted());
    }

    @Test
    @Rollback
    public void whenDeleteUnknownExpense_thenStatus404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expense/-50")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void whenRecoverExpense_thenStatus200() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup4")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        Expense savedExpense = expenseRepository.save(Expense.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payer(user1)
            .group(saved)
            .date(LocalDateTime.now())
            .deleted(true)
            .archived(false)
            .participants(Map.of(user1, 0.6, user2, 0.4))
            .build());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expense/recover/" + savedExpense.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk());

        Expense expense = expenseRepository.findById(savedExpense.getId()).orElse(null);
        assertNotNull(expense);
        assertFalse(expense.isDeleted());
    }

    @Test
    @Rollback
    public void whenRecoverUnknownExpense_thenStatus404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expense/recover/-50")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void whenGetExpenseWithBill_thenStatus200() throws Exception {

        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        MockMultipartFile file = new MockMultipartFile("bill", "bill.png", "image/png", "This is an image".getBytes());

        byte[] res = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/expense")
                .file(file)
                .param("name", "NewTestExpense")
                .param("category", "Other")
                .param("amount", "10.0")
                .param("payerEmail", "EXuser1@example.com")
                .param("groupId", saved.getId().toString())
                .param("participants", objectMapper.writeValueAsString(Map.of("EXuser1@example.com", 0.6, "EXuser2@example.com", 0.4)))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        ExpenseDetailDto result = objectMapper.readerFor(ExpenseDetailDto.class)
            .readValue(res);

        assertNotNull(result);

        Long id = result.getId();

        byte[] res2 = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expense/bill/" + id)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        assertArrayEquals("This is an image".getBytes(), res2);
    }


    @Test
    @Rollback
    public void whenGetExpenseWithoutBill_thenStatus404() throws Exception {
        GroupEntity group = GroupEntity.builder()
            .groupName("EXTestGroup4")
            .users(new HashSet<>(Arrays.asList(user1, user2)))
            .build();

        GroupEntity saved = groupRepository.save(group);

        Expense savedExpense = expenseRepository.save(Expense.builder()
            .name("NewTestExpense")
            .category(Category.Other)
            .amount(10.0)
            .payer(user1)
            .group(saved)
            .date(LocalDateTime.now())
            .deleted(false)
            .archived(false)
            .participants(Map.of(user1, 0.6, user2, 0.4))
            .build());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expense/bill/" + savedExpense.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("EXuser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }
}
