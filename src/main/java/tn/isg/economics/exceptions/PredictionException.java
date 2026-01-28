package tn.isg.economics.exceptions;

public class PredictionException extends EconomicsException {
    public PredictionException(String message) {
        super(message);
    }

    public PredictionException(String message, Throwable cause) {
        super(message, cause);
    }
}