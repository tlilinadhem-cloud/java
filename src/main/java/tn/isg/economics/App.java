package tn.isg.economics;

import lombok.extern.slf4j.Slf4j;
import tn.isg.economics.ai.DJLPredictor;
import tn.isg.economics.ai.LangChain4jReportGenerator;
import tn.isg.economics.ai.Predictor;
import tn.isg.economics.ai.ReportGenerator;
import tn.isg.economics.dashboard.controller.GuiDashboardController;
import tn.isg.economics.dashboard.model.DashboardModel;
import tn.isg.economics.dashboard.view.SwingDashboardView;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.MarketIndicator;
import tn.isg.economics.model.ProductType;
import tn.isg.economics.repository.ExportRecordRepository;
import tn.isg.economics.repository.InMemoryExportRecordRepository;
import tn.isg.economics.service.ExportAnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main application entry point.
 * Tunisian Agricultural Export Price Prediction and Market Intelligence System using AI
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        log.info("Starting Tunisian Agricultural Export AI System (GUI)...");

        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize components
                ExportRecordRepository repository = new InMemoryExportRecordRepository();
                ExportAnalyticsService analyticsService = new ExportAnalyticsService();

                // Load sample data
                List<ExportRecord> sampleData = generateSampleData();
                repository.saveAll(sampleData);
                log.info("Loaded {} sample export records", sampleData.size());

                // Initialize AI components
                Predictor predictor = new DJLPredictor(); // Uses BaseAIModel as fallback
                ReportGenerator reportGenerator = new LangChain4jReportGenerator();

                // Initialize dashboard (MVC)
                DashboardModel model = new DashboardModel();
                model.setRecords(repository.findAll());

                SwingDashboardView view = new SwingDashboardView(model);
                new GuiDashboardController(
                        model,
                        view,
                        analyticsService,
                        predictor,
                        reportGenerator
                );

                view.setVisible(true);
                log.info("GUI dashboard initialized.");

            } catch (Exception e) {
                log.error("Fatal error starting GUI application", e);
                JOptionPane.showMessageDialog(null,
                        "Fatal error: " + e.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    /**
     * Generates sample Tunisian agricultural export data for demonstration.
     */
    private static List<ExportRecord> generateSampleData() {
        List<ExportRecord> records = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusMonths(24);
        
        ProductType[] products = ProductType.values();
        String[] destinations = {"France", "Italy", "Spain", "Germany", "UK", "USA", "Canada", "Libya", "Algeria"};
        
        // Generate records for each month
        for (int month = 0; month < 24; month++) {
            LocalDate date = startDate.plusMonths(month);
            
            for (ProductType product : products) {
                // Each product exported to 2-4 destinations per month
                int numDestinations = 2 + (int)(Math.random() * 3);
                
                for (int d = 0; d < numDestinations; d++) {
                    String destination = destinations[(int)(Math.random() * destinations.length)];
                    
                    // Base prices per product (in TND per ton)
                    double basePrice = switch (product) {
                        case OLIVE_OIL -> 8000 + (Math.random() * 2000);
                        case DATES -> 3000 + (Math.random() * 1000);
                        case CITRUS -> 1500 + (Math.random() * 500);
                        case TOMATO -> 1200 + (Math.random() * 400);
                        case ALMONDS -> 12000 + (Math.random() * 3000);
                        case HARISSA -> 5000 + (Math.random() * 2000);
                    };
                    
                    // Add seasonal variation
                    double seasonalFactor = 1.0 + 0.2 * Math.sin(month * Math.PI / 6);
                    double price = basePrice * seasonalFactor;
                    
                    // Volume in tons
                    double volume = 10 + (Math.random() * 90);
                    
                    // Market indicators
                    Map<MarketIndicator, Double> indicators = new HashMap<>();
                    indicators.put(MarketIndicator.USD_TND, 3.0 + (Math.random() * 0.5));
                    indicators.put(MarketIndicator.EUR_TND, 3.2 + (Math.random() * 0.4));
                    indicators.put(MarketIndicator.BRENT_OIL, 70 + (Math.random() * 30));
                    indicators.put(MarketIndicator.INFLATION_RATE, 5.0 + (Math.random() * 3.0));
                    
                    records.add(new ExportRecord(
                            date,
                            product,
                            destination,
                            volume,
                            BigDecimal.valueOf(price).setScale(2, java.math.RoundingMode.HALF_UP),
                            indicators
                    ));
                }
            }
        }
        
        return records;
    }
}