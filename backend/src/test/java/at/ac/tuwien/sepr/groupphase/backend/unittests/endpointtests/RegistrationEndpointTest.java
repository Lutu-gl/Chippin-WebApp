package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationEndpointTest extends BaseTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;
  @Autowired
  private ObjectMapper objectMapper;


  @Test
  public void givenValidUser_whenRegister_then201Created() throws Exception {
    UserRegisterDto userRegisterDto = UserRegisterDto.builder()
        .email("test@example.com").password("Test1234").build();
    when(userService.register(userRegisterDto, false)).thenReturn("jwtToken");

    mockMvc.perform(post("/api/v1/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRegisterDto)))
        .andExpect(status().isCreated());

    verify(userService, times(1)).register(userRegisterDto, false);
  }

  @Test
  public void givenExistingUser_whenRegister_then409Conflict() throws Exception {
    UserRegisterDto userRegisterDto = UserRegisterDto.builder()
        .email("text@example.com").password("Test1234").build();
    when(userService.register(userRegisterDto, false)).thenThrow(new UserAlreadyExistsException("User already exists"));

    mockMvc.perform(post("/api/v1/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRegisterDto)))
        .andExpect(status().isConflict());

    verify(userService, times(1)).register(userRegisterDto, false);
  }

  @Test
  public void givenInvalidUser_whenRegister_then400BadRequest() throws Exception {
    UserRegisterDto userRegisterDto = UserRegisterDto.builder()
        .email("invalid-Email").password("weak").build();

    mockMvc.perform(post("/api/v1/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRegisterDto)))
        .andExpect(status().isBadRequest());

    verify(userService, times(0)).register(userRegisterDto, false);
  }

}
