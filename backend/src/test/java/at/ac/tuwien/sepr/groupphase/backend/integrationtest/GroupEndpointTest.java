package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


    @Test
    @Transactional
    @Rollback
    public void whenCreateGroup_withValidData_thenStatus201() throws Exception {
        userRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroup").members(new HashSet<>(Arrays.asList("user1@example.com", "user2@example.com"))).build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        String res = mockMvc.perform(MockMvcRequestBuilders.post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        assertEquals(res, "{\"id\":1,\"groupName\":\"NewGroup\",\"members\":[\"user2@example.com\",\"user1@example.com\"]}");
    }

    @Test
    @Transactional
    @Rollback
    public void whenCreateGroup_withInvalidData_thenStatus209ConflictMembersNotExist() throws Exception {
        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroup").members(new HashSet<>(Arrays.asList("user1@example.com", "user2@example.com"))).build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict())
            .andExpect(new ResultMatcher() {
                @Override
                public void match(MvcResult result) throws Exception {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("No user found with email: user1@example.com"));
                }
            });

    }

    @Test
    @Transactional
    @Rollback
    public void whenCreateGroup_withInvalidData_thenStatus409ConflictOwnerNotMember() throws Exception {
        GroupCreateDto groupCreateDto =
            GroupCreateDto.builder().groupName("NewGroup").members(new HashSet<>(Arrays.asList("user1@example.com", "user2@example.com"))).build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@example.com", ADMIN_ROLES)))
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
    @Transactional
    @Rollback
    public void whenCreateGroup_withInvalidData_thenStatus422Validation() throws Exception {
        GroupCreateDto groupCreateDto = GroupCreateDto.builder()
            .groupName("     ")
            .members(new HashSet<>(Arrays.asList("user1@example.com", "user2@example.com")))
            .build();

        String body = objectMapper.writeValueAsString(groupCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
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
