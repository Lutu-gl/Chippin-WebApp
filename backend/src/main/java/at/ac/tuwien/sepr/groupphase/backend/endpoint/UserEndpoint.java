package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserEndpoint {
    private final UserService userService;
    private final GroupMapper groupMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public UserEndpoint(UserService userService, GroupMapper groupMapper) {
        this.userService = userService;
        this.groupMapper = groupMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("/groups")
    public Set<GroupDetailDto> getUserGroups() {
        LOGGER.trace("getUserGroups()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return groupMapper.setOfGroupEntityToSetOfGroupDto(userService.getGroupsByUserEmail(authentication.getName()));
    }

    @Secured("ROLE_USER")
    @GetMapping("/groups-with-debt-infos")
    public Set<GroupListDto> getUserGroupsWithDebtInfos() {
        LOGGER.trace("getUserGroupsWithDebtInfos()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getGroupsByUserEmailWithDebtInfos(authentication.getName());
    }
}
