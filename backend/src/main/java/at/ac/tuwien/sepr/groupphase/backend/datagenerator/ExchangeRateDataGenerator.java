package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ExchangeRate;

import at.ac.tuwien.sepr.groupphase.backend.repository.ExchangeRateRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class ExchangeRateDataGenerator implements DataGenerator {


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    ExchangeRateRepository exchangeRateRepository;

    private static final LocalDate lastUpdated = LocalDate.MAX;
    //This data has been accessed and is not being updated for testing purposes (01.06.2024)
    private static Map<String, Double> rates = new HashMap<>();


    @Override
    @Transactional
    public void generateData() {
        rates.put("EUR", 1.0);
        rates.put("AED", 3.9826);
        rates.put("AFN", 77.6785);
        rates.put("ALL", 101.0194);
        rates.put("AMD", 420.211);
        rates.put("ANG", 1.9411);
        rates.put("AOA", 937.5375);
        rates.put("ARS", 937.7654);
        rates.put("AUD", 1.6316);
        rates.put("AWG", 1.9411);
        rates.put("AZN", 1.8427);
        rates.put("BAM", 1.9558);
        rates.put("BBD", 2.1689);
        rates.put("BDT", 127.3694);
        rates.put("BGN", 1.9558);
        rates.put("BHD", 0.4077);
        rates.put("BIF", 3092.1401);
        rates.put("BMD", 1.0844);
        rates.put("BND", 1.4657);
        rates.put("BOB", 7.4834);
        rates.put("BRL", 5.6423);
        rates.put("BSD", 1.0844);
        rates.put("BTN", 90.4898);
        rates.put("BWP", 14.9038);
        rates.put("BYN", 3.5123);
        rates.put("BZD", 2.1689);
        rates.put("CAD", 1.479);
        rates.put("CDF", 3011.5909);
        rates.put("CHF", 0.9798);
        rates.put("CLP", 992.5475);
        rates.put("CNY", 7.8683);
        rates.put("COP", 4190.723);
        rates.put("CRC", 563.4761);
        rates.put("CUP", 26.0264);
        rates.put("CVE", 110.265);
        rates.put("CZK", 24.704);
        rates.put("DJF", 192.7269);
        rates.put("DKK", 7.4582);
        rates.put("DOP", 63.8389);
        rates.put("DZD", 145.6071);
        rates.put("EGP", 51.1875);
        rates.put("ERN", 16.2665);
        rates.put("ETB", 62.2236);
        rates.put("FJD", 2.4312);
        rates.put("FKP", 0.8519);
        rates.put("FOK", 7.4582);
        rates.put("GBP", 0.8519);
        rates.put("GEL", 3.029);
        rates.put("GGP", 0.8519);
        rates.put("GHS", 16.4218);
        rates.put("GIP", 0.8519);
        rates.put("GMD", 70.1818);
        rates.put("GNF", 9316.6382);
        rates.put("GTQ", 8.3917);
        rates.put("GYD", 225.972);
        rates.put("HKD", 8.4813);
        rates.put("HNL", 26.6972);
        rates.put("HRK", 7.5345);
        rates.put("HTG", 143.347);
        rates.put("HUF", 389.1577);
        rates.put("IDR", 17625.1743);
        rates.put("ILS", 4.0273);
        rates.put("IMP", 0.8519);
        rates.put("INR", 90.49);
        rates.put("IQD", 1415.7051);
        rates.put("IRR", 45856.5781);
        rates.put("ISK", 149.111);
        rates.put("JEP", 0.8519);
        rates.put("JMD", 167.7229);
        rates.put("JOD", 0.7689);
        rates.put("JPY", 170.4137);
        rates.put("KES", 141.6233);
        rates.put("KGS", 95.2257);
        rates.put("KHR", 4417.0);
        rates.put("KID", 1.6316);
        rates.put("KMF", 491.9678);
        rates.put("KRW", 1499.6286);
        rates.put("KWD", 0.3325);
        rates.put("KYD", 0.9037);
        rates.put("KZT", 485.2356);
        rates.put("LAK", 23517.5776);
        rates.put("LBP", 97056.9547);
        rates.put("LKR", 326.3247);
        rates.put("LRD", 209.3477);
        rates.put("LSL", 20.3622);
        rates.put("LYD", 5.2278);
        rates.put("MAD", 10.7913);
        rates.put("MDL", 19.1502);
        rates.put("MGA", 4802.4894);
        rates.put("MKD", 61.5572);
        rates.put("MMK", 3190.1664);
        rates.put("MNT", 3661.2077);
        rates.put("MOP", 8.7357);
        rates.put("MRU", 42.4494);
        rates.put("MUR", 49.7803);
        rates.put("MVR", 16.6812);
        rates.put("MWK", 1895.6473);
        rates.put("MXN", 18.4445);
        rates.put("MYR", 5.1038);
        rates.put("MZN", 69.2944);
        rates.put("NAD", 20.3622);
        rates.put("NGN", 1546.8647);
        rates.put("NIO", 39.8064);
        rates.put("NOK", 11.3888);
        rates.put("NPR", 144.7837);
        rates.put("NZD", 1.767);
        rates.put("OMR", 0.417);
        rates.put("PAB", 1.0844);
        rates.put("PEN", 4.0561);
        rates.put("PGK", 4.1466);
        rates.put("PHP", 63.5162);
        rates.put("PKR", 301.76);
        rates.put("PLN", 4.2741);
        rates.put("PYG", 8099.0849);
        rates.put("QAR", 3.9473);
        rates.put("RON", 4.9764);
        rates.put("RSD", 117.1091);
        rates.put("RUB", 97.8538);
        rates.put("RWF", 1455.1995);
        rates.put("SAR", 4.0666);
        rates.put("SBD", 9.1277);
        rates.put("SCR", 15.5238);
        rates.put("SDG", 482.9082);
        rates.put("SEK", 11.4271);
        rates.put("SGD", 1.4657);
        rates.put("SHP", 0.8519);
        rates.put("SLE", 24.4043);
        rates.put("SLL", 24404.2321);
        rates.put("SOS", 616.8994);
        rates.put("SRD", 35.0481);
        rates.put("SSP", 1957.458);
        rates.put("STN", 24.5);
        rates.put("SYP", 13873.0388);
        rates.put("SZL", 20.3622);
        rates.put("THB", 39.9072);
        rates.put("TJS", 11.742);
        rates.put("TMT", 3.796);
        rates.put("TND", 3.3713);
        rates.put("TOP", 2.5233);
        rates.put("TRY", 34.9777);
        rates.put("TTD", 7.7248);
        rates.put("TVD", 1.6316);
        rates.put("TWD", 35.2151);
        rates.put("TZS", 2821.1498);
        rates.put("UAH", 43.9172);
        rates.put("UGX", 4130.1964);
        rates.put("USD", 1.0844);
        rates.put("UYU", 41.6917);
        rates.put("UZS", 13653.5286);
        rates.put("VES", 39.6332);
        rates.put("VND", 27614.1965);
        rates.put("VUV", 128.7672);
        rates.put("WST", 2.931);
        rates.put("XAF", 655.957);
        rates.put("XCD", 2.928);
        rates.put("XDR", 0.8187);
        rates.put("XOF", 655.957);
        rates.put("XPF", 119.332);
        rates.put("YER", 270.3094);
        rates.put("ZAR", 20.3513);
        rates.put("ZMW", 28.5705);
        rates.put("ZWL", 14.7726);
        ExchangeRate saveRate;

        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            saveRate = ExchangeRate.builder().rate(entry.getValue()).currency(entry.getKey()).lastUpdated(lastUpdated).build();

            exchangeRateRepository.save(saveRate);
        }

    }


    @Override
    public void cleanData() {
        LOGGER.trace("Deleting all exchangeRates");
        exchangeRateRepository.deleteAll();
    }
}
