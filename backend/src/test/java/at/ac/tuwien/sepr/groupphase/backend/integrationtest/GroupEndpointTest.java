package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
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

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GroupEndpointTest extends BaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PantryRepository pantryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    public void whenUpdateGroup_withValidData_thenStatus200() throws Exception {
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
    public void whenCreateGroup_withValidData_thenCreatedPantryWithGroupId() throws Exception {
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
    public void whenCreateGroup_withValidData_thenStatus201() throws Exception {
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

}
