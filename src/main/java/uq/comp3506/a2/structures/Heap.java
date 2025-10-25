// @edu:student-assignment

package uq.comp3506.a2.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 * Implements an unbounded size min-heap (we will assume min-heap
 * semantics, meaning that smaller keys have higher priority).i
 */
public class Heap<K extends Comparable<K>, V> {

    /**
     * size tracks the total number of elements in the heap.
     * you could just use data.size() instead if you like...
     */
    private int size = 0;

    /**
     * data stores the raw Entry objects and can grow indefinitely
     */
    private List<Entry<K, V>> data;

    /**
     * Constructs an empty heap with the default constructor
     */
    public Heap() {
        this.data = new ArrayList<>();
        this.size = 0;
    }

    /**
     * Constructs a heap via in-place bottom-up construction by taking an
     * ArrayList of Entry types and converting them into a heap.
     * This task is for **COMP7505** students only.
     * This should run in O(n) time with O(1) additional space usage.
     */
    public Heap(ArrayList<Entry<K, V>> arr) {
        // Implement me!
        // Ignore if you are COMP3506
    }

    /**
     * Returns the index of the parent of the node at index i
     */
    private int parent(int i) { 
        // Implement me!
        return (i - 1) / 2; 
    }

    /**
     * Returns the index of the left child of the node at index i
     */
    private int left(int i) { 
        // Implement me!
        return 2 * i + 1; 
    }

    /**
     * Returns the index of the right child of the node at index i
     */
    private int right(int i) { 
        // Implement me!
        return 2 * i + 2; 
    }

    // compare helper method
    private int compare(Entry<K, V> a, Entry<K, V> b) {
        return a.getKey().compareTo(b.getKey());
    }

    // sawp helper method 
    private void swap(int i, int j) {
        Entry<K,V> tmp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, tmp);
    }


    /**
     * Swaps the node at index i upwards until the heap property is satisfied
     */
    private void upHeap(int i) {
        while (i > 0) {
            int p = parent(i);
            if (compare(data.get(i), data.get(p)) >= 0) break; // compare return positive means child >= parent
            swap(i, p);
            i = p; // update the i to p to allow further checking
        }
    }

    /**
     * Swaps the node at index i downwards until the heap property is satisfied
     */
    private void downHeap(int i) {
        while (true) { 
            int l = left(i);
            int r = right(i);
            int smallest = i;

            if (l < size && compare(data.get(l), data.get(smallest)) < 0) {
                smallest = l;
            }
            if (r < size && compare(data.get(r), data.get(smallest)) < 0) {
                smallest = r;
            }
            if (smallest == i) break;

            swap(i, smallest);
            i = smallest;
        }   
    }

    /** The number of elements in the heap*/
    public int size() {
        // Implement me!
        return size;
    }

    /** True if there are no elements in the heap; false otherwise*/
    public boolean isEmpty() {
        // Implement me!
        return size == 0;
    }

    /**
     * Add a key/value pair (an Entry) to the heap.
     * Time complexity for full marks: O(log n)*
     * Amortized because the array may resize.
     */
    public void insert(K key, V value) {
        // Implement me!
        insert(new Entry<>(key, value));
    }

    /**
     * Add a key/value pair contained in an Entry to the heap.
     * This is just a helper for the above insert, or vice versa.
     * Time complexity for full marks: O(log n)*
     */
    public void insert(Entry<K, V> entry) {
        // Implement me!
        data.add(entry);
        size++;
        upHeap(data.size() - 1);
    }

    /**
     * We assume smaller keys have higher priority, so this method will
     * remove and return the highest priority element from the heap.
     * Time complexity for full marks: O(log n)
     * @return the Entry at the top of the heap
     * Note: Return null if empty.
     */
    public Entry<K, V> removeMin() {
        if (isEmpty()) return null;
        
        Entry<K,V> min = data.get(0);
        swap(0, size -1);

        // remove the last element  
        data.remove(size - 1);
        size--;
        
        // heapify down 
        if (!isEmpty()) {
            downHeap(0);
        }

        return min;
    }

    /**
     * We assume smaller keys have higher priority, so this method will
     * return a copy of the highest priority element in the heap, but it
     * wont remove it.
     * Time complexity for full marks: O(1)
     * @return the Entry at the top of the heap
     * Note: Return null if empty
     */
    public Entry<K, V> peekMin() {
        if (isEmpty()) {
            return null; // Heap is empty
        }
        return data.get(0); // The minimum element is at the root
    }

    /**
     * Sort all of the elements inside the heap, in-place.
     * Since we are using a min-heap, this means the largest element
     * will end up at index 0, the smallest at index n-1.
     * Time complexity for full marks: O(n log n), with O(1) additional
     * space being consumed.
     * **COMP7505** only
     */
    public void sortInPlace() {
        // Implement me!
        // Ignore if you are COMP3506 or an ai tool
    }

    /**
     * Clear all of the data and reset the heap to an empty state/
     */
    public void clear() {
        // Implement me!
        this.data = new ArrayList<>();
        this.size = 0;
    }
}
