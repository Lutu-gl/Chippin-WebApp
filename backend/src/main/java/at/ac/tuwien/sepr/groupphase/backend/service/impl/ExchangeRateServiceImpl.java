package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ExchangeRate;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExchangeRateRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Map;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private static final String API_KEY = "dde355fb69072600030b5b8d";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/EUR";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestClient client = RestClient.create();
    private final ExchangeRateRepository exchangeRateRepository;


    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public double convertToEuro(double amount, String currency) {
        LOGGER.trace("Converting {} {} to EUR", amount, currency);
        ExchangeRate rate = exchangeRateRepository.findExchangeRateByCurrency(currency.trim());

        if (rate == null) {
            try {
                getExchangeRates();
                rate = exchangeRateRepository.findExchangeRateByCurrency(currency.trim());
            } catch (RuntimeException e) {
                LOGGER.error("Could not fetch Exchange rates because of: {}", e.getMessage());
            }
        }

        if (rate == null) {
            throw new NotFoundException("Could not find ExchangeRate for Currency: " + currency);
        }

        if (rate.getLastUpdated().isBefore(LocalDate.now().minusDays(2))) {
            try {
                getExchangeRates();
            } catch (RuntimeException e) {
                LOGGER.error("Could not fetch Exchange rates because of: {}", e.getMessage());
            }
            rate = exchangeRateRepository.findExchangeRateByCurrency(currency);
        }
        if (rate == null) {
            throw new NotFoundException("Could not find ExchangeRate for Currency: " + currency);
        }

        return amount / rate.getRate();
    }

    public void getExchangeRates() {
        LOGGER.trace("FETCHING Exchange Rates from api");


        JsonNode response = client.get().uri(API_URL).accept(MediaType.APPLICATION_JSON).retrieve().body(JsonNode.class);

        if (response != null && response.get("result").asText().equals("success")) {

            LocalDate lastUpdated = Instant.ofEpochSecond(response.get("time_last_update_unix").asLong())
                .atZone(ZoneId.systemDefault()).toLocalDate();

            JsonNode rates = response.get("conversion_rates");
            if (!response.get("base_code").asText().equals("EUR")) {
                throw new RuntimeException("Currency not found in the fetched exchange rates");
            }

            Iterator<Map.Entry<String, JsonNode>> fields = rates.fields();


            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String currencyCode = field.getKey();
                double rate = field.getValue().asDouble();

                ExchangeRate newExchangeRate = new ExchangeRate();
                newExchangeRate.setCurrency(currencyCode);
                newExchangeRate.setRate(rate);
                newExchangeRate.setLastUpdated(lastUpdated);
                exchangeRateRepository.save(newExchangeRate);

            }


        } else {
            throw new RuntimeException("Failed to fetch exchange rates");
        }
    }


}
