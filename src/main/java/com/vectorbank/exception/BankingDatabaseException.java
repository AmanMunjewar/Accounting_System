package com.vectorbank.exception;

/**
 * Thrown when an underlying JDBC or database error occurs during a transaction.
 */
public class BankingDatabaseException extends BankingException {
    public BankingDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
