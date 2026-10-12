package com.vectorbank.exception;

/**
 * Base unchecked exception for all banking domain errors.
 */
public class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
