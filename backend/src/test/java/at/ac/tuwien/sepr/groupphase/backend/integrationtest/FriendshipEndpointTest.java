package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AcceptFriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FriendshipEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private SecurityProperties securityProperties;


    @AfterEach
    public void afterEach() {
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    @Transactional
    public void whenSendFriendRequest_withValidData_thenStatus202() throws Exception {
        userRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail("user2@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isCreated());

    }

    @Test
    @Rollback
    @Transactional
    public void sendFriendRequestAndAcceptItShouldWork() throws Exception {
        userRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail("user2@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isCreated());

        AcceptFriendRequestDto acceptFriendRequestDto = new AcceptFriendRequestDto();
        acceptFriendRequestDto.setSenderEmail("user1@example.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/friendship/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acceptFriendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user2@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isOk());

    }

    @Test
    @Rollback
    @Transactional
    public void sendFriendRequestAndRejectItShouldWork() throws Exception {
        userRepository.deleteAll();

        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("user1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("user2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);

        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail("user2@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/friendship/reject/{parameter}", "user1@example.com")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user2@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isOk());
    }

}
