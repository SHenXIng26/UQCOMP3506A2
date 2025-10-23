/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 */

import uq.comp3506.a2.structures.Entry;
import uq.comp3506.a2.structures.Heap;

public class TestPQueue {

    public static void main(String[] args) {
        System.out.println("Testing the Heap-based Priority Queue Class...");
     
        Heap<Integer, String> pq = new Heap<>();

        // test 1: basic insertion and peek
        System.out.println("Test 1: basic insertion and peek ");
        pq.insert(5, "Five");
        pq.insert(3, "Three");
        pq.insert(7, "Seven");
        Entry<Integer, String> min = pq.peekMin();
        System.out.println("Peek min: Key=" + min.getKey() + ", Value=" + min.getValue());




    }
}
