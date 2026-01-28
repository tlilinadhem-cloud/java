package tn.isg.economics.dashboard.model;

import lombok.Getter;
import lombok.Setter;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Dashboard data model (MVC pattern).
 */
@Getter
@Setter
public class DashboardModel {
    private List<ExportRecord> records = new ArrayList<>();
    private List<PredictionResult> predictions = new ArrayList<>();
    private Map<String, Object> statistics = Map.of();
    private List<DashboardModelListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(DashboardModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DashboardModelListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners() {
        listeners.forEach(DashboardModelListener::onModelChanged);
    }

    public void setRecords(List<ExportRecord> records) {
        this.records = new ArrayList<>(records);
        notifyListeners();
    }

    public void setPredictions(List<PredictionResult> predictions) {
        this.predictions = new ArrayList<>(predictions);
        notifyListeners();
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = Map.copyOf(statistics);
        notifyListeners();
    }
}
