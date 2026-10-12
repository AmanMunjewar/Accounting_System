package com.vectorbank.exception;

/**
 * Thrown when an account number does not exist in the bank's records.
 */
public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(int accountNo) {
        super("Account not found: " + accountNo);
    }
}
