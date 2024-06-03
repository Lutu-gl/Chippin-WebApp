package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;

import java.time.LocalDate;

/**
 * This entity stores the exchange rates of currencies to EUR.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "exchange_rate")
public class ExchangeRate {
    @Id
    private String currency;

    private double rate;

    private LocalDate lastUpdated;


}
