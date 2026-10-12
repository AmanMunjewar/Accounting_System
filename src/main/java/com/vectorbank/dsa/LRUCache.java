package com.vectorbank.dsa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Custom Least Recently Used (LRU) Cache implementation using a custom Doubly Linked List
 * and a Java HashMap.
 *
 * Provides strictly O(1) average time complexity for both {@code get} and {@code put} operations.
 *
 * TIME COMPLEXITY:
 * - get(K key): O(1) average time. Map lookup is O(1), moving the node to the head of the DLL is O(1).
 * - put(K key, V value): O(1) average time. Map lookup/insert is O(1), inserting/moving/evicting nodes is O(1).
 * - remove(K key): O(1) average time.
 * - size(): O(1) time.
 *
 * SPACE COMPLEXITY:
 * - O(C) where C is the maximum capacity of the cache.
 *   The HashMap stores at most C entries, and the Doubly Linked List stores at most C nodes
 *   plus 2 dummy sentinel nodes (head and tail).
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public class LRUCache<K, V> {

    /**
     * Doubly linked list node containing key, value, and pointers to prev and next nodes.
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    // Dummy sentinel nodes to eliminate null checks when adding/removing at boundary
    private final Node<K, V> head;
    private final Node<K, V> tail;

    /**
     * Initializes the LRUCache with a positive maximum capacity.
     *
     * @param capacity maximum number of items the cache can hold (must be >= 1)
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("LRU Cache capacity must be greater than 0, received: " + capacity);
        }
        this.capacity = capacity;
        this.map = new HashMap<>();

        // Create sentinel nodes
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    /**
     * Retrieves the value associated with the specified key, if present.
     * Marks the accessed entry as Most Recently Used by moving it to the head of the list.
     *
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     *
     * @param key the key whose associated value is to be returned
     * @return Optional containing the value if found, or empty Optional if absent
     */
    public synchronized Optional<V> get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return Optional.empty();
        }
        // Move accessed node to head (Most Recently Used)
        moveToHead(node);
        return Optional.of(node.value);
    }

    /**
     * Inserts or updates the value associated with the key.
     * If the key exists, its value is updated and moved to the head.
     * If the key does not exist and the cache is at capacity, the Least Recently Used item
     * (the one right before the tail sentinel) is evicted before inserting the new item.
     *
     * Time Complexity: O(1)
     * Space Complexity: O(1) auxiliary
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public synchronized void put(K key, V value) {
        Node<K, V> existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            moveToHead(existing);
            return;
        }

        // Evict LRU item if capacity is reached
        if (map.size() >= capacity) {
            Node<K, V> lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }

        Node<K, V> newNode = new Node<>(key, value);
        map.put(key, newNode);
        addFirst(newNode);
    }

    /**
     * Removes an entry from the cache by key.
     *
     * Time Complexity: O(1)
     */
    public synchronized boolean remove(K key) {
        Node<K, V> node = map.remove(key);
        if (node != null) {
            removeNode(node);
            return true;
        }
        return false;
    }

    /**
     * Clears all entries from the cache.
     *
     * Time Complexity: O(C)
     */
    public synchronized void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns the current number of key-value mappings in this cache.
     *
     * Time Complexity: O(1)
     */
    public synchronized int size() {
        return map.size();
    }

    /**
     * Returns the configured capacity of this cache.
     */
    public int getCapacity() {
        return capacity;
    }

    // ---------------------------------------------------------
    // Doubly Linked List Helper Methods (Strictly O(1))
    // ---------------------------------------------------------

    private void addFirst(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addFirst(node);
    }
}
