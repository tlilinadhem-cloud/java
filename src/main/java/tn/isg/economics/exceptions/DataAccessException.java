package tn.isg.economics.exceptions;

public class DataAccessException extends EconomicsException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

