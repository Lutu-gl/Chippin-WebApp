package at.ac.tuwien.sepr.groupphase.backend.endpoint;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = GroupEndpoint.BASE_PATH)
public class GroupEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/group";

    private final GroupService groupService;

    @Autowired
    public GroupEndpoint(GroupService groupService) {
        this.groupService = groupService;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    public GroupCreateDto getById(@PathVariable("id") long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        LOGGER.debug("request parameters: {}", id);

        GroupCreateDto res = null;
        try {
            res = groupService.getById(id);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            logClientError(status, "Group not found", e);
            throw new ResponseStatusException(status, e.getMessage(), e);
        }

        return res;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GroupCreateDto createGroup(@RequestBody GroupCreateDto groupCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("POST " + BASE_PATH);
        LOGGER.debug("request parameters: {}", groupCreateDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return groupService.create(groupCreateDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}")
    public GroupCreateDto updateGroup(@PathVariable("id") long id, @RequestBody GroupCreateDto groupCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("PUT " + BASE_PATH + "/{}", id);
        LOGGER.debug("request parameters: {}, {}", groupCreateDto, id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        groupCreateDto.setId(id);   // set the id of the group to update

        return groupService.update(groupCreateDto, authentication.getName());
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }
}
