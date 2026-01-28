package tn.isg.economics.dashboard.view;

import lombok.extern.slf4j.Slf4j;
import tn.isg.economics.dashboard.model.DashboardModel;
import tn.isg.economics.dashboard.model.DashboardModelListener;
import tn.isg.economics.model.PredictionResult;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Console-based dashboard view (MVC pattern).
 */
@Slf4j
public class ConsoleDashboardView implements DashboardModelListener {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DashboardModel model;

    public ConsoleDashboardView(DashboardModel model) {
        this.model = model;
        model.addListener(this);
    }

    @Override
    public void onModelChanged() {
        display();
    }

    public void display() {
        clearScreen();
        displayHeader();
        displayStatistics();
        displayPredictions();
        displayMenu();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void displayHeader() {
        System.out.println("=".repeat(80));
        System.out.println("TUNISIAN AGRICULTURAL EXPORT AI SYSTEM - DASHBOARD");
        System.out.println("=".repeat(80));
        System.out.println();
    }

    private void displayStatistics() {
        System.out.println("## STATISTICS");
        System.out.println("-".repeat(80));
        
        if (model.getRecords().isEmpty()) {
            System.out.println("No data available.");
        } else {
            System.out.printf("Total Records: %d\n", model.getRecords().size());
            
            if (!model.getStatistics().isEmpty()) {
                model.getStatistics().forEach((key, value) -> {
                    System.out.printf("  %s: %s\n", key, value);
                });
            }
        }
        System.out.println();
    }

    private void displayPredictions() {
        System.out.println("## RECENT PREDICTIONS");
        System.out.println("-".repeat(80));
        
        List<PredictionResult> preds = model.getPredictions();
        if (preds.isEmpty()) {
            System.out.println("No predictions available.");
        } else {
            for (PredictionResult pred : preds) {
                System.out.printf("  %s → %s: %s TND/ton (confidence: %.1f%%) [%s]\n",
                        pred.productType(),
                        pred.destination(),
                        pred.predictedPricePerTon(),
                        pred.confidence() * 100,
                        pred.targetDate().format(DATE_FORMAT));
            }
        }
        System.out.println();
    }

    private void displayMenu() {
        System.out.println("## MENU");
        System.out.println("-".repeat(80));
        System.out.println("1. View Statistics");
        System.out.println("2. Filter Data");
        System.out.println("3. View Charts");
        System.out.println("4. Generate Prediction");
        System.out.println("5. Generate Report");
        System.out.println("6. Export Data");
        System.out.println("0. Exit");
        System.out.print("\nEnter choice: ");
    }

    public void displayChart(String title, Map<String, Double> data, ChartStrategy strategy) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(title);
        System.out.println("=".repeat(80));
        System.out.println(strategy.render(data));
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message + "\n");
    }

    public void displayError(String error) {
        System.err.println("\nERROR: " + error + "\n");
    }
}
