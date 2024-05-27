package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    public void setUp() {
        ApplicationUser user = ApplicationUser.builder().id(1L).email("test@email.com").build();
        var authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), null, null);

        // Set the security context to a user with the given email
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));
    }


    @Test
    public void givenValidUserId_whenHasCorrectId_thenReturnTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(ApplicationUser.builder().id(1L).email("test@email.com").build()));
        Long id = 1L;

        boolean hasCorrectId = securityService.hasCorrectId(id);
        assertThat(hasCorrectId).isTrue();
    }

    @Test
    public void givenInvalidUserId_whenHasCorrectId_thenReturnFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Long id = 1L;

        boolean hasCorrectId = securityService.hasCorrectId(id);
        assertThat(hasCorrectId).isFalse();
    }

    @Test
    public void givenValidGroupId_whenIsGroupMember_thenReturnTrue() {
        when(userRepository.findByEmail("test@email.com"))
            .thenReturn(ApplicationUser.builder()
                .id(1L)
                .groups(Set.of(GroupEntity.builder().id(5L).build()))
                .build());

        Long groupId = 5L;

        boolean isGroupMember = securityService.isGroupMember(groupId);
        assertThat(isGroupMember).isTrue();
    }

    @Test
    public void givenInvalidGroupId_whenIsGroupMember_thenReturnFalse() {
        when(userRepository.findByEmail("test@email.com"))
            .thenReturn(ApplicationUser.builder()
                .id(1L)
                .groups(Set.of(GroupEntity.builder().id(5L).build()))
                .build());

        Long groupId = 6L;

        boolean isGroupMember = securityService.isGroupMember(groupId);
        assertThat(isGroupMember).isFalse();

    }

    // Tests for canAccessShoppingList

    @Test
    public void givenValidShoppingListIdWithPrincipalIsOwner_whenCanAccessShoppingList_thenReturnTrue() {
        when(userRepository.findByEmail("test@email.com"))
            .thenReturn(ApplicationUser.builder()
                .id(1L)
                .groups(Set.of())
                .build());
        when(shoppingListRepository.findById(1L))
            .thenReturn(Optional.of(ShoppingList.builder().id(1L).owner(ApplicationUser.builder().id(1L).build()).build()));

        Long shoppingListId = 1L;
        boolean canAccessShoppingList = securityService.canAccessShoppingList(shoppingListId);
        assertThat(canAccessShoppingList).isTrue();
    }

    @Test
    public void givenValidShoppingListIdWithPrincipalIsNotOwnerButInGroup_whenCanAccessShoppingList_thenReturnTrue() {
        when(userRepository.findByEmail("test@email.com"))
            .thenReturn(ApplicationUser.builder()
                .id(1L)
                .groups(Set.of(GroupEntity.builder().id(5L).build()))
                .build());
        when(shoppingListRepository.findById(1L))
            .thenReturn(
                Optional.of(ShoppingList.builder().id(1L).owner(ApplicationUser.builder().id(2L).build()).group(GroupEntity.builder().id(5L).build()).build()));
        Long shoppingListId = 1L;
        boolean canAccessShoppingList = securityService.canAccessShoppingList(shoppingListId);
        assertThat(canAccessShoppingList).isTrue();
    }

    @Test
    public void givenValidShoppingListIdWithPrincipalIsNotOwnerAndNotInGroup_whenCanAccessShoppingList_thenReturnFalse() {
        when(userRepository.findByEmail("test@email.com"))
            .thenReturn(ApplicationUser.builder()
                .id(1L)
                .groups(Set.of())
                .build());
        when(shoppingListRepository.findById(1L))
            .thenReturn(
                Optional.of(ShoppingList.builder().id(1L).owner(ApplicationUser.builder().id(2L).build()).group(GroupEntity.builder().id(5L).build()).build()));
        Long shoppingListId = 1L;
        boolean canAccessShoppingList = securityService.canAccessShoppingList(shoppingListId);
        assertThat(canAccessShoppingList).isFalse();
    }

}