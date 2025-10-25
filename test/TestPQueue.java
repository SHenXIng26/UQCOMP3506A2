/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 */

import uq.comp3506.a2.structures.Entry;
import uq.comp3506.a2.structures.Heap;

public class TestPQueue {

    public static void main(String[] args) {
        System.out.println("Testing the Heap-based Priority Queue Class...");
     
        Heap<Integer, String> pq = new Heap<>();

        // Test 1: Basic insertion and verify min element
        System.out.println("\n=== Test 1: Basic Insertion ===");
        pq.insert(5, "Five");
        pq.insert(3, "Three");
        pq.insert(7, "Seven");

        Entry<Integer, String> min = pq.peekMin();
        System.out.println("After inserting 5, 3, 7:");
        System.out.println("Peek min: Key=" + min.getKey() + ", Value=" + min.getValue());
        System.out.println("Expected: Key=3, Value=Three");
        System.out.println("Test 1 passed: " + (min.getKey() == 3 && min.getValue().equals("Three")));

        // Test 2: Insert smaller key than current min
        System.out.println("\n=== Test 2: Insert Smaller Key ===");
        pq.insert(1, "One");
        min = pq.peekMin();
        System.out.println("After inserting key=1:");
        System.out.println("Peek min: Key=" + min.getKey() + ", Value=" + min.getValue());
        System.out.println("Expected: Key=1, Value=One");
        System.out.println("Test 2 passed: " + (min.getKey() == 1 && min.getValue().equals("One")));

        // test 3. check the size function true
        System.out.println("the size of the heap is " + pq.size());

        // test 4. check the removeMin function
        pq.removeMin();
        min = pq.peekMin();
        System.out.println("After removing min: Key=" + min.getKey() + ", Value=" + min.getValue());

        // reset the heap
        pq.clear();
        System.out.println("is pq empty? " + pq.isEmpty());

        // test 5
        System.out.println("Testing downHeap Function...");

        // Test 1: Simple downHeap scenario
        System.out.println("\n=== Test 5: Simple downHeap ===");
        pq.insert(10, "Ten");
        pq.insert(20, "Twenty");
        pq.insert(15, "Fifteen");

        System.out.println("Initial heap structure:");
        //pq.printHeap();

        // Remove root to trigger downHeap
        Entry<Integer, String> removed = pq.removeMin();
        System.out.println("Removed root: " + removed.getKey() + " -> " + removed.getValue());
        System.out.println("Heap after removal (downHeap should have been called):");
        //pq.printHeap();

        Entry<Integer, String> newMin = pq.peekMin();
        System.out.println("New min: " + newMin.getKey() + " -> " + newMin.getValue());
        System.out.println("Test 1 passed: " + (newMin.getKey() == 15));

    }

}
