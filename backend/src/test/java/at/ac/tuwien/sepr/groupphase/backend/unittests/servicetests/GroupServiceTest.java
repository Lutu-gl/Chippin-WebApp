package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.GroupServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.GroupValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private PantryRepository pantryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private GroupValidator groupValidator;

    @InjectMocks
    private GroupServiceImpl groupService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateGroupSuccess() throws Exception {
        // Mock-Konfigurationen
        GroupEntity mockGroupEntity = new GroupEntity();
        ;
        when(groupMapper.groupCreateDtoToGroupEntity(any(GroupCreateDto.class))).thenReturn(mockGroupEntity);
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(mockGroupEntity);
        when(pantryRepository.save(any())).thenReturn(null);
        when(groupMapper.groupEntityToGroupCreateDto(any(GroupEntity.class)))
            .thenReturn(GroupCreateDto.builder()
                .groupName("NewTestGroup")
                .members(Arrays.stream(new String[]{"user1@example.com", "user2@example.com"}).collect(Collectors.toSet()))
                .build());

        when(groupValidator.validateFriendsWithEveryone(any(GroupEntity.class))).thenReturn(true);

        GroupCreateDto groupCreateDto = GroupCreateDto.builder()
            .groupName("NewTestGroup")
            .members(Arrays.stream(new String[]{"user1@example.com", "user2@example.com"}).collect(Collectors.toSet()))
            .build();

        // Execution
        GroupCreateDto result = groupService.create(groupCreateDto, "user1@example.com");

        // Verification
        assertNotNull(result);
        assertEquals("NewTestGroup", result.getGroupName());
        assertEquals(2, result.getMembers().size());
        assertTrue(result.getMembers().contains("user1@example.com"));
        assertTrue(result.getMembers().contains("user2@example.com"));
        verify(groupValidator, times(1)).validateForCreation(groupCreateDto, "user1@example.com");
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateGroupSuccess() throws Exception {
        GroupEntity existingGroupEntity = new GroupEntity();
        existingGroupEntity.setId(1L); // assuming this ID exists in the database

        GroupCreateDto updateDto = GroupCreateDto.builder()
            .groupName("UpdatedTestGroup")
            .members(Arrays.stream(new String[]{"user1@example.com", "user2@example.com"}).collect(Collectors.toSet()))
            .build();

        when(groupMapper.groupCreateDtoToGroupEntity(updateDto)).thenReturn(existingGroupEntity);
        when(groupRepository.save(existingGroupEntity)).thenReturn(existingGroupEntity);
        when(groupMapper.groupEntityToGroupCreateDto(existingGroupEntity)).thenReturn(updateDto);
        when(groupValidator.validateFriendsWithEveryone(any(GroupEntity.class))).thenReturn(true);

        // Act
        GroupCreateDto result = groupService.update(updateDto, "user1@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("UpdatedTestGroup", result.getGroupName());
        assertEquals(2, result.getMembers().size());
        assertTrue(result.getMembers().contains("user1@example.com"));
        assertTrue(result.getMembers().contains("user2@example.com"));
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateGroupValidationException() throws Exception {
        GroupCreateDto groupCreateDto = GroupCreateDto.builder().build();
        doThrow(new ValidationException("Invalid data", null)).when(groupValidator).validateForCreation(any(), anyString());

        assertThrows(ValidationException.class, () -> {
            groupService.create(groupCreateDto, "owner@example.com");
        });
    }
}
