package com.vectorbank.dsa;

import com.vectorbank.model.Transaction;

import java.util.*;

/**
 * Top-K Largest Transactions extractor implemented using a Min-Heap (PriorityQueue).
 *
 * ALGORITHM:
 * Maintain a Min-Heap of size at most K, where elements are ordered by transaction amount ascending.
 * 1. For each transaction in the collection:
 *    - If the heap has fewer than K elements, add the transaction.
 *    - Else if the transaction amount is strictly greater than the heap's minimum element (peek()),
 *      poll the minimum element and insert the new transaction.
 * 2. At the end of the iteration, the heap contains the K largest transactions.
 * 3. Extract elements and sort descending to return in highest-to-lowest order.
 *
 * TIME COMPLEXITY:
 * - Iterating through N transactions:
 *   - Insertion / heapify operation takes O(log K) time.
 *   - Total processing time: O(N * log K).
 * - Extracting and sorting K elements: O(K * log K).
 * - Overall Time Complexity: O(N * log K).
 *   (Significantly faster than sorting the entire list O(N * log N) when K << N).
 *
 * SPACE COMPLEXITY:
 * - O(K) auxiliary space to store at most K elements in the min-heap.
 */
public class TopKTransactions {

    /**
     * Extracts the top K largest transactions by absolute monetary amount.
     *
     * @param transactions collection of transactions (can be empty or null)
     * @param k number of top transactions to retrieve (must be >= 0)
     * @return List of top K transactions ordered from highest amount to lowest
     */
    public static List<Transaction> findTopK(Collection<Transaction> transactions, int k) {
        if (transactions == null || transactions.isEmpty() || k <= 0) {
            return Collections.emptyList();
        }

        // Min-heap ordering by amount ascending (smallest amount at root)
        PriorityQueue<Transaction> minHeap = new PriorityQueue<>(
                Comparator.comparing(Transaction::getAmount)
        );

        for (Transaction tx : transactions) {
            if (minHeap.size() < k) {
                minHeap.offer(tx);
            } else if (tx.getAmount().compareTo(minHeap.peek().getAmount()) > 0) {
                minHeap.poll(); // Evict smallest among current top-K
                minHeap.offer(tx);
            }
        }

        // Transfer heap elements to list and sort descending
        List<Transaction> result = new ArrayList<>(minHeap);
        result.sort((t1, t2) -> t2.getAmount().compareTo(t1.getAmount()));
        return result;
    }
}
