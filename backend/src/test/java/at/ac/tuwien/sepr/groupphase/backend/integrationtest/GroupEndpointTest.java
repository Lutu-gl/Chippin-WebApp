package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GroupEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PantryRepository pantryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    @Transactional
    public void whenUpdateGroup_withValidData_MemberLeaves_thenAllExpensesAndPaymentsMarkedAsArchived() throws Exception {
        userRepository.deleteAll();
        groupRepository.deleteAll();

        GroupEntity savedGroup = createChippinGroupWithExpense();


        GroupCreateDto groupUpdateDto =
            GroupCreateDto.builder().groupName("Chippin").members(new HashSet<>(Arrays.asList("rafael@chippin.com", "luca@chippin.com", "emil@chippin.com", "max@chippin.com", "sebastian@chippin.com")))
                .build();

        String body = objectMapper.writeValueAsString(groupUpdateDto);

        String res = mockMvc.perform(MockMvcRequestBuilders.put(String.format("/api/v1/group/%d", savedGroup.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("luca@chippin.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        GroupCreateDto updateDto = objectMapper.readValue(res, GroupCreateDto.class);
        GroupEntity groupSaved = groupRepository.getReferenceById(updateDto.getId());

        List<Expense> allByGroupId = expenseRepository.findAllByGroupId(groupSaved.getId());
        List<Payment> allByGroupId1 = paymentRepository.findAllByGroupId(groupSaved.getId());

        for (Expense expense : allByGroupId) {
            if (expense.getName().equals("TestEx1")) {
                assertFalse(expense.getArchived());
            } else {
                assertTrue(expense.getArchived());
            }
        }

        for (Payment payment : allByGroupId1) {
            if (payment.getAmount() == 30.0) {
                assertFalse(payment.getArchived());
            } else {
                assertTrue(payment.getArchived());
            }

        }

    }

    @Test
    @Rollback
    @Transactional
    public void whenUpdateGroup_withValidData_MemberLeaves_thenCorrect() throws Exception {
        userRepository.deleteAll();
        groupRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1GE@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2GE@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupEntity group = GroupEntity.builder().groupName("NewGroup").users(new HashSet<>(Arrays.asList(user1, user2))).build();
        Pantry pantry = Pantry.builder().group(group).build();
        group.setPantry(pantry);
        GroupEntity savedGroup = groupRepository.save(group);

        GroupCreateDto groupUpdateDto =
            GroupCreateDto.builder().groupName("NewGroup1").members(new HashSet<>(Arrays.asList("user1GE@example.com")))
                .build();

        String body = objectMapper.writeValueAsString(groupUpdateDto);

        String res = mockMvc.perform(MockMvcRequestBuilders.put(String.format("/api/v1/group/%d", savedGroup.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        GroupCreateDto updateDto = objectMapper.readValue(res, GroupCreateDto.class);
        GroupEntity groupSaved = groupRepository.getReferenceById(updateDto.getId());

        assertAll(
            () -> assertEquals(groupSaved.getGroupName(), updateDto.getGroupName()),
            () -> assertTrue(groupSaved.getUsers().contains(user1)),
            () -> assertFalse(groupSaved.getUsers().contains(user2))
        );
    }

    @Test
    @Rollback
    @Transactional
    public void whenUpdateGroup_withValidData_TwoMemberLeaves_thenCorrect() throws Exception {
        userRepository.deleteAll();
        groupRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1GE@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2GE@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupEntity group = GroupEntity.builder().groupName("NewGroup").users(new HashSet<>(Arrays.asList(user1, user2))).build();
        Pantry pantry = Pantry.builder().group(group).build();
        group.setPantry(pantry);
        GroupEntity savedGroup = groupRepository.save(group);

        GroupCreateDto groupUpdateDto =
            GroupCreateDto.builder().groupName("NewGroup1").members(new HashSet<>())
                .build();

        String body = objectMapper.writeValueAsString(groupUpdateDto);

        String res = mockMvc.perform(MockMvcRequestBuilders.put(String.format("/api/v1/group/%d", savedGroup.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        GroupCreateDto updateDto = objectMapper.readValue(res, GroupCreateDto.class);
        GroupEntity groupSaved = groupRepository.getReferenceById(updateDto.getId());

        assertAll(
            () -> assertEquals(groupSaved.getGroupName(), updateDto.getGroupName()),
            () -> assertFalse(groupSaved.getUsers().contains(user1)),
            () -> assertFalse(groupSaved.getUsers().contains(user2))
        );
    }


    @Test
    @Rollback
    @Transactional
    public void whenUpdateGroup_withValidData_thenStatus200() throws Exception {
        userRepository.deleteAll();
        groupRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1GE@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2GE@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupEntity group = GroupEntity.builder().groupName("NewGroup").users(new HashSet<>(Arrays.asList(user1, user2))).build();
        Pantry pantry = Pantry.builder().group(group).build();
        group.setPantry(pantry);
        GroupEntity savedGroup = groupRepository.save(group);

        GroupCreateDto groupUpdateDto =
            GroupCreateDto.builder().groupName("NewGroupChangedName").members(new HashSet<>(Arrays.asList("user1GE@example.com", "user2GE@example.com")))
                .build();

        String body = objectMapper.writeValueAsString(groupUpdateDto);

        String res = mockMvc.perform(MockMvcRequestBuilders.put(String.format("/api/v1/group/%d", savedGroup.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        GroupCreateDto updateDto = objectMapper.readValue(res, GroupCreateDto.class);
        GroupEntity groupSaved = groupRepository.getReferenceById(updateDto.getId());

        assertAll(
            () -> assertEquals(groupSaved.getGroupName(), updateDto.getGroupName()),
            () -> assertTrue(groupSaved.getUsers().contains(user1)),
            () -> assertTrue(groupSaved.getUsers().contains(user2))
        );
    }

    @Test
    @Transactional
    public void whenCreateGroup_withValidData_thenCreatedPantryWithGroupId() throws Exception {
        userRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1GE@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2GE@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroupGE").members(new HashSet<>(Arrays.asList("user1GE@example.com", "user2GE@example.com"))).build();
        String body = objectMapper.writeValueAsString(groupCreateDto);

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        GroupCreateDto createDto = objectMapper.readValue(res, GroupCreateDto.class);
        GroupEntity group = groupRepository.getReferenceById(createDto.getId());

        assertSame(group.getPantry().getId(), group.getId());
    }


    @Test
    @Transactional
    @Rollback
    public void whenCreateGroup_withValidData_thenStatus201() throws Exception {
        userRepository.deleteAll();
        groupRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1GE@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2GE@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroup").members(new HashSet<>(Arrays.asList("user1GE@example.com", "user2GE@example.com"))).build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        String res = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        GroupCreateDto createDto = objectMapper.readValue(res, GroupCreateDto.class);
        GroupEntity group = groupRepository.getReferenceById(createDto.getId());
        assertAll(
            () -> assertEquals(group.getGroupName(), createDto.getGroupName()),
            () -> assertTrue(group.getUsers().contains(user1)),
            () -> assertTrue(group.getUsers().contains(user2))
        );
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser("user1GE@example.com")
    public void whenCreateGroup_withInvalidData_thenStatus209ConflictMembersNotExist() throws Exception {
        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroup").members(new HashSet<>(Arrays.asList("user1GE@example.com", "user2GE@example.com"))).build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict())
            .andExpect(new ResultMatcher() {
                @Override
                public void match(MvcResult result) throws Exception {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("No user found with email: user1GE@example.com"));
                }
            });

    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser("admin@example.com")
    public void whenCreateGroup_withInvalidData_thenStatus409ConflictOwnerNotMember() throws Exception {
        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroup").members(new HashSet<>(Arrays.asList("user1GE@example.com", "user2@example.com"))).build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict())
            .andExpect(new ResultMatcher() {
                @Override
                public void match(MvcResult result) throws Exception {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("Owner must be a member of the group."));
                }
            });
    }

    @Test
    @Rollback
    @Transactional
    @WithMockUser("user1E@example.com")
    public void whenCreateGroup_withInvalidData_thenStatus422Validation() throws Exception {
        GroupCreateDto groupCreateDto = GroupCreateDto.builder()
            .groupName("     ")
            .members(new HashSet<>(Arrays.asList("user1GE@example.com", "user2GE@example.com")))
            .build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(new ResultMatcher() {
                @Override
                public void match(MvcResult result) throws Exception {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("Group name must not be empty"));
                }
            });
    }

    private GroupEntity createChippinGroupWithExpense() {
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("emil@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("rafael@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("luca@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("lukas@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("max@chippin.com")
            .admin(false)
            .build());
        userRepository.save(ApplicationUser.builder()
            .password("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG")
            .email("sebastian@chippin.com")
            .admin(false)
            .build());

        ApplicationUser user1 = userRepository.findByEmail("luca@chippin.com");
        ApplicationUser user2 = userRepository.findByEmail("max@chippin.com");
        ApplicationUser user3 = userRepository.findByEmail("lukas@chippin.com");
        ApplicationUser user4 = userRepository.findByEmail("rafael@chippin.com");
        ApplicationUser user5 = userRepository.findByEmail("emil@chippin.com");
        ApplicationUser user6 = userRepository.findByEmail("sebastian@chippin.com");

        Set<ApplicationUser> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);

        GroupEntity group = groupRepository.save(GroupEntity.builder()
            .groupName("Chippin")
            .users(users)
            .build());

        List<ApplicationUser> usersInGroup = new ArrayList<>(group.getUsers());
        usersInGroup.sort(Comparator.comparing(ApplicationUser::getEmail));

        Map<ApplicationUser, Double> participants = new HashMap<>();
        participants.put(usersInGroup.get(0), 0.6);
        participants.put(usersInGroup.get(1), 0.4); // user 1 owes user 0 40

        Map<ApplicationUser, Double> participants2 = new HashMap<>();
        participants2.put(usersInGroup.get(0), 0.5);
        participants2.put(usersInGroup.get(1), 0.2);
        participants2.put(usersInGroup.get(2), 0.3); // user 1 owes user 0 60 and user 2 owes user 0 30

        Map<ApplicationUser, Double> participants3 = new HashMap<>();
        participants3.put(usersInGroup.get(0), 0.1);
        participants3.put(usersInGroup.get(1), 0.1);
        participants3.put(usersInGroup.get(2), 0.8); // user1


        // With user 2 deleted: Only TestEx1 isnt archived
        Expense expense = Expense.builder()
            .name("TestEx1")
            .category(Category.Food)
            .amount(100.0d)
            .date(LocalDateTime.now())
            .payer(usersInGroup.get(0))
            .group(group)
            .participants(participants)
            .deleted(false)
            .archived(false)
            .build();

        Expense expense2 = Expense.builder()
            .name("TestEx2")
            .category(Category.Food)
            .amount(100.0d)
            .date(LocalDateTime.now())
            .group(group)
            .payer(usersInGroup.get(0))
            .participants(participants2)
            .deleted(false)
            .archived(false)
            .build();

        Expense expense3 = Expense.builder()
            .name("TestEx3")
            .category(Category.Food)
            .amount(100.0d)
            .date(LocalDateTime.now())
            .group(group)
            .payer(usersInGroup.get(1))
            .participants(participants3)
            .deleted(false)
            .archived(false)
            .build();
        expenseRepository.save(expense);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);

        // With user 2 deleted: Only Payment with 30 isnt archived
        Payment payment =
            Payment.builder()
                .payer(usersInGroup.get(1))
                .receiver(usersInGroup.get(0))
                .amount(30.0)
                .date(LocalDateTime.now())
                .group(group)
                .deleted(false)
                .archived(false)
                .build();

        Payment payment2 =
            Payment.builder()
                .payer(usersInGroup.get(1))
                .receiver(usersInGroup.get(2))
                .amount(20.0)
                .date(LocalDateTime.now())
                .group(group)
                .deleted(false)
                .archived(false)
                .build();

        paymentRepository.save(payment);
        paymentRepository.save(payment2);

        return group;
    }
}
