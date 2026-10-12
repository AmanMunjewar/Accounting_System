package com.vectorbank.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Immutable financial transaction entry stored in the passbook table.
 */
public class Transaction {
    private final int transactionId;
    private final int accountNo;
    private final Timestamp timestamp;
    private final BigDecimal deposit;
    private final BigDecimal withdraw;
    private final BigDecimal balanceAfter;

    public Transaction(int transactionId, int accountNo, Timestamp timestamp, 
                       BigDecimal deposit, BigDecimal withdraw, BigDecimal balanceAfter) {
        this.transactionId = transactionId;
        this.accountNo = accountNo;
        this.timestamp = timestamp;
        this.deposit = deposit;
        this.withdraw = withdraw;
        this.balanceAfter = balanceAfter;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    /**
     * Returns the absolute transaction amount (either deposit or withdraw).
     */
    public BigDecimal getAmount() {
        if (deposit != null && deposit.compareTo(BigDecimal.ZERO) > 0) {
            return deposit;
        }
        if (withdraw != null && withdraw.compareTo(BigDecimal.ZERO) > 0) {
            return withdraw;
        }
        return BigDecimal.ZERO;
    }

    public boolean isDeposit() {
        return deposit != null && deposit.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + transactionId +
                ", accountNo=" + accountNo +
                ", time=" + timestamp +
                ", deposit=" + deposit +
                ", withdraw=" + withdraw +
                ", balanceAfter=" + balanceAfter +
                '}';
    }
}
