package tn.isg.economics.model;

import java.time.LocalDate;

/**
 * Simple DTO representing an export data point (alternative to ExportRecord).
 */
public record ExportData(
        LocalDate date,
        ProductType productType,
        double pricePerTon,
        double volume,
        String destinationCountry,
        MarketIndicator indicator
) {
    public ExportData {
        if (pricePerTon < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (volume < 0) {
            throw new IllegalArgumentException("Volume cannot be negative");
        }
    }

    public double calculateRevenue() {
        return pricePerTon * volume;
    }
}

