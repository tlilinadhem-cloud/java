package tn.isg.economics.exceptions;

/**
 * Base runtime exception for the economic intelligence system.
 */
public class EconomicsException extends RuntimeException {

    public EconomicsException(String message) {
        super(message);
    }

    public EconomicsException(String message, Throwable cause) {
        super(message, cause);
    }
}