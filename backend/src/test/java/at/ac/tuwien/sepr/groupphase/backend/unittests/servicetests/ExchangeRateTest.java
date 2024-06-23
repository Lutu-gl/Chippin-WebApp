package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.entity.ExchangeRate;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExchangeRateRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple test to see if getting the exchange rate getting works.
 * Do not use this in the default Test setting as the API Usage is limited per month
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRateTest extends BaseTest {
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    @Autowired
    private ExchangeRateService exchangeRateService;

    @Test
    @Rollback
    public void getExchangeRatesFromApi_ThenRatesInPersistence() {

        exchangeRateRepository.deleteAll();

        exchangeRateService.getExchangeRates();

        List<ExchangeRate> list = exchangeRateRepository.findAll();


        assertAll(
            () -> assertNotNull(list),
            () -> assertFalse(list.isEmpty()),
            () -> assertTrue(list.getFirst().getLastUpdated().isAfter(LocalDate.now().minusDays(3))),
            () -> assertNotNull(exchangeRateRepository.findExchangeRateByCurrency("USD")),
            () -> assertNotNull(exchangeRateRepository.findExchangeRateByCurrency("CHF"))
        );

    }

    @Test
    public void givenAmountInDifferentCurrency_WhenConvertToEuro_ConvertsSuccessfully() {
        Double fromEUR = exchangeRateService.convertToEuro(5, "EUR");

        Double fromUSD = exchangeRateService.convertToEuro(5, "USD");

        Double fromCHF = exchangeRateService.convertToEuro(5, "CHF");

        assertAll(
            () -> assertEquals(5, fromEUR),
            () -> assertNotEquals(5, fromUSD),
            () -> assertNotEquals(5, fromCHF)
        );
    }
}
