package com.vectorbank.service;

import com.vectorbank.config.DatabaseConfig;
import com.vectorbank.dsa.LRUCache;
import com.vectorbank.dsa.TopKTransactions;
import com.vectorbank.exception.AccountNotFoundException;
import com.vectorbank.exception.AuthenticationException;
import com.vectorbank.exception.BankingDatabaseException;
import com.vectorbank.exception.InsufficientFundsException;
import com.vectorbank.model.Account;
import com.vectorbank.model.Customer;
import com.vectorbank.model.Transaction;
import com.vectorbank.repository.AccountRepository;
import com.vectorbank.repository.TransactionRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Production implementation of BankingService.
 * Handles atomic transactional boundaries, overdraft prevention, BCrypt hashing,
 * LRU cache invalidation/caching, and DSA operations.
 */
public class BankingServiceImpl implements BankingService {
    private static final int DEFAULT_CACHE_CAPACITY = 100;
    private static final int BCRYPT_WORK_FACTOR = 12;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final DatabaseConfig databaseConfig;
    private final LRUCache<Integer, Account> accountCache;
    private final Random random = new Random();

    public BankingServiceImpl(AccountRepository accountRepository, 
                              TransactionRepository transactionRepository, 
                              DatabaseConfig databaseConfig) {
        this(accountRepository, transactionRepository, databaseConfig, new LRUCache<>(DEFAULT_CACHE_CAPACITY));
    }

    public BankingServiceImpl(AccountRepository accountRepository, 
                              TransactionRepository transactionRepository, 
                              DatabaseConfig databaseConfig, 
                              LRUCache<Integer, Account> accountCache) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.databaseConfig = databaseConfig;
        this.accountCache = accountCache;
    }

    @Override
    public int createAccount(String name, LocalDate dob, String pan, BigDecimal initialDeposit, String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }

        // Generate a 4-digit unique account number between 1000 and 9999
        int accountNo;
        int attempts = 0;
        do {
            accountNo = 1000 + random.nextInt(9000);
            attempts++;
            if (attempts > 50) {
                accountNo = 10000 + random.nextInt(90000);
                break;
            }
        } while (accountRepository.existsById(accountNo));

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
        BigDecimal normalizedDeposit = initialDeposit.setScale(2, RoundingMode.HALF_UP);
        Customer customer = new Customer(accountNo, name, dob, pan);

        accountRepository.save(customer, normalizedDeposit, hashedPassword);

        Account account = new Account(accountNo, customer, normalizedDeposit, hashedPassword);
        accountCache.put(accountNo, account);

        return accountNo;
    }

    @Override
    public Account authenticate(int accountNo, String rawPassword) {
        Account account = getAccountInfo(accountNo);

        if (account.getPasswordHash() == null || !BCrypt.checkpw(rawPassword, account.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials for account number: " + accountNo);
        }

        return account;
    }

    @Override
    public Account getAccountInfo(int accountNo) {
        return accountCache.get(accountNo).orElseGet(() -> {
            Account fetched = accountRepository.findById(accountNo)
                    .orElseThrow(() -> new AccountNotFoundException(accountNo));
            accountCache.put(accountNo, fetched);
            return fetched;
        });
    }

    @Override
    public BigDecimal deposit(int accountNo, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be strictly greater than zero.");
        }
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

        try (Connection conn = databaseConfig.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                Account account = accountRepository.findById(accountNo)
                        .orElseThrow(() -> new AccountNotFoundException(accountNo));

                BigDecimal newBalance = account.getBalance().add(normalizedAmount).setScale(2, RoundingMode.HALF_UP);
                accountRepository.updateBalance(conn, accountNo, newBalance);

                Transaction tx = new Transaction(
                        0,
                        accountNo,
                        new Timestamp(System.currentTimeMillis()),
                        normalizedAmount,
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        newBalance
                );
                transactionRepository.recordTransaction(conn, tx);

                conn.commit();

                // Update cache
                account.setBalance(newBalance);
                accountCache.put(accountNo, account);

                return newBalance;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new BankingDatabaseException("Deposit transaction failed and was rolled back.", e);
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Database connection error during deposit for account: " + accountNo, e);
        }
    }

    @Override
    public BigDecimal withdraw(int accountNo, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly greater than zero.");
        }
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

        try (Connection conn = databaseConfig.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                Account account = accountRepository.findById(accountNo)
                        .orElseThrow(() -> new AccountNotFoundException(accountNo));

                BigDecimal currentBalance = account.getBalance();
                if (currentBalance.compareTo(normalizedAmount) < 0) {
                    throw new InsufficientFundsException(normalizedAmount, currentBalance);
                }

                BigDecimal newBalance = currentBalance.subtract(normalizedAmount).setScale(2, RoundingMode.HALF_UP);
                accountRepository.updateBalance(conn, accountNo, newBalance);

                Transaction tx = new Transaction(
                        0,
                        accountNo,
                        new Timestamp(System.currentTimeMillis()),
                        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        normalizedAmount,
                        newBalance
                );
                transactionRepository.recordTransaction(conn, tx);

                conn.commit();

                // Update cache
                account.setBalance(newBalance);
                accountCache.put(accountNo, account);

                return newBalance;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new BankingDatabaseException("Withdrawal transaction failed and was rolled back.", e);
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new BankingDatabaseException("Database connection error during withdrawal for account: " + accountNo, e);
        }
    }

    @Override
    public List<Transaction> getPassbook(int accountNo) {
        if (!accountRepository.existsById(accountNo)) {
            throw new AccountNotFoundException(accountNo);
        }
        return transactionRepository.findByAccountNo(accountNo);
    }

    @Override
    public List<Transaction> getTopKTransactions(int accountNo, int k) {
        List<Transaction> allTransactions = getPassbook(accountNo);
        return TopKTransactions.findTopK(allTransactions, k);
    }
}
