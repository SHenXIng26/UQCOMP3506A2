/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 */

import uq.comp3506.a2.structures.UnorderedMap;

public class TestUnorderedMap {

    static void check(String name, Object got, Object want) {
        boolean ok = (got == null ? want == null : got.equals(want));
        System.out.println((ok ? "[PASS] " : "[FAIL] ") +
                name + " -> got=" + got + ", expected=" + want);
    }

    static void checkTrue(String name, boolean cond) {
        System.out.println((cond ? "[PASS] " : "[FAIL] ") + name);
    }

    public static void main(String[] args) {
        System.out.println("Testing the UnorderedMap Class...");

        // Test 1: Basic operations
        System.out.println("\n=== Test 1: Basic Operations ===");
        UnorderedMap<String, Integer> map = new UnorderedMap<>();

        check("size initially 0", map.size(), 0);
        checkTrue("isEmpty initially", map.isEmpty());
        check("get(a) on empty is null", map.get("a"), null);

        System.out.println("\n=== put (insert new) ===");
        check("put(a,1) returns null (new key)", map.put("a", 1), null);
        check("size after 1 insert", map.size(), 1);
        check("get(a) == 1", map.get("a"), 1);

        System.out.println("\n=== put (overwrite existing) ===");
        check("put(a,2) returns old value 1", map.put("a", 2), 1);
        check("size unchanged after overwrite", map.size(), 1);
        check("get(a) == 2", map.get("a"), 2);

        System.out.println("\n=== more inserts ===");
        check("put(b,20) -> null", map.put("b", 20), null);
        check("put(c,30) -> null", map.put("c", 30), null);
        check("size now 3", map.size(), 3);
        check("get(b) == 20", map.get("b"), 20);
        check("get(c) == 30", map.get("c"), 30);

        System.out.println("\n=== remove existing key ===");
        check("remove(b) returns 20", map.remove("b"), 20);
        check("size after remove(b) == 2", map.size(), 2);
        check("get(b) now null", map.get("b"), null);

        System.out.println("\n=== remove non-existing key ===");
        check("remove(nope) returns null", map.remove("nope"), null);
        check("size unchanged == 2", map.size(), 2);

        // Test 2: Hash collisions
        System.out.println("\n=== Test 2: Hash Collisions ===");
        UnorderedMap<String, Integer> collisionMap = new UnorderedMap<>(2); // Small capacity to force collisions

        collisionMap.put("x", 1);
        collisionMap.put("y", 2);
        collisionMap.put("z", 3);

        check("collision: get(x) == 1", collisionMap.get("x"), 1);
        check("collision: get(y) == 2", collisionMap.get("y"), 2);
        check("collision: get(z) == 3", collisionMap.get("z"), 3);
        check("collision: size == 3", collisionMap.size(), 3);

        // Remove from collision chain
        check("collision: remove(y) returns 2", collisionMap.remove("y"), 2);
        check("collision: get(y) after remove is null", collisionMap.get("y"), null);
        check("collision: size after remove == 2", collisionMap.size(), 2);
        check("collision: get(x) still works", collisionMap.get("x"), 1);
        check("collision: get(z) still works", collisionMap.get("z"), 3);

        // Test 3: Null key handling
        System.out.println("\n=== Test 3: Null Key Handling ===");
        UnorderedMap<String, Integer> nullMap = new UnorderedMap<>();

        check("null: put(null,42) returns null", nullMap.put(null, 42), null);
        check("null: get(null) returns 42", nullMap.get(null), 42);
        check("null: size == 1", nullMap.size(), 1);

        check("null: put(null,100) returns old value 42", nullMap.put(null, 100), 42);
        check("null: get(null) returns updated value 100", nullMap.get(null), 100);

        check("null: remove(null) returns 100", nullMap.remove(null), 100);
        check("null: get(null) after remove returns null", nullMap.get(null), null);
        check("null: size after remove == 0", nullMap.size(), 0);

        // Test 5: Clear functionality
        System.out.println("\n=== Test 5: Clear Functionality ===");
        UnorderedMap<String, Integer> clearMap = new UnorderedMap<>();
        clearMap.put("key1", 1);
        clearMap.put("key2", 2);
        clearMap.put("key3", 3);

        clearMap.clear();
        check("clear: size after clear == 0", clearMap.size(), 0);
        checkTrue("clear: isEmpty after clear", clearMap.isEmpty());
        check("clear: get(key1) after clear is null", clearMap.get("key1"), null);
        check("clear: get(key2) after clear is null", clearMap.get("key2"), null);

        // Test 6: Complex operations sequence
        System.out.println("\n=== Test 6: Complex Operations Sequence ===");
        UnorderedMap<String, Integer> complexMap = new UnorderedMap<>();

        complexMap.put("a", 1);
        complexMap.put("b", 2);
        complexMap.put("c", 3);

        check("complex: remove b returns 2", complexMap.remove("b"), 2);
        check("complex: put d returns null", complexMap.put("d", 4), null);
        check("complex: update a returns 1", complexMap.put("a", 10), 1);

        check("complex: get a == 10", complexMap.get("a"), 10);
        check("complex: get b == null", complexMap.get("b"), null);
        check("complex: get c == 3", complexMap.get("c"), 3);
        check("complex: get d == 4", complexMap.get("d"), 4);
        check("complex: final size == 3", complexMap.size(), 3);

        System.out.println("\n✅ All test suites completed.");
    }
}