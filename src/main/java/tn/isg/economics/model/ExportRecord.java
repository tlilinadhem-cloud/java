package tn.isg.economics.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Immutable historical export record (DTO).
 *
 * @param date          export date
 * @param productType   product category
 * @param destination   destination country (ISO-like free text)
 * @param volumeTons    exported volume in tons
 * @param pricePerTon   export price per ton
 * @param indicators    attached market indicators at that time
 */
public record ExportRecord(
        LocalDate date,
        ProductType productType,
        String destination,
        double volumeTons,
        BigDecimal pricePerTon,
        Map<MarketIndicator, Double> indicators
) {
}

