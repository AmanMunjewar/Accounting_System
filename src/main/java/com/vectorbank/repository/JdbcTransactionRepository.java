package com.vectorbank.repository;

import com.vectorbank.config.DatabaseConfig;
import com.vectorbank.exception.BankingDatabaseException;
import com.vectorbank.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of TransactionRepository.
 * Handles recording passbook audit entries and retrieving transaction history.
 */
public class JdbcTransactionRepository implements TransactionRepository {
    private final DatabaseConfig config;

    public JdbcTransactionRepository(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public void recordTransaction(Connection conn, Transaction transaction) {
        String sql = "INSERT INTO passbook (accountNo, timestamp, deposit, withdraw, balance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getAccountNo());
            stmt.setTimestamp(2, transaction.getTimestamp());
            stmt.setBigDecimal(3, transaction.getDeposit());
            stmt.setBigDecimal(4, transaction.getWithdraw());
            stmt.setBigDecimal(5, transaction.getBalanceAfter());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BankingDatabaseException("Failed to insert passbook record for account: " + transaction.getAccountNo(), e);
        }
    }

    @Override
    public List<Transaction> findByAccountNo(int accountNo) {
        String sql = "SELECT transactionId, accountNo, timestamp, deposit, withdraw, balance FROM passbook WHERE accountNo = ? ORDER BY timestamp ASC, transactionId ASC";
        List<Transaction> list = new ArrayList<>();

        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int txId = rs.getInt("transactionId");
                    int acc = rs.getInt("accountNo");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    BigDecimal deposit = rs.getBigDecimal("deposit");
                    BigDecimal withdraw = rs.getBigDecimal("withdraw");
                    BigDecimal bal = rs.getBigDecimal("balance");

                    list.add(new Transaction(txId, acc, ts, deposit, withdraw, bal));
                }
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Failed to fetch passbook for account: " + accountNo, e);
        }
        return list;
    }
}
