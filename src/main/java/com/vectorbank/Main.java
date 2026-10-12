package com.vectorbank;

import com.vectorbank.cli.BankingCli;
import com.vectorbank.config.DatabaseConfig;
import com.vectorbank.dsa.LRUCache;
import com.vectorbank.model.Account;
import com.vectorbank.repository.AccountRepository;
import com.vectorbank.repository.JdbcAccountRepository;
import com.vectorbank.repository.JdbcTransactionRepository;
import com.vectorbank.repository.TransactionRepository;
import com.vectorbank.service.BankingService;
import com.vectorbank.service.BankingServiceImpl;

/**
 * VECTOR Bank Entry Point.
 * Wires configuration, data repositories, LRU cache, and service layer,
 * then launches the interactive CLI.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Initialize DB configuration from environment
        DatabaseConfig dbConfig = new DatabaseConfig();

        // 2. Initialize Repositories
        AccountRepository accountRepository = new JdbcAccountRepository(dbConfig);
        TransactionRepository transactionRepository = new JdbcTransactionRepository(dbConfig);

        // 3. Initialize DSA LRU Cache (Capacity = 100 accounts)
        LRUCache<Integer, Account> accountCache = new LRUCache<>(100);

        // 4. Initialize Core Banking Service
        BankingService bankingService = new BankingServiceImpl(
                accountRepository, 
                transactionRepository, 
                dbConfig, 
                accountCache
        );

        // 5. Start CLI User Interface
        BankingCli cli = new BankingCli(bankingService);
        cli.start();
    }
}
