package at.ac.tuwien.sepr.groupphase.backend.unittests.endpointtests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserEndpointTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GroupMapper groupMapper;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser("user@example.com")
    public void givenUserHasGroups_whenGetUserGroups_then200OK() throws Exception {
        Set<GroupEntity> groupEntities = new HashSet<>();
        groupEntities.add(GroupEntity.builder().id(1L).groupName("Group 1").build());
        groupEntities.add(GroupEntity.builder().id(2L).groupName("Group 2").build());


        Set<GroupDetailDto> groupDetailDtos = new HashSet<>();
        groupDetailDtos.add(new GroupDetailDto(1L, "Group 1"));
        groupDetailDtos.add(new GroupDetailDto(2L, "Group 2"));


        when(userService.getGroupsByUserEmail("user@example.com")).thenReturn(groupEntities);
        when(groupMapper.setOfGroupEntityToSetOfGroupDto(groupEntities)).thenReturn(groupDetailDtos);

        mockMvc.perform(get("/api/v1/users/groups")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@example.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(groupDetailDtos)));
    }

    @Test
    @WithMockUser("user@example.com")
    public void givenUserHasNoGroups_whenGetUserGroups_then200OKAndEmpty() throws Exception {
        when(userService.getGroupsByUserEmail("user@example.com")).thenReturn(Collections.emptySet());
        when(groupMapper.setOfGroupEntityToSetOfGroupDto(Collections.emptySet())).thenReturn(Collections.emptySet());

        mockMvc.perform(get("/api/v1/users/groups")
                //.header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@example.com", ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
}
