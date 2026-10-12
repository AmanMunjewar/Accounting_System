package com.vectorbank.service;

import com.vectorbank.config.DatabaseConfig;
import com.vectorbank.dsa.LRUCache;
import com.vectorbank.exception.AccountNotFoundException;
import com.vectorbank.exception.AuthenticationException;
import com.vectorbank.exception.InsufficientFundsException;
import com.vectorbank.model.Account;
import com.vectorbank.model.Customer;
import com.vectorbank.model.Transaction;
import com.vectorbank.repository.AccountRepository;
import com.vectorbank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankingService Business Logic Tests (Mockito)")
class BankingServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DatabaseConfig databaseConfig;

    @Mock
    private Connection connection;

    private LRUCache<Integer, Account> cache;
    private BankingServiceImpl bankingService;

    private final int testAccountNo = 1234;
    private Customer testCustomer;
    private Account testAccount;
    private final String plainPassword = "SecretPassword123";
    private String hashedPassword;

    @BeforeEach
    void setUp() throws SQLException {
        cache = new LRUCache<>(10);
        bankingService = new BankingServiceImpl(accountRepository, transactionRepository, databaseConfig, cache);

        testCustomer = new Customer(testAccountNo, "Jane Doe", LocalDate.of(1995, 5, 20), "ABCDE1234F");
        hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        testAccount = new Account(testAccountNo, testCustomer, new BigDecimal("500.00"), hashedPassword);

        lenient().when(databaseConfig.getConnection()).thenReturn(connection);
    }

    @Test
    @DisplayName("Deposit: Increases balance and creates passbook record in single atomic transaction")
    void testDepositSuccess() throws SQLException {
        when(accountRepository.findById(testAccountNo)).thenReturn(Optional.of(testAccount));

        BigDecimal depositAmount = new BigDecimal("250.50");
        BigDecimal newBalance = bankingService.deposit(testAccountNo, depositAmount);

        assertEquals(new BigDecimal("750.50"), newBalance);
        assertEquals(new BigDecimal("750.50"), testAccount.getBalance());

        // Verify JDBC transaction boundaries
        verify(connection).setAutoCommit(false);
        verify(accountRepository).updateBalance(eq(connection), eq(testAccountNo), eq(new BigDecimal("750.50")));
        verify(transactionRepository).recordTransaction(eq(connection), any(Transaction.class));
        verify(connection).commit();
        verify(connection, never()).rollback();

        // Verify cache updated
        Optional<Account> cached = cache.get(testAccountNo);
        assertTrue(cached.isPresent());
        assertEquals(new BigDecimal("750.50"), cached.get().getBalance());
    }

    @Test
    @DisplayName("Withdraw: Decreases balance and records transaction when funds are sufficient")
    void testWithdrawSuccess() throws SQLException {
        when(accountRepository.findById(testAccountNo)).thenReturn(Optional.of(testAccount));

        BigDecimal withdrawAmount = new BigDecimal("150.00");
        BigDecimal newBalance = bankingService.withdraw(testAccountNo, withdrawAmount);

        assertEquals(new BigDecimal("350.00"), newBalance);
        assertEquals(new BigDecimal("350.00"), testAccount.getBalance());

        // Verify JDBC transaction boundaries
        verify(connection).setAutoCommit(false);
        verify(accountRepository).updateBalance(eq(connection), eq(testAccountNo), eq(new BigDecimal("350.00")));
        verify(transactionRepository).recordTransaction(eq(connection), any(Transaction.class));
        verify(connection).commit();
        verify(connection, never()).rollback();

        // Verify cache updated
        Optional<Account> cached = cache.get(testAccountNo);
        assertTrue(cached.isPresent());
        assertEquals(new BigDecimal("350.00"), cached.get().getBalance());
    }

    @Test
    @DisplayName("Withdraw: Throws InsufficientFundsException and rolls back when balance is insufficient")
    void testWithdrawInsufficientFunds() throws SQLException {
        when(accountRepository.findById(testAccountNo)).thenReturn(Optional.of(testAccount));

        BigDecimal excessiveAmount = new BigDecimal("999.00"); // Available is 500.00

        assertThrows(InsufficientFundsException.class, () -> 
            bankingService.withdraw(testAccountNo, excessiveAmount)
        );

        // Verify rollback was invoked
        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(accountRepository, never()).updateBalance(any(), anyInt(), any());
        verify(transactionRepository, never()).recordTransaction(any(), any());
    }

    @Test
    @DisplayName("Withdraw: Throws AccountNotFoundException when account does not exist")
    void testWithdrawAccountNotFound() throws SQLException {
        when(accountRepository.findById(9999)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> 
            bankingService.withdraw(9999, new BigDecimal("100.00"))
        );

        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    @DisplayName("Deposit: Rejects negative or zero deposit amounts")
    void testInvalidDepositAmount() {
        assertThrows(IllegalArgumentException.class, () -> 
            bankingService.deposit(testAccountNo, BigDecimal.ZERO)
        );
        assertThrows(IllegalArgumentException.class, () -> 
            bankingService.deposit(testAccountNo, new BigDecimal("-50.00"))
        );
    }

    @Test
    @DisplayName("Authentication: Succeeds with correct password and fails with invalid password")
    void testAuthentication() {
        when(accountRepository.findById(testAccountNo)).thenReturn(Optional.of(testAccount));

        // Successful authentication
        Account authenticated = bankingService.authenticate(testAccountNo, plainPassword);
        assertNotNull(authenticated);
        assertEquals(testAccountNo, authenticated.getAccountNo());

        // Failed authentication
        assertThrows(AuthenticationException.class, () -> 
            bankingService.authenticate(testAccountNo, "WrongPassword")
        );
    }

    @Test
    @DisplayName("CreateAccount: Hashes password with BCrypt and saves customer and account")
    void testCreateAccount() {
        when(accountRepository.existsById(anyInt())).thenReturn(false);

        int newAccNo = bankingService.createAccount("Bob Smith", LocalDate.of(1990, 1, 1), 
                "PAN12345X", new BigDecimal("1000.00"), "MySecurePassword");

        assertTrue(newAccNo >= 1000);

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountRepository).save(any(Customer.class), eq(new BigDecimal("1000.00")), passwordCaptor.capture());

        String savedHash = passwordCaptor.getValue();
        assertNotEquals("MySecurePassword", savedHash);
        assertTrue(BCrypt.checkpw("MySecurePassword", savedHash));
    }
}
