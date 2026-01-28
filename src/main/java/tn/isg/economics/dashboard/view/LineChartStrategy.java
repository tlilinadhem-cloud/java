package tn.isg.economics.dashboard.view;

import java.util.Map;
import java.util.TreeMap;

/**
 * Line chart implementation (Strategy pattern).
 */
public class LineChartStrategy implements ChartStrategy {
    private static final int HEIGHT = 10;
    private static final int WIDTH = 50;
    private static final char POINT_CHAR = '●';
    private static final char LINE_CHAR = '─';
    private static final char AXIS_CHAR = '│';

    @Override
    public String render(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            return "No data to display\n";
        }

        // Sort by key for time-series
        Map<String, Double> sorted = new TreeMap<>(data);
        double maxValue = sorted.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double minValue = sorted.values().stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double range = maxValue - minValue;
        if (range == 0) range = 1.0;

        StringBuilder sb = new StringBuilder();
        sb.append("\nLine Chart:\n");
        sb.append(String.format("Max: %.2f, Min: %.2f\n", maxValue, minValue));
        sb.append(" ".repeat(20)).append("↑\n");

        // Render from top to bottom
        for (int row = HEIGHT - 1; row >= 0; row--) {
            double yValue = minValue + (range * row / (HEIGHT - 1));
            sb.append(String.format("%10.2f %s", yValue, AXIS_CHAR));

            int prevX = -1;
            for (Map.Entry<String, Double> entry : sorted.entrySet()) {
                double value = entry.getValue();
                int x = (int) ((value - minValue) / range * (WIDTH - 1));

                if (prevX >= 0 && Math.abs(x - prevX) > 1) {
                    // Draw line between points
                    for (int i = Math.min(prevX, x) + 1; i < Math.max(prevX, x); i++) {
                        sb.append(LINE_CHAR);
                    }
                }

                if (Math.abs((value - minValue) / range - (double) row / (HEIGHT - 1)) < 0.1) {
                    sb.append(POINT_CHAR);
                } else {
                    sb.append(' ');
                }
                prevX = x;
            }
            sb.append("\n");
        }

        sb.append(" ".repeat(12)).append("└").append("─".repeat(WIDTH)).append("\n");
        sb.append(" ".repeat(12));
        int idx = 0;
        for (String key : sorted.keySet()) {
            if (idx % 5 == 0) {
                sb.append(key.length() > 3 ? key.substring(0, 3) : key);
            }
            sb.append(" ");
            idx++;
        }
        sb.append("\n");

        return sb.toString();
    }
}
