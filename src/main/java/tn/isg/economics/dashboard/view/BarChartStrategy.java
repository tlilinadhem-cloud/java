package tn.isg.economics.dashboard.view;

import java.util.Map;

/**
 * Bar chart implementation (Strategy pattern).
 */
public class BarChartStrategy implements ChartStrategy {
    private static final int MAX_WIDTH = 50;
    private static final char BAR_CHAR = '█';

    @Override
    public String render(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            return "No data to display\n";
        }

        double maxValue = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        if (maxValue == 0) maxValue = 1.0;

        StringBuilder sb = new StringBuilder();
        sb.append("\nBar Chart:\n");
        sb.append("-".repeat(60)).append("\n");

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();
            int barLength = (int) ((value / maxValue) * MAX_WIDTH);

            sb.append(String.format("%-20s |%s %.2f\n",
                    label.length() > 20 ? label.substring(0, 17) + "..." : label,
                    String.valueOf(BAR_CHAR).repeat(barLength),
                    value));
        }

        sb.append("-".repeat(60)).append("\n");
        return sb.toString();
    }
}
