package com.vectorbank.model;

import java.time.LocalDate;

/**
 * Represents a customer profile / account holder in VECTOR Bank.
 */
public class Customer {
    private final int accountNo;
    private final String name;
    private final LocalDate dob;
    private final String pan;

    public Customer(int accountNo, String name, LocalDate dob, String pan) {
        this.accountNo = accountNo;
        this.name = name;
        this.dob = dob;
        this.pan = pan;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getPan() {
        return pan;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "accountNo=" + accountNo +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", pan='" + pan + '\'' +
                '}';
    }
}
