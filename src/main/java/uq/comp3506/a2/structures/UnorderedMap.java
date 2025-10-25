// @edu:student-assignment

package uq.comp3506.a2.structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 * <p>
 * NOTE: You should go and carefully read the documentation provided in the
 * MapInterface.java file - this explains some of the required functionality.
 */
public class UnorderedMap<K, V> implements MapInterface<K, V> {


    /**
     * you will need to put some member variables here to track your
     * data, size, capacity, etc...
     */

    // Tunables
    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;

    // storage
    private List<LinkedList<Entry<K, V>>> buckets;
    private int capacity;
    private int size;

    /**
     * Constructs an empty UnorderedMap
     */
    public UnorderedMap() {
        // Implement me!
        this(DEFAULT_CAPACITY);
    }

    // initialCapacity must be > 0
    public UnorderedMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }

        this.capacity = initialCapacity;
        this.buckets = new ArrayList<>(capacity);

        // Initialize all buckets with empty linked lists
        for (int i = 0; i < capacity; i++) {
            buckets.add(new LinkedList<>());
        }
        this.size = 0;
    }

    /**
     * Hashing function to compute bucket index for a key
     */
    private int index(K key) {
        if (key == null) return 0;
        // And operation to ensure positive and mod to ensure the index
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    private void resize() {
        int newCapacity = capacity * 2;
        List<LinkedList<Entry<K, V>>> oldBuckets = buckets;

        // Create new buckets
        buckets = new ArrayList<>(newCapacity);
        for (int i = 0; i < newCapacity; i++) {
            buckets.add(new LinkedList<>());
        }

        // Rehash all entries
        int oldCapacity = capacity;
        capacity = newCapacity;
        size = 0; // Reset size, will be updated during rehashing

        for (int i = 0; i < oldCapacity; i++) {
            LinkedList<Entry<K, V>> bucket = oldBuckets.get(i);
            for (Entry<K, V> entry : bucket) {
                // Use put to reinsert entries (this will update size)
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Helper method to compare keys, handling null keys properly
     */
    private boolean keysEqual(K key1, K key2) {
        if (key1 == null && key2 == null) return true;
        if (key1 == null || key2 == null) return false;
        return key1.equals(key2);
    }

    /**
     * returns the size of the structure in terms of pairs
     * @return the number of kv pairs stored
     */
    @Override
    public int size() {
        // Implement me!
        return size;
    }

    /**
     * helper to indicate if the structure is empty or not
     * @return true if the map contains no key-value pairs, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears all elements from the map. That means, after calling clear(),
     * the return of size() should be 0, and the data structure should appear
     * to be "empty".
     */
    @Override
    public void clear() {
        // Implement me!
        for (int i = 0; i < capacity; i++) buckets.get(i).clear();
        size = 0;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value
     * is replaced by the specified value.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the payload data value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no such key
     */
    @Override
    public V put(K key, V value) {
        // Implement me!

        if ((double)size / capacity >= MAX_LOAD_FACTOR) {
            resize();
        }

        int bucketIndex = index(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(bucketIndex);

        // check if key already exists in the bucket
        for (Entry<K, V> entry : bucket) {
            if (keysEqual(entry.getKey(), key)) {
                // key exists, update value and return old value
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
        }

        // key doesn't exist, add new entry
        bucket.add(new Entry<>(key, value));
        size++;
        return null;
    }

    /**
     * Looks up the specified key in this map, returning its associated value
     * if such key exists.
     *
     * @param key the key with which the specified value is to be associated
     * @return the value associated with key, or null if there was no such key
     */
    @Override
    public V get(K key) {
        // Implement me!
        int bucketIndex = index(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(bucketIndex);

        // Search for key in the bucket
        for (Entry<K, V> entry : bucket) {
            if (keysEqual(entry.getKey(), key)) {
                return entry.getValue();
            }
        }

        return null; // Key not found
    }

    /**
     * Looks up the specified key in this map, and removes the key-value pair
     * if the key exists.
     *
     * @param key the key with which the specified value is to be associated
     * @return the value associated with key, or null if there was no such key
     */
    @Override
    public V remove(K key) {
        // Implement me!
        int bucketIndex = index(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(bucketIndex);

        // Search for key in the bucket and remove it
        for (Entry<K, V> entry : bucket) {
            if (keysEqual(entry.getKey(), key)) {
                V oldValue = entry.getValue();
                bucket.remove(entry);
                size--;
                return oldValue;
            }
        }
        return null;// Key not found
    } 

}
