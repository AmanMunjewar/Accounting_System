package com.vectorbank.repository;

import com.vectorbank.model.Transaction;

import java.sql.Connection;
import java.util.List;

/**
 * Data access abstraction for Passbook transactions.
 */
public interface TransactionRepository {
    /**
     * Records a new transaction entry in the passbook table.
     */
    void recordTransaction(Connection conn, Transaction transaction);

    /**
     * Retrieves all passbook records for a specific account.
     */
    List<Transaction> findByAccountNo(int accountNo);
}
