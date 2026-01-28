package tn.isg.economics.ai;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;
import tn.isg.economics.model.PredictionStatus;
import tn.isg.economics.model.ProductType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Getter
public class BaseAIModel implements Predictor {

    private final String modelName;

    public BaseAIModel() {
        this("Baseline-AI-Model");
    }

    public BaseAIModel(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public PredictionResult predict(
            List<ExportRecord> historicalData,
            LocalDate targetDate,
            ProductType productType,
            String destination
    ) {
        if (historicalData == null || historicalData.isEmpty()) {
            throw new IllegalArgumentException("No historical data provided");
        }

        // Filter by product and destination
        List<ExportRecord> relevant = historicalData.stream()
                .filter(r -> r.productType() == productType)
                .filter(r -> Objects.equals(r.destination(), destination))
                .sorted((a, b) -> b.date().compareTo(a.date())) // newest first
                .limit(12) // last 12 records
                .collect(Collectors.toList());

        if (relevant.isEmpty()) {
            // Fallback: use all records for this product
            relevant = historicalData.stream()
                    .filter(r -> r.productType() == productType)
                    .sorted((a, b) -> b.date().compareTo(a.date()))
                    .limit(12)
                    .collect(Collectors.toList());
        }

        if (relevant.isEmpty()) {
            throw new IllegalArgumentException("No relevant historical data found");
        }

        // Simple moving average
        BigDecimal avgPrice = relevant.stream()
                .map(ExportRecord::pricePerTon)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(relevant.size()), 2, RoundingMode.HALF_UP);

        // Simple trend: compare last 3 vs previous 3
        if (relevant.size() >= 6) {
            BigDecimal recentAvg = relevant.subList(0, 3).stream()
                    .map(ExportRecord::pricePerTon)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

            BigDecimal olderAvg = relevant.subList(3, 6).stream()
                    .map(ExportRecord::pricePerTon)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

            BigDecimal trend = recentAvg.subtract(olderAvg).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            avgPrice = avgPrice.add(trend);
        }

        // Confidence based on data quality
        double confidence = Math.min(0.7, 0.3 + (relevant.size() / 20.0));

        log.info("Baseline prediction for {} to {} on {}: {} TND/ton (conf={})",
                productType, destination, targetDate, avgPrice, confidence);

        return new PredictionResult(
                targetDate,
                productType,
                destination,
                avgPrice.max(BigDecimal.ZERO),
                confidence,
                PredictionStatus.SUCCESS,
                modelName
        );
    }
}