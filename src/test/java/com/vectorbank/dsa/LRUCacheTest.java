package com.vectorbank.dsa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LRUCache DSA Tests (O(1) get/put)")
class LRUCacheTest {

    @Test
    @DisplayName("Should put and get items correctly")
    void testBasicPutAndGet() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");

        assertEquals(Optional.of("One"), cache.get(1));
        assertEquals(Optional.of("Two"), cache.get(2));
        assertEquals(Optional.empty(), cache.get(3));
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("Should evict least recently used item when capacity is exceeded")
    void testEvictionOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A"); // State: [1]
        cache.put(2, "B"); // State: [2, 1]

        // Access 1 so 2 becomes the least recently used
        assertEquals(Optional.of("A"), cache.get(1)); // State: [1, 2]

        // Insert 3 -> 2 should be evicted
        cache.put(3, "C"); // State: [3, 1]

        assertEquals(Optional.of("A"), cache.get(1));
        assertEquals(Optional.empty(), cache.get(2)); // Evicted!
        assertEquals(Optional.of("C"), cache.get(3));
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("Edge Case: Cache with capacity 1 works properly")
    void testCapacityOne() {
        LRUCache<Integer, String> cache = new LRUCache<>(1);
        assertEquals(1, cache.getCapacity());

        cache.put(10, "Ten");
        assertEquals(Optional.of("Ten"), cache.get(10));
        assertEquals(1, cache.size());

        cache.put(20, "Twenty");
        assertEquals(Optional.empty(), cache.get(10)); // Evicted
        assertEquals(Optional.of("Twenty"), cache.get(20));
        assertEquals(1, cache.size());
    }

    @Test
    @DisplayName("Should update existing key and make it most recently used without increasing size")
    void testUpdateExistingKey() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "First");
        cache.put(2, "Second");

        // Update value of key 1
        cache.put(1, "UpdatedFirst");
        assertEquals(2, cache.size());
        assertEquals(Optional.of("UpdatedFirst"), cache.get(1));

        // Now key 2 should be the LRU; adding key 3 should evict 2, not 1
        cache.put(3, "Third");
        assertEquals(Optional.of("UpdatedFirst"), cache.get(1));
        assertEquals(Optional.empty(), cache.get(2));
        assertEquals(Optional.of("Third"), cache.get(3));
    }

    @Test
    @DisplayName("Should remove specific keys correctly")
    void testRemove() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");

        assertTrue(cache.remove(1));
        assertFalse(cache.remove(99)); // Absent key
        assertEquals(Optional.empty(), cache.get(1));
        assertEquals(1, cache.size());
    }

    @Test
    @DisplayName("Should clear all items")
    void testClear() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.clear();

        assertEquals(0, cache.size());
        assertEquals(Optional.empty(), cache.get(1));
        assertEquals(Optional.empty(), cache.get(2));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when capacity <= 0")
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, String>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, String>(-5));
    }
}
