package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PaymentEndpointTest extends BaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    @Test
    public void createPaymentPayerDoesNotExistConflict() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
            .groupId(1L)
            .payerEmail("use23123r1@examp666le.com")
            .receiverEmail("user1@example.com")
            .amount(50.0d)
            .build();

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("use23123r1@examp666le.com", ADMIN_ROLES)))
            .andExpect(status().isConflict())
            .andReturn().getResponse().getContentAsString();

        assertTrue(contentAsString.contains("Payer does not exist"));
    }

    @Test
    public void createPaymentReceiverDoesNotExistConflict() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
            .groupId(1L)
            .payerEmail("user1@example.com")
            .receiverEmail("use23123r1@examp666le.com")
            .amount(50.0d)
            .build();

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict())
            .andReturn().getResponse().getContentAsString();

        assertTrue(contentAsString.contains("Receiver does not exist"));
    }

    @Test
    public void createPaymentPayerEqualsReceiver422() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
            .groupId(1L)
            .payerEmail("user1@example.com")
            .receiverEmail("user1@example.com")
            .amount(50.0d)
            .build();

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertTrue(contentAsString.contains("Payer and receiver must not be the same person"));
    }

    @Test
    public void createPaymentPayerNotCreator422() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
            .groupId(1L)
            .payerEmail("user1@example.com")
            .receiverEmail("user2@example.com")
            .amount(50.0d)
            .build();

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user2@example.com", ADMIN_ROLES)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse().getContentAsString();

        assertTrue(contentAsString.contains("Creator email must be the same as payer email"));
    }

    @Test
    public void createPaymentOfNonExistingGroupShouldReturnConflict() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
            .groupId(-666L)
            .payerEmail("user1@example.com")
            .receiverEmail("user2@example.com")
            .amount(50.0d)
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isConflict());
    }

    @Test
    public void createPaymentForGroupAnd2UsersWorksAndValid() throws Exception {
        GroupEntity group = groupRepository.findByGroupName("groupExample0");


        PaymentDto paymentDto = PaymentDto.builder()
            .groupId(group.getId())
            .payerEmail("user1@example.com")
            .receiverEmail("user2@example.com")
            .amount(50.0d)
            .build();

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        PaymentDto paymentDtoResponse = objectMapper.readValue(contentAsString, PaymentDto.class);

        assertNotNull(paymentDtoResponse.getGroupId());
        assertEquals(paymentDto.getPayerEmail(), paymentDtoResponse.getPayerEmail());
        assertEquals(paymentDto.getReceiverEmail(), paymentDtoResponse.getReceiverEmail());
        assertEquals(paymentDto.getAmount(), paymentDtoResponse.getAmount());
    }
}
