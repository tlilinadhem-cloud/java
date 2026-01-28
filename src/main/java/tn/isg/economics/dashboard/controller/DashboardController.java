package tn.isg.economics.dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import tn.isg.economics.ai.Predictor;
import tn.isg.economics.ai.ReportGenerator;
import tn.isg.economics.dashboard.model.DashboardModel;
import tn.isg.economics.dashboard.view.ChartFactory;
import tn.isg.economics.dashboard.view.ChartStrategy;
import tn.isg.economics.dashboard.view.ConsoleDashboardView;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;
import tn.isg.economics.model.ProductType;
import tn.isg.economics.service.ExportAnalyticsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard controller (MVC pattern).
 */
@Slf4j
public class DashboardController {
    private final DashboardModel model;
    private final ConsoleDashboardView view;
    private final ExportAnalyticsService analyticsService;
    private final Predictor predictor;
    private final ReportGenerator reportGenerator;
    private final Scanner scanner;
    private final Deque<Command> commandHistory = new ArrayDeque<>();
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private List<ExportRecord> filteredRecords = new ArrayList<>();

    public DashboardController(
            DashboardModel model,
            ConsoleDashboardView view,
            ExportAnalyticsService analyticsService,
            Predictor predictor,
            ReportGenerator reportGenerator
    ) {
        this.model = model;
        this.view = view;
        this.analyticsService = analyticsService;
        this.predictor = predictor;
        this.reportGenerator = reportGenerator;
        this.scanner = new Scanner(System.in);
        this.filteredRecords = new ArrayList<>(model.getRecords());
    }

