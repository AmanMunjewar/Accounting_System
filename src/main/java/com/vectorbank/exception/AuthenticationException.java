package com.vectorbank.exception;

/**
 * Thrown when an account authentication fails due to an invalid password.
 */
public class AuthenticationException extends BankingException {
    public AuthenticationException(String message) {
        super(message);
    }
}
