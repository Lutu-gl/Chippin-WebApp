package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.budget.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.BudgetMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.BudgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/group")
public class BudgetEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BudgetService budgetService;
    private final BudgetMapper budgetMapper;

    @Autowired
    public BudgetEndpoint(BudgetService budgetService, BudgetMapper budgetMapper) {
        this.budgetService = budgetService;
        this.budgetMapper = budgetMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("/{groupId}/budgets")
    public List<BudgetDto> getAllBudgets(@PathVariable long groupId) {
        LOGGER.trace("GET /api/v1/group/{}/budgets", groupId);
        List<Budget> budgets = budgetService.findAllByGroupId(groupId);
        return budgetMapper.budgetListToDtoList(budgets);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{groupId}/budget/{budgetId}")
    public BudgetDto getBudgetById(@PathVariable long groupId, @PathVariable long budgetId) {
        LOGGER.trace("GET /api/v1/group/{}/budget/{}", groupId, budgetId);
        Budget budget = budgetService.findByGroupIdAndBudgetId(groupId, budgetId);
        return budgetMapper.budgetToDto(budget);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{groupId}/budget")
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetDto createBudget(@PathVariable long groupId, @Valid @RequestBody BudgetCreateDto budgetCreateDto) throws ConflictException {
        LOGGER.trace("POST /api/v1/group/{}/budget", groupId);
        return budgetMapper.budgetToDto(budgetService.createBudget(budgetCreateDto, groupId));
    }

    @Secured("ROLE_USER")
    @PutMapping("/{groupId}/budget/{budgetId}")
    public BudgetDto updateBudget(@PathVariable long groupId, @PathVariable long budgetId, @Valid @RequestBody BudgetDto budgetDto) throws ConflictException {
        LOGGER.trace("PUT /api/v1/group/{}/budget/{}", groupId, budgetId);
        return budgetMapper.budgetToDto(budgetService.updateBudget(budgetDto, groupId));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{groupId}/budget/{budgetId}")
    public void deleteBudget(@PathVariable long groupId, @PathVariable long budgetId) {
        LOGGER.trace("DELETE /api/v1/group/{}/budget/{}", groupId, budgetId);
        budgetService.deleteBudget(groupId, budgetId);
    }
}
