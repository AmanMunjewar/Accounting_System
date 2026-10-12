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

        // 5. Start Web UI Server (port 8080 or WEB_PORT env variable)
        int webPort = 8080;
        String envPort = System.getenv("PORT");
        if (envPort == null || envPort.isEmpty()) envPort = System.getenv("WEB_PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                webPort = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        try {
            com.vectorbank.web.BankingWebServer webServer = new com.vectorbank.web.BankingWebServer(webPort, bankingService);
            webServer.start();
        } catch (Exception e) {
            System.err.println("⚠️ Could not start Web Server: " + e.getMessage());
        }

        // 6. Start Interactive CLI User Interface
        BankingCli cli = new BankingCli(bankingService);
        cli.start();
    }
}
