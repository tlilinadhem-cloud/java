package tn.isg.economics.dashboard.view;

/**
 * Factory for creating chart strategies (Factory pattern).
 */
public class ChartFactory {
    public enum ChartType {
        BAR, LINE
    }

    public static ChartStrategy createChart(ChartType type) {
        return switch (type) {
            case BAR -> new BarChartStrategy();
            case LINE -> new LineChartStrategy();
        };
    }
}
