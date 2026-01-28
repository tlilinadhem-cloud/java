package tn.isg.economics.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Prediction output DTO.
 */
public record PredictionResult(
        LocalDate targetDate,
        ProductType productType,
        String destination,
        BigDecimal predictedPricePerTon,
        double confidence,
        PredictionStatus status,
        String modelName
) {
}

