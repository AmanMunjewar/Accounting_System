package com.vectorbank.repository;

import com.vectorbank.config.DatabaseConfig;
import com.vectorbank.exception.BankingDatabaseException;
import com.vectorbank.model.Account;
import com.vectorbank.model.Customer;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * JDBC implementation of AccountRepository using secure prepared statements,
 * indexed single-row queries, and try-with-resources.
 */
public class JdbcAccountRepository implements AccountRepository {
    private final DatabaseConfig config;

    public JdbcAccountRepository(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Optional<Account> findById(int accountNo) {
        String sql = """
            SELECT c.accountNo, c.name, c.dob, c.pan, c.balance, a.password
            FROM client c
            LEFT JOIN account a ON c.accountNo = a.accountNo
            WHERE c.accountNo = ?
        """;

        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    Date dobSql = rs.getDate("dob");
                    LocalDate dob = dobSql != null ? dobSql.toLocalDate() : LocalDate.now();
                    String pan = rs.getString("pan");
                    BigDecimal balance = rs.getBigDecimal("balance");
                    String passwordHash = rs.getString("password");

                    Customer customer = new Customer(accountNo, name, dob, pan);
                    Account account = new Account(accountNo, customer, balance, passwordHash);
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Error querying account by ID: " + accountNo, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(int accountNo) {
        String sql = "SELECT 1 FROM client WHERE accountNo = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Error checking account existence for: " + accountNo, e);
        }
    }

    @Override
    public void save(Customer customer, BigDecimal initialBalance, String passwordHash) {
        String insertClientSql = "INSERT INTO client (accountNo, name, dob, pan, balance) VALUES (?, ?, ?, ?, ?)";
        String insertAccountSql = "INSERT INTO account (accountNo, password) VALUES (?, ?)";

        try (Connection conn = config.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmtClient = conn.prepareStatement(insertClientSql)) {
                    stmtClient.setInt(1, customer.getAccountNo());
                    stmtClient.setString(2, customer.getName());
                    stmtClient.setDate(3, Date.valueOf(customer.getDob()));
                    stmtClient.setString(4, customer.getPan());
                    stmtClient.setBigDecimal(5, initialBalance);
                    stmtClient.executeUpdate();
                }

                try (PreparedStatement stmtAccount = conn.prepareStatement(insertAccountSql)) {
                    stmtAccount.setInt(1, customer.getAccountNo());
                    stmtAccount.setString(2, passwordHash);
                    stmtAccount.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new BankingDatabaseException("Transaction rolled back while creating account: " + customer.getAccountNo(), e);
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Database error saving account: " + customer.getAccountNo(), e);
        }
    }

    @Override
    public void updateBalance(Connection conn, int accountNo, BigDecimal newBalance) {
        String sql = "UPDATE client SET balance = ? WHERE accountNo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountNo);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No client record updated for accountNo: " + accountNo);
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Failed to update balance for account: " + accountNo, e);
        }
    }
}
