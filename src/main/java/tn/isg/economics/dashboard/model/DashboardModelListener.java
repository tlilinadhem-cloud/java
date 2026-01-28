package tn.isg.economics.dashboard.model;

/**
 * Observer interface for dashboard model changes (Observer pattern).
 */
@FunctionalInterface
public interface DashboardModelListener {
    void onModelChanged();
}
