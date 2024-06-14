package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/expense")
public class ExpenseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExpenseService expenseService;
    private final ObjectMapper objectMapper;

    public ExpenseEndpoint(ExpenseService expenseService, ObjectMapper objectMapper) {
        this.expenseService = expenseService;
        this.objectMapper = objectMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ExpenseDetailDto getById(@PathVariable long id) {
        LOGGER.trace("getById({})", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.getById(id, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bill/{id}")
    public ResponseEntity<byte[]> getBill(@PathVariable long id) {
        LOGGER.trace("getBill({})", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.getBill(id, authentication.getName());
    }

    private ExpenseCreateDto getExpenseCreateDtoFromParams(String name, Category category, double amount, String payerEmail, long groupId, String participantsJson, MultipartFile bill) throws ValidationException {
        ExpenseCreateDto expenseCreateDto;
        try {
            expenseCreateDto = ExpenseCreateDto.builder().name(name).category(category).amount(amount).payerEmail(payerEmail)
                .groupId(groupId).participants(objectMapper.readValue(participantsJson, new TypeReference<>() {})).bill(bill).build();
            // Validator
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<ExpenseCreateDto>> violations = validator.validate(expenseCreateDto);
            if (!violations.isEmpty()) {
                List<String> errors = new ArrayList<>();
                for (ConstraintViolation<ExpenseCreateDto> constraintViolation : violations) {
                    errors.add(constraintViolation.getMessage());
                }
                throw new ValidationException("Invalid data", errors);
            }
            return expenseCreateDto;
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid participants JSON", List.of(e.getMessage()));
        }
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExpenseCreateDto createExpense(
        @RequestParam(name = "name") String name,
        @RequestParam(name = "category", required = false) Category category,
        @RequestParam(name = "amount") double amount,
        @RequestParam(name = "payerEmail") String payerEmail,
        @RequestParam(name = "groupId") long groupId,
        @RequestParam(name = "participants") String participantsJson,
        @RequestParam(name = "bill", required = false) MultipartFile bill
    ) throws ValidationException, ConflictException {
        ExpenseCreateDto expenseCreateDto = getExpenseCreateDtoFromParams(name, category, amount, payerEmail, groupId, participantsJson, bill);
        LOGGER.trace("createExpense({})", expenseCreateDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.createExpense(expenseCreateDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public ExpenseCreateDto updateExpense(
        @PathVariable(name = "id") Long expenseId,
        @RequestParam(name = "name") String name,
        @RequestParam(name = "category", required = false) Category category,
        @RequestParam(name = "amount") double amount,
        @RequestParam(name = "payerEmail") String payerEmail,
        @RequestParam(name = "groupId") long groupId,
        @RequestParam(name = "participants") String participantsJson,
        @RequestParam(name = "bill", required = false) MultipartFile bill
    ) throws ValidationException, ConflictException {
        ExpenseCreateDto expenseCreateDto = getExpenseCreateDtoFromParams(name, category, amount, payerEmail, groupId, participantsJson, bill);
        LOGGER.trace("updateExpense({})", expenseCreateDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.updateExpense(expenseId, expenseCreateDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable(name = "id") Long expenseId) throws ConflictException {
        LOGGER.trace("deleteExpense({})", expenseId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        expenseService.deleteExpense(expenseId, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/recover/{id}")
    public ExpenseCreateDto recoverExpense(@PathVariable(name = "id") Long expenseId) throws ConflictException {
        LOGGER.trace("recoverExpense({})", expenseId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return expenseService.recoverExpense(expenseId, authentication.getName());
    }
}
