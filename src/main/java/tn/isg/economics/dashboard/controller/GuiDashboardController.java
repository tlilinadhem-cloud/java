package tn.isg.economics.dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import tn.isg.economics.ai.Predictor;
import tn.isg.economics.ai.ReportGenerator;
import tn.isg.economics.dashboard.model.DashboardModel;
import tn.isg.economics.dashboard.view.ChartFactory;
import tn.isg.economics.dashboard.view.ChartStrategy;
import tn.isg.economics.dashboard.view.SwingDashboardView;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;
import tn.isg.economics.model.ProductType;
import tn.isg.economics.service.ExportAnalyticsService;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GUI controller for the Swing dashboard (MVC pattern).
 */
@Slf4j
public class GuiDashboardController {

    private final DashboardModel model;
    private final SwingDashboardView view;
    private final ExportAnalyticsService analyticsService;
    private final Predictor predictor;
    private final ReportGenerator reportGenerator;

    private List<ExportRecord> filteredRecords = new ArrayList<>();

    public GuiDashboardController(
            DashboardModel model,
            SwingDashboardView view,
            ExportAnalyticsService analyticsService,
            Predictor predictor,
            ReportGenerator reportGenerator
    ) {
        this.model = model;
        this.view = view;
        this.analyticsService = analyticsService;
        this.predictor = predictor;
        this.reportGenerator = reportGenerator;
        this.filteredRecords = new ArrayList<>(model.getRecords());
        wireActions();
        updateStatistics();
    }

    private void wireActions() {
        view.getFilterButton().addActionListener(e -> onFilter());
        view.getPredictButton().addActionListener(e -> onPredict());
        view.getReportButton().addActionListener(e -> onGenerateReport());
        view.getChartRevenueButton().addActionListener(e -> onChartRevenue());
        view.getChartPriceButton().addActionListener(e -> onChartPrice());
    }

    private void onFilter() {
        try {
            ProductType product = (ProductType) JOptionPane.showInputDialog(
                    view,
                    "Select product type:",
                    "Filter by Product",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    ProductType.values(),
                    ProductType.OLIVE_OIL
            );
            if (product == null) {
                return;
            }

            String destination = JOptionPane.showInputDialog(
                    view,
                    "Destination country (leave empty for all):",
                    "Filter by Destination",
                    JOptionPane.QUESTION_MESSAGE
            );

            filteredRecords = analyticsService.filter(model.getRecords(), r ->
                    r.productType() == product &&
                            (destination == null || destination.isBlank() || r.destination().equalsIgnoreCase(destination.trim()))
            );

            updateStatistics();
            view.showStatistics(model.getStatistics());
            log.info("Applied filter: product={}, destination={}", product, destination);
        } catch (Exception ex) {
            log.error("Filter error", ex);
            view.showError("Filter error: " + ex.getMessage());
        }
    }

    private void onPredict() {
        try {
            ProductType product = (ProductType) JOptionPane.showInputDialog(
                    view,
                    "Select product type:",
                    "Prediction - Product",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    ProductType.values(),
                    ProductType.OLIVE_OIL
            );
            if (product == null) return;

            String destination = JOptionPane.showInputDialog(
                    view,
                    "Destination country:",
                    "Prediction - Destination",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (destination == null || destination.isBlank()) return;

            String dateStr = JOptionPane.showInputDialog(
                    view,
                    "Target date (YYYY-MM-DD):",
                    "Prediction - Date",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (dateStr == null || dateStr.isBlank()) return;

            LocalDate targetDate = LocalDate.parse(dateStr.trim());

            PredictionResult result = predictor.predict(
                    filteredRecords.isEmpty() ? model.getRecords() : filteredRecords,
                    targetDate,
                    product,
                    destination.trim()
            );

            List<PredictionResult> preds = new ArrayList<>(model.getPredictions());
            preds.add(result);
            model.setPredictions(preds);

            view.showPredictions(preds);
            view.showMessage(String.format(
                    "Prediction: %s TND/ton (conf: %.1f%%)",
                    result.predictedPricePerTon(),
                    result.confidence() * 100
            ));
        } catch (Exception ex) {
            log.error("Prediction error", ex);
            view.showError("Prediction error: " + ex.getMessage());
        }
    }

    private void onGenerateReport() {
        try {
            String report = reportGenerator.generateReport(
                    filteredRecords.isEmpty() ? model.getRecords() : filteredRecords,
                    model.getPredictions(),
                    model.getStatistics()
            );
            view.showReport(report);
        } catch (Exception ex) {
            log.error("Report generation error", ex);
            view.showError("Report generation error: " + ex.getMessage());
        }
    }

    private void onChartRevenue() {
        try {
            Map<String, Double> data = analyticsService.revenueByProduct(
                            filteredRecords.isEmpty() ? model.getRecords() : filteredRecords)
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().name(),
                            e -> e.getValue().doubleValue()
                    ));
            ChartStrategy strategy = ChartFactory.createChart(ChartFactory.ChartType.BAR);
            String chartText = strategy.render(data);
            view.showChart("Revenue by Product", chartText);
        } catch (Exception ex) {
            log.error("Chart error", ex);
            view.showError("Chart error: " + ex.getMessage());
        }
    }

    private void onChartPrice() {
        try {
            Map<String, Double> data = analyticsService.averagePriceByMonth(
                            filteredRecords.isEmpty() ? model.getRecords() : filteredRecords)
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> "Month " + e.getKey(),
                            e -> e.getValue().doubleValue()
                    ));
            ChartStrategy strategy = ChartFactory.createChart(ChartFactory.ChartType.LINE);
            String chartText = strategy.render(data);
            view.showChart("Average Price by Month", chartText);
        } catch (Exception ex) {
            log.error("Chart error", ex);
            view.showError("Chart error: " + ex.getMessage());
        }
    }

    private void updateStatistics() {
        var stats = new java.util.LinkedHashMap<String, Object>();
        List<ExportRecord> data = filteredRecords.isEmpty() ? model.getRecords() : filteredRecords;
        stats.put("Total Records", data.size());
        if (!data.isEmpty()) {
            stats.put("Average Price", analyticsService.averagePrice(data));
            var volumeStats = analyticsService.volumeStats(data);
            stats.put("Total Volume (tons)", volumeStats.getSum());
            stats.put("Avg Volume (tons)", volumeStats.getAverage());
            stats.put("Min Volume (tons)", volumeStats.getMin());
            stats.put("Max Volume (tons)", volumeStats.getMax());
        }
        model.setStatistics(stats);
    }
}

