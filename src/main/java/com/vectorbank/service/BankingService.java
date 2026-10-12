package com.vectorbank.service;

import com.vectorbank.model.Account;
import com.vectorbank.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Banking business operations contract.
 */
public interface BankingService {
    /**
     * Creates a new customer account, hashes the password using BCrypt, and persists.
     */
    int createAccount(String name, LocalDate dob, String pan, BigDecimal initialDeposit, String rawPassword);

    /**
     * Authenticates an account using BCrypt.checkpw.
     */
    Account authenticate(int accountNo, String rawPassword);

    /**
     * Retrieves account information. Checks LRU cache first; caches on cache-miss.
     */
    Account getAccountInfo(int accountNo);

    /**
     * Atomically deposits amount into account and creates passbook record.
     */
    BigDecimal deposit(int accountNo, BigDecimal amount);

    /**
     * Atomically withdraws amount from account with overdraft validation,
     * throwing InsufficientFundsException if balance is insufficient.
     */
    BigDecimal withdraw(int accountNo, BigDecimal amount);

    /**
     * Retrieves passbook transaction history.
     */
    List<Transaction> getPassbook(int accountNo);

    /**
     * Finds top-K largest transactions for an account using min-heap DSA algorithm.
     */
    List<Transaction> getTopKTransactions(int accountNo, int k);
}
