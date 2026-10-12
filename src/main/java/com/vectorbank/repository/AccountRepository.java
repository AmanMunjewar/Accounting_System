package com.vectorbank.repository;

import com.vectorbank.model.Account;
import com.vectorbank.model.Customer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Optional;

/**
 * Data access abstraction for Account and Customer records.
 */
public interface AccountRepository {
    /**
     * Finds an account by its unique account number.
     * Guaranteed O(1) database lookup using indexed primary key.
     */
    Optional<Account> findById(int accountNo);

    /**
     * Checks if an account exists.
     */
    boolean existsById(int accountNo);

    /**
     * Saves a new customer and account in an atomic transaction.
     */
    void save(Customer customer, BigDecimal initialBalance, String passwordHash);

    /**
     * Updates an account's balance within the context of an existing transaction connection.
     */
    void updateBalance(Connection conn, int accountNo, BigDecimal newBalance);
}
