package com.vectorbank.benchmark;

import com.vectorbank.dsa.LRUCache;
import com.vectorbank.dsa.TopKTransactions;
import com.vectorbank.model.Account;
import com.vectorbank.model.Customer;
import com.vectorbank.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Empirical Performance and Concurrency Benchmark")
class EmpiricalBenchmarkTest {

    @Test
    @DisplayName("Run Full Benchmark and Report Real Metrics")
    void runBenchmark() throws Exception {
        System.out.println("\n=================================================================");
        System.out.println("     VECTOR BANK: EMPIRICAL PERFORMANCE & CONCURRENCY BENCHMARK  ");
        System.out.println("=================================================================\n");

        benchmarkBCrypt();
        benchmarkLRUCache();
        benchmarkTopKTransactions();
        benchmarkConcurrencyScaling();

        System.out.println("\n=================================================================");
        System.out.println("                     BENCHMARK COMPLETE                          ");
        System.out.println("=================================================================\n");
        assertTrue(true);
    }

    private void benchmarkBCrypt() {
        System.out.println("--- 1. BCrypt Security & Password Hashing Benchmark ---");
        String samplePassword = "SecurePass!2026";
        int iterations = 10;

        // Warmup
        BCrypt.hashpw(samplePassword, BCrypt.gensalt(10));

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String hash = BCrypt.hashpw(samplePassword, BCrypt.gensalt(12));
            BCrypt.checkpw(samplePassword, hash);
        }
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        double avgCostMs = (double) durationMs / iterations;

        System.out.printf("   Iterations: %d | Total: %d ms | Avg per Auth Cycle (Hash+Verify): %.2f ms\n", 
                iterations, durationMs, avgCostMs);
        System.out.printf("   Throughput: %.2f auth operations/sec\n\n", 
                1000.0 / avgCostMs);
    }

    private void benchmarkLRUCache() {
        System.out.println("--- 2. LRU Cache O(1) Operations Benchmark ---");
        LRUCache<Integer, Account> cache = new LRUCache<>(1000);
        int operations = 500_000;

        // Prepopulate with accounts
        for (int i = 0; i < 1000; i++) {
            Customer c = new Customer(i, "User " + i, LocalDate.now(), "PAN" + i);
            cache.put(i, new Account(i, c, BigDecimal.valueOf(100.0), "hash"));
        }

        // Measure O(1) Get
        long startGet = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            cache.get(i % 1200);
        }
        long getDurationNs = System.nanoTime() - startGet;
        double avgGetNs = (double) getDurationNs / operations;

        // Measure O(1) Put
        long startPut = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            Customer c = new Customer(i, "User " + i, LocalDate.now(), "PAN" + i);
            cache.put(i % 1500, new Account(i, c, BigDecimal.valueOf(100.0), "hash"));
        }
        long putDurationNs = System.nanoTime() - startPut;
        double avgPutNs = (double) putDurationNs / operations;

        System.out.printf("   Operations: %,d\n", operations);
        System.out.printf("   Avg LRU Get Latency: %.2f ns (%.4f µs) | Throughput: %,.0f ops/sec\n", 
                avgGetNs, avgGetNs / 1000.0, 1_000_000_000.0 / avgGetNs);
        System.out.printf("   Avg LRU Put Latency: %.2f ns (%.4f µs) | Throughput: %,.0f ops/sec\n\n", 
                avgPutNs, avgPutNs / 1000.0, 1_000_000_000.0 / avgPutNs);
    }

    private void benchmarkTopKTransactions() {
        System.out.println("--- 3. Top-K Largest Transactions Min-Heap Benchmark ---");
        int numTransactions = 50_000;
        int k = 10;
        List<Transaction> transactions = new ArrayList<>(numTransactions);
        Random r = new Random(42);

        for (int i = 0; i < numTransactions; i++) {
            BigDecimal amt = BigDecimal.valueOf(r.nextDouble() * 10_000).setScale(2, RoundingMode.HALF_UP);
            transactions.add(new Transaction(i, 1001, new Timestamp(System.currentTimeMillis()), amt, BigDecimal.ZERO, BigDecimal.valueOf(50000)));
        }

        // Warmup
        TopKTransactions.findTopK(transactions, k);

        int runs = 50;
        long totalNs = 0;
        for (int i = 0; i < runs; i++) {
            long start = System.nanoTime();
            TopKTransactions.findTopK(transactions, k);
            totalNs += (System.nanoTime() - start);
        }

        double avgMs = (totalNs / 1_000_000.0) / runs;
        System.out.printf("   Input Dataset (N): %,d transactions | K: %d\n", numTransactions, k);
        System.out.printf("   Avg Min-Heap Extraction Time: %.3f ms | Throughput: %,.0f dataset extractions/sec\n\n", 
                avgMs, 1000.0 / avgMs);
    }

    private void benchmarkConcurrencyScaling() throws InterruptedException {
        System.out.println("--- 4. Multi-Threaded In-Memory Concurrency Benchmark ---");
        int[] threadLevels = {1, 5, 10, 25, 50, 100};
        int operationsPerThread = 10_000;

        LRUCache<Integer, String> sharedCache = new LRUCache<>(500);

        System.out.printf("   %-15s | %-15s | %-18s | %-15s\n", 
                "Concurrent Users", "Total Ops", "Total Time (ms)", "Throughput (ops/s)");
        System.out.println("   ------------------------------------------------------------------");

        for (int threads : threadLevels) {
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CountDownLatch latch = new CountDownLatch(threads);
            AtomicInteger counter = new AtomicInteger(0);

            long start = System.currentTimeMillis();
            for (int t = 0; t < threads; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        Random rand = new Random(threadId);
                        for (int i = 0; i < operationsPerThread; i++) {
                            int key = rand.nextInt(1000);
                            if (rand.nextBoolean()) {
                                sharedCache.put(key, "Val-" + key);
                            } else {
                                sharedCache.get(key);
                            }
                            counter.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            long duration = Math.max(1, System.currentTimeMillis() - start);
            executor.shutdown();

            int totalOps = threads * operationsPerThread;
            double opsPerSec = (totalOps / (double) duration) * 1000.0;

            System.out.printf("   %-15d | %-15d | %-18d | %-15.0f\n", 
                    threads, totalOps, duration, opsPerSec);
        }
    }
}
