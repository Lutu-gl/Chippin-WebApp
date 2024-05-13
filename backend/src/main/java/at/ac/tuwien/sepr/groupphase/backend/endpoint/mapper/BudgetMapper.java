package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.BudgetDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Budget;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BudgetMapper {


    BudgetDto budgetToDto(Budget budget);

    Budget budgetCreateDtoToBudget(BudgetCreateDto budgetCreateDto);

    List<BudgetDto> budgetListToDtoList(List<Budget> budgets);
}
