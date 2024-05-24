package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AcceptFriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.FriendshipService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FriendshipEndpointTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private FriendshipService friendshipService;
    @MockBean
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private SecurityProperties securityProperties;

    private static final String TEST_EMAIL_1 = "friendshipTestUser1@test.com";
    private static final String TEST_EMAIL_2 = "friendshipTestUser2@test.com";

    private String[] getLoginTokensOfTestUsers() {
        String bearerToken1 = jwtTokenizer.getAuthToken(TEST_EMAIL_1, List.of("ROLE_USER"));
        String bearerToken2 = jwtTokenizer.getAuthToken(TEST_EMAIL_2, List.of("ROLE_USER"));

        return new String[] {bearerToken1, bearerToken2};
    }

    @Test
    @WithMockUser(username = TEST_EMAIL_1)
    public void testSendFriendRequestShouldReturn202() throws Exception {

        //String[] tokens = getLoginTokensOfTestUsers();

        FriendRequestDto friendRequestDto = new FriendRequestDto();
        friendRequestDto.setReceiverEmail(TEST_EMAIL_2);

        mockMvc.perform(post("/api/v1/friendship")
                //.header(securityProperties.getAuthHeader(), tokens[0])
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(friendRequestDto))
            )
            .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = TEST_EMAIL_1)
    public void testAcceptFriendRequestShouldReturn200() throws Exception {

        //String[] tokens = getLoginTokensOfTestUsers();

        friendshipService.sendFriendRequest(TEST_EMAIL_2, TEST_EMAIL_1);

        AcceptFriendRequestDto acceptFriendRequestDto = new AcceptFriendRequestDto();
        acceptFriendRequestDto.setSenderEmail(TEST_EMAIL_2);

        mockMvc.perform(put("/api/v1/friendship/accept")
                //.header(securityProperties.getAuthHeader(), tokens[0])
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acceptFriendRequestDto))
            )
            .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = TEST_EMAIL_1)
    public void testRejectFriendRequestShouldReturn200() throws Exception {
        //String[] tokens = getLoginTokensOfTestUsers();

        friendshipService.sendFriendRequest(TEST_EMAIL_2, TEST_EMAIL_1);

        mockMvc.perform(delete("/api/v1/friendship/reject/{parameter}", TEST_EMAIL_2)
                //.header(securityProperties.getAuthHeader(), tokens[0])
            )
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL_1)
    public void testGetFriendsShouldReturn200() throws Exception {
        //String[] tokens = getLoginTokensOfTestUsers();

        friendshipService.sendFriendRequest(TEST_EMAIL_2, TEST_EMAIL_1);
        friendshipService.acceptFriendRequest(TEST_EMAIL_2, TEST_EMAIL_1);

        when(friendshipService.getFriends(TEST_EMAIL_1)).thenReturn(List.of(TEST_EMAIL_2));

        byte[] body = mockMvc.perform(get("/api/v1/friendship/friends")
                //.header(securityProperties.getAuthHeader(), tokens[0])
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        Collection<String> res = objectMapper.readerFor(Collection.class).readValue(body);
        assertEquals(1, res.size());
        assertTrue(res.contains(TEST_EMAIL_2));
    }

    @Test
    @WithMockUser(username = TEST_EMAIL_1)
    public void testGetIncomingFriendRequestsShouldReturn200() throws Exception {
        //String[] tokens = getLoginTokensOfTestUsers();
        friendshipService.sendFriendRequest(TEST_EMAIL_2, TEST_EMAIL_1);
        when(friendshipService.getIncomingFriendRequest(TEST_EMAIL_1)).thenReturn(List.of(TEST_EMAIL_2));

        byte[] body = mockMvc.perform(get("/api/v1/friendship/friend-requests")
                //.header(securityProperties.getAuthHeader(), tokens[0])
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        Collection<String> res = objectMapper.readerFor(Collection.class).readValue(body);
        assertEquals(1, res.size());
        assertTrue(res.contains(TEST_EMAIL_2));
    }
}
