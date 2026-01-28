package tn.isg.economics.ai;

import tn.isg.economics.model.ExportRecord;
import tn.isg.economics.model.PredictionResult;

import java.util.List;

/**
 * Interface for price prediction models.
 */
public interface Predictor {
    /**
     * Predicts future price for a product.
     *
     * @param historicalData historical export records
     * @param targetDate     date to predict for
     * @param productType    product type
     * @param destination    destination country
     * @return prediction result
     */
    PredictionResult predict(
            List<ExportRecord> historicalData,
            java.time.LocalDate targetDate,
            tn.isg.economics.model.ProductType productType,
            String destination
    );

    /**
     * @return model name/identifier
     */
    String getModelName();
}
