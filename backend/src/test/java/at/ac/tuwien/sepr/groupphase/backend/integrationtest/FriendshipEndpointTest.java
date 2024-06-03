package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBevorAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.AcceptFriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FriendshipEndpointTest extends BaseTestGenAndClearBevorAfterEach {
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

    @BeforeEach
    public void beforeEach() {
        ApplicationUser user1 = new ApplicationUser();
        user1.setEmail("testUser1@example.com");
        user1.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        ApplicationUser user2 = new ApplicationUser();
        user2.setEmail("testUser2@example.com");
        user2.setPassword("$2a$10$CMt4NPOyYWlEUP6zg6yNxewo24xZqQnmOPwNGycH0OW4O7bidQ5CG");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void whenSendFriendRequest_withValidData_thenStatus202() throws Exception {
        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail("testUser2@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testUser1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isCreated());

    }

    @Test
    public void sendFriendRequestAndAcceptItShouldWork() throws Exception {
        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail("testUser2@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testUser1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isCreated());

        AcceptFriendRequestDto acceptFriendRequestDto = new AcceptFriendRequestDto();
        acceptFriendRequestDto.setSenderEmail("testUser1@example.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/friendship/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acceptFriendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testUser2@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void sendFriendRequestAndRejectItShouldWork() throws Exception {
        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail("testUser2@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testUser1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/friendship/reject/{parameter}", "testUser1@example.com")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testUser2@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isOk());
    }

}
