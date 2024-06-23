package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserChangePasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChangePasswordEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testChangePassword_returns200() throws Exception {
        ApplicationUser user = ApplicationUser.builder()
            .email("testPasswordChangeWorks@example.com")
            .password("$2a$10$FScNm.MGJS/rutdSPUag0OIZUTaOwSbZ6h.xD4DDUcbozeNdK7oJa")
            .build();

        userRepository.save(user);

        UserChangePasswordDto userChangePasswordDto = UserChangePasswordDto.builder()
            .currentPassword("Kennwort0")
            .newPassword("Kennwort1")
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/change-password")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testPasswordChangeWorks@example.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userChangePasswordDto)))
            .andExpect(status().isNoContent());

    }

    @Test
    public void testChangePassword_WithWrongCurrentPassword_Returns403() throws Exception {
        ApplicationUser user = ApplicationUser.builder()
            .email("testPasswordChangeFails@example.com")
            .password("$2a$10$FScNm.MGJS/rutdSPUag0OIZUTaOwSbZ6h.xD4DDUcbozeNdK7oJa")
            .build();

        userRepository.save(user);

        UserChangePasswordDto userChangePasswordDto = UserChangePasswordDto.builder()
            .currentPassword("Kennwort1")
            .newPassword("Kennwort2")
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/change-password")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("testPasswordChangeFails@example.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userChangePasswordDto)))
            .andExpect(status().isForbidden());

    }

}
