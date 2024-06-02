package at.ac.tuwien.sepr.groupphase.backend.service;

public interface ExchangeRateService {


    /**
     * Simple function that takes an amount and currency and converts the amount to EUR.
     * The exchange rate is taken live from the ExchangeRate API and if the data is older than two days it will be refreshed
     *
     * @param amount   amount to convert
     * @param currency currency to convert
     * @return the amount in Euro
     */
    double convertToEuro(double amount, String currency);

    /**
     * This function sends a request to the ExchangeRate API to get all rates for EUR.
     * and stores the result in the database. The rates should be refreshed every two days
     */
    void getExchangeRates();
}
