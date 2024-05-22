package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DebtEndpointTest extends BaseTest {
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
    private PaymentRepository paymentRepository;

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
    public void retreiveDebtOfNonExistingGroupShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/debt/-666")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1GE@example.com", ADMIN_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void retreiveDebtOfGroupCorrectValuesUser1Example_3ExpensesPos() throws Exception {
        GroupEntity groupExample0 = groupRepository.findByGroupName("groupExample0");


        String res = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/v1/debt/%d", groupExample0.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        DebtGroupDetailDto debtGroupDetailDto = objectMapper.readValue(res, DebtGroupDetailDto.class);


        // Look in the datagen for the values. Pay also attention to the payment entries.
        assertEquals(groupExample0.getId(), debtGroupDetailDto.getGroupId());
        assertEquals(30.0d, debtGroupDetailDto.getMembersDebts().get("user2@example.com"));
        assertEquals(30.0d, debtGroupDetailDto.getMembersDebts().get("user3@example.com"));

    }

    @Test
    public void retreiveDebtOfGroupCorrectValuesUser2Example_3ExpensesNeg() throws Exception {
        GroupEntity groupExample0 = groupRepository.findByGroupName("groupExample0");


        String res = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/v1/debt/%d", groupExample0.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user2@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        DebtGroupDetailDto debtGroupDetailDto = objectMapper.readValue(res, DebtGroupDetailDto.class);

        // Look in the datagen for the values. Pay also attention to the payment entries.
        assertEquals(groupExample0.getId(), debtGroupDetailDto.getGroupId());
        assertEquals(-30.0d, debtGroupDetailDto.getMembersDebts().get("user1@example.com"));
        assertEquals(80.0d, debtGroupDetailDto.getMembersDebts().get("user3@example.com"));

    }

    @Test
    public void retreiveDebtOfGroupCorrectValuesUser3Example_2ExpensesNeg() throws Exception {
        GroupEntity groupExample0 = groupRepository.findByGroupName("groupExample0");

        String res = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/v1/debt/%d", groupExample0.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user3@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        DebtGroupDetailDto debtGroupDetailDto = objectMapper.readValue(res, DebtGroupDetailDto.class);

        // Look in the datagen for the values. Pay also attention to the payment entries.
        assertEquals(groupExample0.getId(), debtGroupDetailDto.getGroupId());
        assertEquals(-30.0d, debtGroupDetailDto.getMembersDebts().get("user1@example.com"));
        assertEquals(-80.0d, debtGroupDetailDto.getMembersDebts().get("user2@example.com"));

    }


}
