package com.vectorbank.exception;

import java.math.BigDecimal;

/**
 * Thrown when an account withdrawal or debit exceeds the available balance.
 */
public class InsufficientFundsException extends BankingException {
    public InsufficientFundsException(BigDecimal attempted, BigDecimal available) {
        super(String.format("Insufficient funds: Attempted to withdraw $%.2f, but available balance is only $%.2f.", 
                attempted, available));
    }
}
