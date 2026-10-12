package com.vectorbank.model;

import java.math.BigDecimal;

/**
 * Represents a bank account with financial balance and authentication details.
 * Balance is maintained using BigDecimal to guarantee exact decimal representation.
 */
public class Account {
    private final int accountNo;
    private final Customer customer;
    private BigDecimal balance;
    private final String passwordHash;

    public Account(int accountNo, Customer customer, BigDecimal balance, String passwordHash) {
        this.accountNo = accountNo;
        this.customer = customer;
        this.balance = balance != null ? balance : BigDecimal.ZERO.setScale(2);
        this.passwordHash = passwordHash;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNo=" + accountNo +
                ", customer=" + (customer != null ? customer.getName() : "N/A") +
                ", balance=" + balance +
                '}';
    }
}
