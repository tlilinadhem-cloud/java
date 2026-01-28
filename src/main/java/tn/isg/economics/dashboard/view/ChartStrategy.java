package tn.isg.economics.dashboard.view;

import java.util.Map;

/**
 * Strategy interface for different chart types (Strategy pattern).
 */
public interface ChartStrategy {
    /**
     * Renders a chart as ASCII art.
     *
     * @param data data to visualize
     * @return ASCII chart string
     */
    String render(Map<String, Double> data);
}