    public void run() {
        updateStatistics();
        view.display();

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                int choice = Integer.parseInt(input);
                handleChoice(choice);

                if (choice == 0) {
                    break;
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Please enter a number.");
            } catch (Exception e) {
                log.error("Error handling user input", e);
                view.displayError("An error occurred: " + e.getMessage());
            }
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> showStatistics();
            case 2 -> filterData();
            case 3 -> showCharts();
            case 4 -> generatePrediction();
            case 5 -> generateReport();
            case 6 -> exportData();
            case 0 -> {
                view.displayMessage("Exiting...");
                System.exit(0);
            }
            default -> view.displayError("Invalid choice. Please try again.");
        }
    }

    private void showStatistics() {
        view.displayMessage("=== STATISTICS ===");
        Map<String, Object> stats = computeStatistics();
        stats.forEach((key, value) -> {
            System.out.printf("  %s: %s\n", key, value);
        });
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        view.display();
    }

    private void filterData() {
        view.displayMessage("=== FILTER DATA ===");
        System.out.println("Filter by:");
        System.out.println("1. Product Type");
        System.out.println("2. Date Range");
        System.out.println("3. Destination");
        System.out.println("4. Clear Filters");
        System.out.print("Choice: ");

        try {
            int filterChoice = Integer.parseInt(scanner.nextLine().trim());
            List<ExportRecord> original = new ArrayList<>(filteredRecords);

            switch (filterChoice) {
                case 1 -> filterByProduct();
                case 2 -> filterByDateRange();
                case 3 -> filterByDestination();
                case 4 -> {
                    filteredRecords = new ArrayList<>(model.getRecords());
                    view.displayMessage("Filters cleared.");
                }
                default -> {
                    view.displayError("Invalid choice.");
                    return;
                }
            }

            // Command pattern for undo
            Command filterCommand = new FilterCommand(original, filteredRecords);
            executeCommand(filterCommand);

            updateStatistics();
            view.display();
        } catch (Exception e) {
            view.displayError("Filter error: " + e.getMessage());
        }
    }

    private void filterByProduct() {
        System.out.println("Available products:");
        Arrays.stream(ProductType.values()).forEach(p -> System.out.println("  " + p.ordinal() + ". " + p));
        System.out.print("Enter product number: ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        ProductType product = ProductType.values()[idx];
        filteredRecords = analyticsService.filter(filteredRecords, r -> r.productType() == product);
        view.displayMessage("Filtered by product: " + product);
    }

    private void filterByDateRange() {
        System.out.print("Start date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("End date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine().trim());
        filteredRecords = analyticsService.filter(filteredRecords, r -> 
                !r.date().isBefore(start) && !r.date().isAfter(end));
        view.displayMessage("Filtered by date range: " + start + " to " + end);
    }

    private void filterByDestination() {
        Set<String> destinations = analyticsService.destinations(filteredRecords);
        System.out.println("Available destinations:");
        List<String> destList = new ArrayList<>(destinations);
        for (int i = 0; i < destList.size(); i++) {
            System.out.println("  " + i + ". " + destList.get(i));
        }
        System.out.print("Enter destination number: ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        String dest = destList.get(idx);
        filteredRecords = analyticsService.filter(filteredRecords, r -> r.destination().equals(dest));
        view.displayMessage("Filtered by destination: " + dest);
    }

    private void showCharts() {
        view.displayMessage("=== CHARTS ===");
        System.out.println("1. Revenue by Product (Bar Chart)");
        System.out.println("2. Average Price by Month (Line Chart)");
        System.out.print("Choice: ");

        try {
            int chartChoice = Integer.parseInt(scanner.nextLine().trim());
            Map<String, Double> data;

            if (chartChoice == 1) {
                data = analyticsService.revenueByProduct(filteredRecords).entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey().name(),
                                e -> e.getValue().doubleValue()
                        ));
                ChartStrategy strategy = ChartFactory.createChart(ChartFactory.ChartType.BAR);
                view.displayChart("Revenue by Product", data, strategy);
            } else if (chartChoice == 2) {
                data = analyticsService.averagePriceByMonth(filteredRecords).entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> "Month " + e.getKey(),
                                e -> e.getValue().doubleValue()
                        ));
                ChartStrategy strategy = ChartFactory.createChart(ChartFactory.ChartType.LINE);
                view.displayChart("Average Price by Month", data, strategy);
            } else {
                view.displayError("Invalid choice.");
                return;
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            view.display();
        } catch (Exception e) {
            view.displayError("Chart error: " + e.getMessage());
        }
    }

    private void generatePrediction() {
        view.displayMessage("=== GENERATE PREDICTION ===");
        try {
            System.out.println("Product types:");
            Arrays.stream(ProductType.values()).forEach(p -> System.out.println("  " + p.ordinal() + ". " + p));
            System.out.print("Enter product number: ");
            int prodIdx = Integer.parseInt(scanner.nextLine().trim());
            ProductType product = ProductType.values()[prodIdx];

            System.out.print("Destination: ");
            String destination = scanner.nextLine().trim();

            System.out.print("Target date (YYYY-MM-DD): ");
            LocalDate targetDate = LocalDate.parse(scanner.nextLine().trim());

            PredictionResult result = predictor.predict(
                    filteredRecords.isEmpty() ? model.getRecords() : filteredRecords,
                    targetDate,
                    product,
                    destination
            );

            List<PredictionResult> currentPreds = new ArrayList<>(model.getPredictions());
            currentPreds.add(result);
            model.setPredictions(currentPreds);

            view.displayMessage(String.format(
                    "Prediction generated: %s TND/ton (confidence: %.1f%%)",
                    result.predictedPricePerTon(),
                    result.confidence() * 100
            ));

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            view.display();
        } catch (Exception e) {
            view.displayError("Prediction error: " + e.getMessage());
        }
    }

    private void generateReport() {
        view.displayMessage("=== GENERATING REPORT ===");
        try {
            String report = reportGenerator.generateReport(
                    filteredRecords.isEmpty() ? model.getRecords() : filteredRecords,
                    model.getPredictions(),
                    computeStatistics()
            );

            System.out.println(report);
            System.out.print("\nSave to file? (y/n): ");
            String save = scanner.nextLine().trim().toLowerCase();
            if (save.equals("y")) {
                System.out.print("Filename (without extension): ");
                String filename = scanner.nextLine().trim();
                Path reportPath = Path.of(filename + ".md");
                Files.writeString(reportPath, report);
                view.displayMessage("Report saved to: " + reportPath.toAbsolutePath());
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            view.display();
        } catch (Exception e) {
            view.displayError("Report generation error: " + e.getMessage());
        }
    }

    private void exportData() {
        view.displayMessage("=== EXPORT DATA ===");
        System.out.println("1. Export to CSV");
        System.out.println("2. Export to JSON");
        System.out.print("Choice: ");

        try {
            int exportChoice = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Filename (without extension): ");
            String filename = scanner.nextLine().trim();

            List<ExportRecord> dataToExport = filteredRecords.isEmpty() ? model.getRecords() : filteredRecords;

            if (exportChoice == 1) {
                exportToCSV(dataToExport, filename + ".csv");
            } else if (exportChoice == 2) {
                exportToJSON(dataToExport, filename + ".json");
            } else {
                view.displayError("Invalid choice.");
                return;
            }

            view.displayMessage("Data exported successfully.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            view.display();
        } catch (Exception e) {
            view.displayError("Export error: " + e.getMessage());
        }
    }

    private void exportToCSV(List<ExportRecord> records, String filename) throws IOException {
        Path path = Path.of(filename);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("Date,ProductType,Destination,VolumeTons,PricePerTon");
            for (ExportRecord record : records) {
                writer.printf("%s,%s,%s,%.2f,%s\n",
                        record.date(),
                        record.productType(),
                        record.destination(),
                        record.volumeTons(),
                        record.pricePerTon());
            }
        }
    }

    private void exportToJSON(List<ExportRecord> records, String filename) throws IOException {
        Path path = Path.of(filename);
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < records.size(); i++) {
            ExportRecord r = records.get(i);
            json.append("  {\n");
            json.append("    \"date\": \"").append(r.date()).append("\",\n");
            json.append("    \"productType\": \"").append(r.productType()).append("\",\n");
            json.append("    \"destination\": \"").append(r.destination()).append("\",\n");
            json.append("    \"volumeTons\": ").append(r.volumeTons()).append(",\n");
            json.append("    \"pricePerTon\": ").append(r.pricePerTon()).append("\n");
            json.append("  }");
            if (i < records.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        Files.writeString(path, json.toString());
    }

    private Map<String, Object> computeStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        if (filteredRecords.isEmpty()) {
            filteredRecords = new ArrayList<>(model.getRecords());
        }

        stats.put("Total Records", filteredRecords.size());
        if (!filteredRecords.isEmpty()) {
            stats.put("Average Price", analyticsService.averagePrice(filteredRecords));
            var volumeStats = analyticsService.volumeStats(filteredRecords);
            stats.put("Total Volume (tons)", volumeStats.getSum());
            stats.put("Avg Volume (tons)", volumeStats.getAverage());
            stats.put("Min Volume (tons)", volumeStats.getMin());
            stats.put("Max Volume (tons)", volumeStats.getMax());
        }
        return stats;
    }

    private void updateStatistics() {
        model.setStatistics(computeStatistics());
    }

    private void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
        undoStack.clear(); // Clear redo stack
    }

    public void undo() {
        if (!commandHistory.isEmpty()) {
            Command cmd = commandHistory.pop();
            cmd.undo();
            undoStack.push(cmd);
            updateStatistics();
            view.display();
        }
    }

    private class FilterCommand implements Command {
        private final List<ExportRecord> oldState;
        private final List<ExportRecord> newState;

        public FilterCommand(List<ExportRecord> oldState, List<ExportRecord> newState) {
            this.oldState = new ArrayList<>(oldState);
            this.newState = new ArrayList<>(newState);
        }

        @Override
        public void execute() {
            filteredRecords = new ArrayList<>(newState);
            updateStatistics();
        }

        @Override
        public void undo() {
            filteredRecords = new ArrayList<>(oldState);
            updateStatistics();
        }

        @Override
        public String getDescription() {
            return "Filter data";
        }
    }
}
