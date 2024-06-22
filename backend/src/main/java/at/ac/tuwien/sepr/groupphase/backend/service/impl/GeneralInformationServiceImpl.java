package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GeneralInformationDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.GeneralInformationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
@RequiredArgsConstructor
public class GeneralInformationServiceImpl implements GeneralInformationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public GeneralInformationDto getGeneralInformation() {
        LOGGER.trace("getGeneralInformation()");
        long amountUsers = userRepository.count();
        long amountExpenses = expenseRepository.count();
        double totalAmountExpenses = expenseRepository.getTotalAmountOfExpenses();
        long amountShoppingLists = expenseRepository.count();

        return GeneralInformationDto.builder()
            .amountUsers(amountUsers)
            .amountExpenses(amountExpenses)
            .expensesSum(totalAmountExpenses)
            .amountShoppingLists(amountShoppingLists)
            .build();
    }
}
