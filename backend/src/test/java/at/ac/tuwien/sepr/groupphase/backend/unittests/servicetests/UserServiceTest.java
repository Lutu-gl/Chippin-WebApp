package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest extends BaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserDetailService userService;

    private UserRegisterDto userRegisterDto;
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userRegisterDto = UserRegisterDto.builder()
            .email("test@example.com")
            .password("Test1234")
            .build();

        userService = new CustomUserDetailService(userRepository, passwordEncoder, jwtTokenizer);
    }

    @Test
    @Transactional
    @Rollback
    public void givenValidUser_whenRegister_thenNoException() throws UserAlreadyExistsException {
        var createdUser = ApplicationUser.builder().email("test@example.com").password("encodedPassword").id(4L).admin(false).build();
        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(null).thenReturn(createdUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");


        userService.register(userRegisterDto, false);

        verify(userRepository, times(1)).save(any(ApplicationUser.class));
    }

    @Test
    @Transactional
    @Rollback
    public void givenDuplicateEmail_whenRegister_thenUserAlreadyExistsException() {
        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(new ApplicationUser());

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(userRegisterDto, false));

        verify(userRepository, times(0)).save(any(ApplicationUser.class));
    }

    @Test
    @Transactional
    @Rollback
    void testGetGroupsByUserEmail() {
        String userEmail = "test@example.com";
        Set<GroupEntity> expectedGroups = new HashSet<>();
        expectedGroups.add(new GroupEntity(1L, "Group 1", null));
        expectedGroups.add(new GroupEntity(2L, "Group 2", null));

        when(userRepository.findGroupsByUserEmail(userEmail)).thenReturn(expectedGroups);

        Set<GroupEntity> resultGroups = userService.getGroupsByUserEmail(userEmail);

        assertEquals(expectedGroups, resultGroups, "The returned groups should match the expected groups");
        verify(userRepository).findGroupsByUserEmail(userEmail);
    }
}
