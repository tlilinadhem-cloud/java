package tn.isg.economics.ai;

import tn.isg.economics.exceptions.ReportGenerationException;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;

import java.util.List;
import java.util.Map;

/**
 * Interface for generating market intelligence reports using LLMs.
 */
public interface ReportGenerator {
    /**
     * Generates a market intelligence report.
     *
     * @param historicalData historical export records
     * @param predictions    recent predictions
     * @param statistics     computed statistics
     * @return report content (markdown/text)
     */
    String generateReport(
            List<ExportRecord> historicalData,
            List<PredictionResult> predictions,
            Map<String, Object> statistics
    ) throws ReportGenerationException;
}
