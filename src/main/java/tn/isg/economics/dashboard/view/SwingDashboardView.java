package tn.isg.economics.dashboard.view;

import tn.isg.economics.dashboard.model.DashboardModel;
import tn.isg.economics.dashboard.model.DashboardModelListener;
import tn.isg.economics.model.PredictionResult;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Swing-based dashboard view (MVC pattern).
 */
public class SwingDashboardView extends JFrame implements DashboardModelListener {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DashboardModel model;

    private final JLabel totalRecordsLabel = new JLabel("Total Records: 0");
    private final JLabel avgPriceLabel = new JLabel("Average Price: -");
    private final JTextArea outputArea = new JTextArea();

    private final JButton filterButton = new JButton("Filter Data");
    private final JButton predictButton = new JButton("Generate Prediction");
    private final JButton reportButton = new JButton("Generate AI Report");
    private final JButton chartRevenueButton = new JButton("Revenue by Product");
    private final JButton chartPriceButton = new JButton("Average Price by Month");

    public SwingDashboardView(DashboardModel model) {
        super("Tunisian Agricultural Export AI Dashboard");
        this.model = model;
        this.model.addListener(this);
        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(totalRecordsLabel);
        topPanel.add(avgPriceLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(filterButton);
        buttonPanel.add(predictButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(chartRevenueButton);
        buttonPanel.add(chartPriceButton);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(topPanel, BorderLayout.NORTH);
        content.add(buttonPanel, BorderLayout.CENTER);
        content.add(scrollPane, BorderLayout.SOUTH);

        setContentPane(content);
    }

    public JButton getFilterButton() {
        return filterButton;
    }

    public JButton getPredictButton() {
        return predictButton;
    }

    public JButton getReportButton() {
        return reportButton;
    }

    public JButton getChartRevenueButton() {
        return chartRevenueButton;
    }

    public JButton getChartPriceButton() {
        return chartPriceButton;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showChart(String title, String chartText) {
        outputArea.setText(title + "\n\n" + chartText);
    }

    public void showReport(String report) {
        outputArea.setText(report);
    }

    public void showStatistics(Map<String, Object> stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("STATISTICS:\n\n");
        stats.forEach((k, v) -> sb.append(String.format("  %s: %s%n", k, v)));
        outputArea.setText(sb.toString());
    }

    public void showPredictions(List<PredictionResult> predictions) {
        StringBuilder sb = new StringBuilder();
        sb.append("PREDICTIONS:\n\n");
        for (PredictionResult pred : predictions) {
            sb.append(String.format("%s → %s: %s TND/ton (conf: %.1f%%) [%s]%n",
                    pred.productType(),
                    pred.destination(),
                    pred.predictedPricePerTon(),
                    pred.confidence() * 100,
                    pred.targetDate().format(DATE_FORMAT)));
        }
        outputArea.setText(sb.toString());
    }

    @Override
    public void onModelChanged() {
        totalRecordsLabel.setText("Total Records: " + model.getRecords().size());
        Object avgPrice = model.getStatistics().getOrDefault("Average Price", "-");
        avgPriceLabel.setText("Average Price: " + avgPrice);
    }
}

