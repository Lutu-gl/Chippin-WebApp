package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.ActivityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    @Test
    @Transactional
    @Rollback
    public void testGetById() throws Exception {
        ActivityDetailDto mockActivity = ActivityDetailDto.builder()
            .id(1L)
            .category(ActivityCategory.PAYMENT)
            .timestamp(LocalDateTime.now())
            .build();
        when(activityService.getById(any())).thenReturn(mockActivity);
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/activity/1")
            .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        ActivityDetailDto response = objectMapper.readValue(body, ActivityDetailDto.class);

        assertNotNull(response);
        assertEquals(mockActivity.getId(), response.getId());
        assertEquals(mockActivity.getCategory(), response.getCategory());
        assertEquals(mockActivity.getTimestamp(), response.getTimestamp());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetGroupExpenses() throws Exception {
        ActivityDetailDto mockActivity = ActivityDetailDto.builder()
            .id(1L)
            .category(ActivityCategory.EXPENSE)
            .timestamp(LocalDateTime.now())
            .build();
        when(activityService.getExpenseActivitiesByGroupId(any(), any(), any())).thenReturn(List.of(mockActivity));
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/activity/group-expenses/1")
            .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        ActivityDetailDto[] response = objectMapper.readValue(body, ActivityDetailDto[].class);

        assertNotNull(response);
        assertEquals(1, response.length);
        assertEquals(mockActivity.getId(), response[0].getId());
        assertEquals(mockActivity.getCategory(), response[0].getCategory());
        assertEquals(mockActivity.getTimestamp(), response[0].getTimestamp());
    }

    @Test
    @Transactional
    @Rollback
    public void testGetGroupPayments() throws Exception {
        ActivityDetailDto mockActivity = ActivityDetailDto.builder()
            .id(1L)
            .category(ActivityCategory.PAYMENT)
            .timestamp(LocalDateTime.now())
            .build();
        when(activityService.getPaymentActivitiesByGroupId(anyLong(), any(), any())).thenReturn(List.of(mockActivity));
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/activity/group-payments/1")
            .header("Authorization", "Bearer " + jwtTokenizer.getAuthToken("test@email.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        ActivityDetailDto[] response = objectMapper.readValue(body, ActivityDetailDto[].class);

        assertNotNull(response);
        assertEquals(1, response.length);
        assertEquals(mockActivity.getId(), response[0].getId());
        assertEquals(mockActivity.getCategory(), response[0].getCategory());
        assertEquals(mockActivity.getTimestamp(), response[0].getTimestamp());
    }

}
