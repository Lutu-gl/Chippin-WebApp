package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/expense")
public class ExpenseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ExpenseService expenseService;

    public ExpenseEndpoint(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ExpenseDetailDto getById(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.getById(id, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExpenseCreateDto createExpense(@Valid @RequestBody ExpenseCreateDto expenseCreateDto) throws ValidationException, ConflictException { // TODO check if this correct
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.createExpense(expenseCreateDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public ExpenseCreateDto updateExpense(@PathVariable(name = "id") Long expenseId, @Valid @RequestBody ExpenseCreateDto expenseCreateDto) throws ValidationException, ConflictException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.updateExpense(expenseId, expenseCreateDto, authentication.getName());
    }

}
