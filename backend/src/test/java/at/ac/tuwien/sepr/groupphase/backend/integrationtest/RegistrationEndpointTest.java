package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationEndpointTest extends BaseTest {
    @Autowired
    private MockMvc mockMvc;

    private UserRegisterDto userRegisterDto;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    public void setUp() {
        userRegisterDto = UserRegisterDto.builder()
            .email("test@example.com")
            .password("Test1234")
            .build();
    }

    @Test
    @Transactional
    @Rollback
    public void givenValidUser_whenRegister_then201() throws Exception {
        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenValidUser_whenRegister_then201AndUserExistsInDatabase() throws Exception {
        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        assertNotNull(customUserDetailService.loadUserByUsername(userRegisterDto.getEmail()));
    }

    @Test
    public void givenDuplicateEmail_whenRegister_then409() throws Exception {
        String body = objectMapper.writeValueAsString(userRegisterDto);

        // Register the user once
        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        // Attempt to register the user again
        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    @Rollback
    public void givenInvalidEmail_whenRegister_then400() throws Exception {
        // Set an invalid email
        userRegisterDto.setEmail("invalidEmail");

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Rollback
    public void givenInvalidPassword_whenRegister_then400() throws Exception {
        // Set an invalid password
        userRegisterDto.setPassword("invalid");

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPasswordWithoutNumber_whenRegister_then400() throws Exception {
        // Set a password without a number
        userRegisterDto.setPassword("Invalidpass");

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Rollback
    public void givenPasswordWithoutUppercase_whenRegister_then400() throws Exception {
        // Set a password without an uppercase letter
        userRegisterDto.setPassword("invalid1");

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPasswordWithoutLowercase_whenRegister_then400() throws Exception {
        // Set a password without a lowercase letter
        userRegisterDto.setPassword("INVALID1");

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPasswordLessThanEightCharacters_whenRegister_then400() throws Exception {
        // Set a password with less than eight characters
        userRegisterDto.setPassword("Inv1");

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenMissingEmail_whenRegister_then400() throws Exception {
        // Set email to null
        userRegisterDto.setEmail(null);

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenMissingPassword_whenRegister_then400() throws Exception {
        // Set password to null
        userRegisterDto.setPassword(null);

        String body = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/api/v1/authentication/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

}
