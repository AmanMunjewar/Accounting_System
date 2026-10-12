package com.vectorbank.cli;

import com.vectorbank.exception.AuthenticationException;
import com.vectorbank.exception.BankingException;
import com.vectorbank.model.Account;
import com.vectorbank.model.Customer;
import com.vectorbank.model.Transaction;
import com.vectorbank.service.BankingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Command Line Interface for VECTOR Bank.
 * Provides user menus, formatted transaction tables, and error notifications.
 */
public class BankingCli {
    private final BankingService bankingService;
    private final InputReader reader;

    public BankingCli(BankingService bankingService) {
        this.bankingService = bankingService;
        this.reader = new InputReader(new Scanner(System.in));
    }

    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            System.out.println("\n╔═════════════════════════════════════════════╗");
            System.out.println("║             VECTOR BANK MAIN MENU           ║");
            System.out.println("╠═════════════════════════════════════════════╣");
            System.out.println("║  1. Open New Account                        ║");
            System.out.println("║  2. Sign In to Existing Account             ║");
            System.out.println("║  3. Exit System                             ║");
            System.out.println("╚═════════════════════════════════════════════╝");

            int choice = reader.readInt("Select an option (1-3): ");
            switch (choice) {
                case 1 -> handleCreateAccount();
                case 2 -> handleAccountSession();
                case 3 -> {
                    System.out.println("\n👋 Thank you for banking with VECTOR Bank. Goodbye!");
                    running = false;
                }
                default -> System.out.println("❌ Invalid selection. Please enter 1, 2, or 3.");
            }
        }
    }

    private void handleCreateAccount() {
        System.out.println("\n--- [ Open New Account ] ---");
        String rawName = reader.readNonEmptyString("Enter Full Name: ");
        String name = InputReader.capitalizeWords(rawName);
        LocalDate dob = reader.readDate("Enter Date of Birth (YYYY-MM-DD): ");
        String pan = reader.readNonEmptyString("Enter PAN / Tax ID: ").toUpperCase();
        BigDecimal initialDeposit = reader.readBigDecimal("Enter Initial Deposit: $");
        String password = reader.readNonEmptyString("Enter Account Password: ");

        try {
            int accountNo = bankingService.createAccount(name, dob, pan, initialDeposit, password);
            System.out.println("\n🎉 Account successfully created!");
            System.out.printf("👉 Your Account Number is: [%d]\n", accountNo);
            System.out.printf("💰 Initial Balance: $%.2f\n", initialDeposit);
            System.out.println("⚠️  Please store your account number safely.");
        } catch (BankingException e) {
            System.out.println("❌ Account creation failed: " + e.getMessage());
        }
    }

    private void handleAccountSession() {
        System.out.println("\n--- [ Sign In ] ---");
        int accountNo = reader.readInt("Enter Account Number: ");
        String password = reader.readNonEmptyString("Enter Password: ");

        try {
            Account account = bankingService.authenticate(accountNo, password);
            System.out.println("\n✅ Login Successful! Welcome back, " + account.getCustomer().getName() + ".");

            boolean inSession = true;
            while (inSession) {
                System.out.println("\n┌─────────────────────────────────────────────┐");
                System.out.printf("│  ACCOUNT: %-8d   BALANCE: $%-13.2f│\n", accountNo, account.getBalance());
                System.out.println("├─────────────────────────────────────────────┤");
                System.out.println("│  1. View Account Profile                    │");
                System.out.println("│  2. Deposit Funds                           │");
                System.out.println("│  3. Withdraw Funds                          │");
                System.out.println("│  4. View Passbook (Audit History)           │");
                System.out.println("│  5. View Top-K Largest Transactions (DSA)   │");
                System.out.println("│  6. Logout                                  │");
                System.out.println("└─────────────────────────────────────────────┘");

                int choice = reader.readInt("Select an option (1-6): ");
                switch (choice) {
                    case 1 -> displayAccountInfo(accountNo);
                    case 2 -> {
                        BigDecimal amount = reader.readBigDecimal("Enter Deposit Amount: $");
                        try {
                            BigDecimal newBal = bankingService.deposit(accountNo, amount);
                            account.setBalance(newBal);
                            System.out.printf("✅ Deposit successful! Updated balance: $%.2f\n", newBal);
                        } catch (BankingException e) {
                            System.out.println("❌ Deposit rejected: " + e.getMessage());
                        }
                    }
                    case 3 -> {
                        BigDecimal amount = reader.readBigDecimal("Enter Withdrawal Amount: $");
                        try {
                            BigDecimal newBal = bankingService.withdraw(accountNo, amount);
                            account.setBalance(newBal);
                            System.out.printf("✅ Withdrawal successful! Remaining balance: $%.2f\n", newBal);
                        } catch (BankingException e) {
                            System.out.println("❌ Withdrawal rejected: " + e.getMessage());
                        }
                    }
                    case 4 -> displayPassbook(accountNo);
                    case 5 -> displayTopK(accountNo);
                    case 6 -> {
                        System.out.println("🔒 Logged out of account " + accountNo + ".");
                        inSession = false;
                    }
                    default -> System.out.println("❌ Invalid option. Please select 1 through 6.");
                }
            }
        } catch (AuthenticationException e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
        } catch (BankingException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void displayAccountInfo(int accountNo) {
        try {
            Account acc = bankingService.getAccountInfo(accountNo);
            Customer cust = acc.getCustomer();
            System.out.println("\n==============================================");
            System.out.println("               ACCOUNT DETAILS                ");
            System.out.println("==============================================");
            System.out.printf(" Account Number : %d\n", acc.getAccountNo());
            System.out.printf(" Holder Name    : %s\n", cust.getName());
            System.out.printf(" Date of Birth  : %s\n", cust.getDob());
            System.out.printf(" PAN Card / ID  : %s\n", cust.getPan());
            System.out.printf(" Current Balance: $%.2f\n", acc.getBalance());
            System.out.println("==============================================");
        } catch (BankingException e) {
            System.out.println("❌ Unable to load account info: " + e.getMessage());
        }
    }

    private void displayPassbook(int accountNo) {
        try {
            List<Transaction> transactions = bankingService.getPassbook(accountNo);
            if (transactions.isEmpty()) {
                System.out.println("ℹ️  No transaction history found for this account.");
                return;
            }

            System.out.println("\n" + "=".repeat(85));
            System.out.printf("%-6s | %-20s | %-15s | %-15s | %-15s\n", 
                    "ID", "Timestamp", "Deposit ($)", "Withdraw ($)", "Balance ($)");
            System.out.println("-".repeat(85));

            for (Transaction t : transactions) {
                System.out.printf("%-6d | %-20s | %-15.2f | %-15.2f | %-15.2f\n",
                        t.getTransactionId(),
                        t.getTimestamp().toString().substring(0, 19),
                        t.getDeposit(),
                        t.getWithdraw(),
                        t.getBalanceAfter());
            }
            System.out.println("=".repeat(85));
        } catch (BankingException e) {
            System.out.println("❌ Failed to retrieve passbook: " + e.getMessage());
        }
    }

    private void displayTopK(int accountNo) {
        int k = reader.readInt("Enter number of top transactions to view (K): ");
        if (k <= 0) {
            System.out.println("❌ K must be greater than zero.");
            return;
        }

        try {
            List<Transaction> topList = bankingService.getTopKTransactions(accountNo, k);
            if (topList.isEmpty()) {
                System.out.println("ℹ️  No transactions available to rank.");
                return;
            }

            System.out.println("\n" + "=".repeat(85));
            System.out.printf("          TOP %d LARGEST TRANSACTIONS (MIN-HEAP O(N log K))          \n", Math.min(k, topList.size()));
            System.out.println("=".repeat(85));
            System.out.printf("%-6s | %-10s | %-15s | %-20s | %-15s\n",
                    "Rank", "Type", "Amount ($)", "Timestamp", "Balance After ($)");
            System.out.println("-".repeat(85));

            int rank = 1;
            for (Transaction t : topList) {
                String type = t.isDeposit() ? "DEPOSIT" : "WITHDRAW";
                System.out.printf("#%-5d | %-10s | %-15.2f | %-20s | %-15.2f\n",
                        rank++,
                        type,
                        t.getAmount(),
                        t.getTimestamp().toString().substring(0, 19),
                        t.getBalanceAfter());
            }
            System.out.println("=".repeat(85));
        } catch (BankingException e) {
            System.out.println("❌ Error retrieving top transactions: " + e.getMessage());
        }
    }

    private void printBanner() {
        System.out.println("""
        ========================================================================
             ██╗   ██╗███████╗ ██████╗████████╗ ██████╗ ██████╗     ██████╗  █████╗ ███╗   ██╗██╗  ██╗
             ██║   ██║██╔════╝██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗    ██╔══██╗██╔══██╗████╗  ██║██║ ██╔╝
             ██║   ██║█████╗  ██║        ██║   ██║   ██║██████╔╝    ██████╔╝███████║██╔██╗ ██║█████╔╝ 
             ╚██╗ ██╔╝██╔══╝  ██║        ██║   ██║   ██║██╔══██╗    ██╔══██╗██╔══██║██║╚██╗██║██╔═██╗ 
              ╚████╔╝ ███████╗╚██████╗   ██║   ╚██████╔╝██║  ██║    ██████╔╝██║  ██║██║ ╚████║██║  ██╗
               ╚═══╝  ╚══════╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝
                                 --- Precision Banking System v2.0 ---
        ========================================================================
        """);
    }
}
