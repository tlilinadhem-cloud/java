package tn.isg.economics.exceptions;

public class ReportGenerationException extends EconomicsException {
    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

