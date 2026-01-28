package tn.isg.economics.service;

import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.ProductType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Collection/Streams heavy analytics.
 */
public class ExportAnalyticsService {

    public List<ExportRecord> filter(List<ExportRecord> records, RecordFilter filter) {
        return records.stream().filter(filter::test).toList();
    }

    public DoubleSummaryStatistics volumeStats(List<ExportRecord> records) {
        return records.stream().collect(Collectors.summarizingDouble(ExportRecord::volumeTons));
    }

    public BigDecimal averagePrice(List<ExportRecord> records) {
        if (records.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = records.stream()
                .map(ExportRecord::pricePerTon)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
    }

    public Map<ProductType, BigDecimal> revenueByProduct(List<ExportRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ExportRecord::productType,
                        TreeMap::new,
                        Collectors.mapping(
                                r -> r.pricePerTon().multiply(BigDecimal.valueOf(r.volumeTons())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    public Map<String, BigDecimal> revenueByDestination(List<ExportRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ExportRecord::destination,
                        TreeMap::new,
                        Collectors.mapping(
                                r -> r.pricePerTon().multiply(BigDecimal.valueOf(r.volumeTons())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    public Set<String> destinations(List<ExportRecord> records) {
        return records.stream().map(ExportRecord::destination).collect(Collectors.toSet());
    }

    public Map<Integer, BigDecimal> averagePriceByMonth(List<ExportRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.date().getMonthValue(),
                        TreeMap::new,
                        Collectors.mapping(
                                ExportRecord::pricePerTon,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        prices -> {
                                            if (prices.isEmpty()) return BigDecimal.ZERO;
                                            BigDecimal s = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                                            return s.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);
                                        }
                                )
                        )
                ));
    }

    public Optional<LocalDate> minDate(List<ExportRecord> records) {
        return records.stream().map(ExportRecord::date).min(LocalDate::compareTo);
    }

    public Optional<LocalDate> maxDate(List<ExportRecord> records) {
        return records.stream().map(ExportRecord::date).max(LocalDate::compareTo);
    }

    public <K> Map<K, Long> countBy(List<ExportRecord> records, Function<ExportRecord, K> classifier) {
        return records.stream().collect(Collectors.groupingBy(classifier, Collectors.counting()));
    }
}

