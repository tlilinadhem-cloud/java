package tn.isg.economics.model;

public class PricePrediction {

    private String productName;
    private double predictedPrice;
    private String currency;

    public PricePrediction(String productName, double predictedPrice, String currency) {
        this.productName = productName;
        this.predictedPrice = predictedPrice;
        this.currency = currency;
    }

    public String getProductName() {
        return productName;
    }

    public double getPredictedPrice() {
        return predictedPrice;
    }

    public String getCurrency() {
        return currency;
    }
}
