package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.DebtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/debt")
public class DebtEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    DebtService debtService;

    public DebtEndpoint(DebtService debtService) {
        this.debtService = debtService;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    public DebtGroupDetailDto getByGroupId(@PathVariable("id") long groupId) throws NotFoundException {
        LOGGER.trace("getByGroupId({})", groupId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return debtService.getById(authentication.getName(), groupId);
    }
}
