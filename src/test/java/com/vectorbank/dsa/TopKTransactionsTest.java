package com.vectorbank.dsa;

import com.vectorbank.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TopKTransactions DSA Tests (Min-Heap O(N log K))")
class TopKTransactionsTest {

    private Transaction createTx(int id, double amount, boolean isDeposit) {
        BigDecimal deposit = isDeposit ? BigDecimal.valueOf(amount).setScale(2) : BigDecimal.ZERO.setScale(2);
        BigDecimal withdraw = !isDeposit ? BigDecimal.valueOf(amount).setScale(2) : BigDecimal.ZERO.setScale(2);
        return new Transaction(id, 1001, new Timestamp(System.currentTimeMillis()), deposit, withdraw, BigDecimal.valueOf(1000).setScale(2));
    }

    @Test
    @DisplayName("Should extract top K largest transactions sorted descending")
    void testStandardTopK() {
        List<Transaction> transactions = List.of(
                createTx(1, 100.00, true),
                createTx(2, 500.00, false),
                createTx(3, 20.00, true),
                createTx(4, 1500.00, true),
                createTx(5, 750.00, false)
        );

        List<Transaction> top3 = TopKTransactions.findTopK(transactions, 3);

        assertEquals(3, top3.size());
        assertEquals(BigDecimal.valueOf(1500.00).setScale(2), top3.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(750.00).setScale(2), top3.get(1).getAmount());
        assertEquals(BigDecimal.valueOf(500.00).setScale(2), top3.get(2).getAmount());
    }

    @Test
    @DisplayName("Edge Case: Empty input transactions list returns empty list")
    void testEmptyInput() {
        List<Transaction> empty = Collections.emptyList();
        List<Transaction> result = TopKTransactions.findTopK(empty, 5);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        List<Transaction> nullResult = TopKTransactions.findTopK(null, 5);
        assertNotNull(nullResult);
        assertTrue(nullResult.isEmpty());
    }

    @Test
    @DisplayName("Edge Case: K <= 0 returns empty list")
    void testKZeroOrNegative() {
        List<Transaction> transactions = List.of(createTx(1, 100.00, true));
        assertTrue(TopKTransactions.findTopK(transactions, 0).isEmpty());
        assertTrue(TopKTransactions.findTopK(transactions, -3).isEmpty());
    }

    @Test
    @DisplayName("Edge Case: K = 1 returns the single largest transaction")
    void testKEqualsOne() {
        List<Transaction> transactions = List.of(
                createTx(1, 50.00, true),
                createTx(2, 250.00, true),
                createTx(3, 120.00, false)
        );

        List<Transaction> top1 = TopKTransactions.findTopK(transactions, 1);
        assertEquals(1, top1.size());
        assertEquals(BigDecimal.valueOf(250.00).setScale(2), top1.get(0).getAmount());
        assertEquals(2, top1.get(0).getTransactionId());
    }

    @Test
    @DisplayName("Edge Case: K >= N returns all transactions sorted descending")
    void testKGreaterThanOrEqualToN() {
        List<Transaction> transactions = List.of(
                createTx(1, 200.00, true),
                createTx(2, 100.00, true)
        );

        List<Transaction> top10 = TopKTransactions.findTopK(transactions, 10);
        assertEquals(2, top10.size());
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), top10.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2), top10.get(1).getAmount());
    }

    @Test
    @DisplayName("Edge Case: Duplicate transaction amounts handled correctly")
    void testDuplicateAmounts() {
        List<Transaction> transactions = List.of(
                createTx(1, 300.00, true),
                createTx(2, 300.00, false),
                createTx(3, 100.00, true),
                createTx(4, 500.00, true)
        );

        List<Transaction> top3 = TopKTransactions.findTopK(transactions, 3);
        assertEquals(3, top3.size());
        assertEquals(BigDecimal.valueOf(500.00).setScale(2), top3.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(300.00).setScale(2), top3.get(1).getAmount());
        assertEquals(BigDecimal.valueOf(300.00).setScale(2), top3.get(2).getAmount());
    }
}
