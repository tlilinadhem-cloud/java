package tn.isg.economics.ai;

import lombok.extern.slf4j.Slf4j;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import tn.isg.economics.exceptions.ReportGenerationException;
import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LangChain4j-based report generator.
 * Uses LLM (Ollama local or OpenAI) to generate market intelligence reports.
 */
@Slf4j
public class LangChain4jReportGenerator implements ReportGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ChatLanguageModel chatModel;

    public LangChain4jReportGenerator() {
        // Ollama local model as primary LLM (http://localhost:11434)
        this.chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2") // you can change to another local model name
                .build();
    }

    @Override
    public String generateReport(
            List<ExportRecord> historicalData,
            List<PredictionResult> predictions,
            Map<String, Object> statistics
    ) {
        try {
            // Build structured prompt data
            StringBuilder promptData = new StringBuilder();
            promptData.append("# Tunisian Agricultural Export Market Intelligence Report\n\n");
            promptData.append("## Historical Data Summary\n");
            promptData.append(String.format("- Total Records: %d\n", historicalData.size()));

            if (!historicalData.isEmpty()) {
                LocalDate minDate = historicalData.stream()
                        .map(ExportRecord::date)
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                LocalDate maxDate = historicalData.stream()
                        .map(ExportRecord::date)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                promptData.append(String.format("- Date Range: %s to %s\n", 
                        minDate.format(DATE_FORMAT), maxDate.format(DATE_FORMAT)));
            }

            promptData.append("\n## Recent Predictions\n");
            if (predictions.isEmpty()) {
                promptData.append("- No predictions available\n");
            } else {
                for (PredictionResult pred : predictions) {
                    promptData.append(String.format(
                            "- %s: %s TND/ton (confidence: %.1f%%) for %s on %s\n",
                            pred.productType(),
                            pred.predictedPricePerTon(),
                            pred.confidence() * 100,
                            pred.destination(),
                            pred.targetDate().format(DATE_FORMAT)
                    ));
                }
            }

            promptData.append("\n## Key Statistics\n");
            statistics.forEach((key, value) -> {
                promptData.append(String.format("- %s: %s\n", key, value));
            });

            String report;
            try {
                // Use Ollama via LangChain4j as the primary report generator
                String llmResponse = chatModel.generate(
                        promptData.toString() +
                                "\n\nYou are an economic analyst. Generate a structured, concise market intelligence report in Markdown about Tunisian agricultural exports based on the data above."
                );
                report = llmResponse;
            } catch (Exception llmError) {
                // Fallback to template-based report if Ollama is not available
                log.warn("Ollama/LangChain4j call failed, using template report instead: {}", llmError.getMessage());
                report = generateTemplateReport(promptData.toString(), historicalData, predictions, statistics);
            }

            log.info("Generated market intelligence report ({} chars)", report.length());
            return report;

        } catch (Exception e) {
            throw new ReportGenerationException("Failed to generate report: " + e.getMessage(), e);
        }
    }

    private String generateTemplateReport(
            String promptData,
            List<ExportRecord> historicalData,
            List<PredictionResult> predictions,
            Map<String, Object> statistics
    ) {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("TUNISIAN AGRICULTURAL EXPORT MARKET INTELLIGENCE REPORT\n");
        report.append("Generated: ").append(LocalDate.now().format(DATE_FORMAT)).append("\n");
        report.append("=".repeat(80)).append("\n\n");

        report.append("## Executive Summary\n");
        report.append("This report analyzes Tunisian agricultural export trends and provides ");
        report.append("price predictions based on historical data and AI models.\n\n");

        report.append("## Historical Data Analysis\n");
        if (historicalData.isEmpty()) {
            report.append("No historical data available.\n\n");
        } else {
            report.append(String.format("Total export records analyzed: %d\n", historicalData.size()));
            
            Map<String, Long> byProduct = historicalData.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.productType().name(),
                            Collectors.counting()
                    ));
            report.append("\nRecords by product:\n");
            byProduct.forEach((product, count) -> 
                    report.append(String.format("  - %s: %d records\n", product, count))
            );
        }

        report.append("\n## Price Predictions\n");
        if (predictions.isEmpty()) {
            report.append("No predictions available at this time.\n\n");
        } else {
            report.append("Recent AI-generated price predictions:\n\n");
            for (PredictionResult pred : predictions) {
                report.append(String.format("**%s** to %s\n", pred.productType(), pred.destination()));
                report.append(String.format("  - Predicted Price: %s TND/ton\n", pred.predictedPricePerTon()));
                report.append(String.format("  - Target Date: %s\n", pred.targetDate().format(DATE_FORMAT)));
                report.append(String.format("  - Confidence: %.1f%%\n", pred.confidence() * 100));
                report.append(String.format("  - Model: %s\n", pred.modelName()));
                report.append(String.format("  - Status: %s\n\n", pred.status()));
            }
        }

        report.append("## Market Statistics\n");
        statistics.forEach((key, value) -> {
            report.append(String.format("- **%s**: %s\n", key, value));
        });

        report.append("\n## Recommendations\n");
        report.append("Based on the analysis:\n");
        report.append("1. Monitor price trends closely, especially for products with high volatility.\n");
        report.append("2. Consider diversifying export destinations to reduce market risk.\n");
        report.append("3. Use AI predictions as guidance but always validate with current market conditions.\n");

        report.append("\n").append("=".repeat(80)).append("\n");
        report.append("End of Report\n");
        report.append("=".repeat(80)).append("\n");

        return report.toString();
    }
}
