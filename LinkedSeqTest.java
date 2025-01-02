package cs2110;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedSeqTest {

    // Helper functions for creating lists used by multiple tests.  By constructing strings with
    // `new`, more likely to catch inadvertent use of `==` instead of `.equals()`.

    /**
     * Creates [].
     */
    static Seq<String> makeList0() {
        return new LinkedSeq<>();
    }

    /**
     * Creates ["A"].  Only uses prepend.
     */
    static Seq<String> makeList1() {
        Seq<String> ans = new LinkedSeq<>();
        ans.prepend(new String("A"));
        return ans;
    }

    /**
     * Creates ["A", "B"].  Only uses prepend.
     */
    static Seq<String> makeList2() {
        Seq<String> ans = new LinkedSeq<>();
        ans.prepend(new String("B"));
        ans.prepend(new String("A"));
        return ans;
    }

    /**
     * Creates ["A", "B", "C"].  Only uses prepend.
     */
    static Seq<String> makeList3() {
        Seq<String> ans = new LinkedSeq<>();
        ans.prepend(new String("C"));
        ans.prepend(new String("B"));
        ans.prepend(new String("A"));
        return ans;
    }

    /**
     * Creates a list containing the same elements (in the same order) as array `elements`.  Only
     * uses prepend.
     */
    static <T> Seq<T> makeList(T[] elements) {
        Seq<T> ans = new LinkedSeq<>();
        for (int i = elements.length; i > 0; i--) {
            ans.prepend(elements[i - 1]);
        }
        return ans;
    }

    @DisplayName("WHEN a LinkedSeq is first constructed, THEN it should be empty.")
    @Test
    void testConstructorSize() {
        Seq<String> list = new LinkedSeq<>();
        assertEquals(0, list.size());
    }

    @DisplayName("GIVEN a LinkedSeq, WHEN an element is prepended, " +
            "THEN its size should increase by 1 each time.")
    @Test
    void testPrependSize() {
        // Note: List creation helper functions use prepend.
        Seq<String> list;

        // WHEN an element is prepended to an empty list
        list = makeList1();
        assertEquals(1, list.size());

        // WHEN an element is prepended to a list whose head and tail are the same
        list = makeList2();
        assertEquals(2, list.size());

        // WHEN an element is prepended to a list with no nodes between its head and tail
        list = makeList3();
        assertEquals(3, list.size());
    }

    @DisplayName("GIVEN a LinkedSeq containing a sequence of values, " +
            "THEN its string representation should include the string representations of its " +
            "values, in order, separated by a comma and space, all enclosed in square brackets.")
    @Test
    void testToString() {
        Seq<String> list;

        // WHEN empty
        list = makeList0();
        assertEquals("[]", list.toString());

        // WHEN head and tail are the same
        list = makeList1();
        assertEquals("[A]", list.toString());

        // WHEN there are no nodes between head and tail
        list = makeList2();
        assertEquals("[A, B]", list.toString());

        // WHEN there are at least 3 nodes
        list = makeList3();
        assertEquals("[A, B, C]", list.toString());

        // WHEN values are not strings
        Seq<Integer> intList = makeList(new Integer[]{1, 2, 3, 4});
        assertEquals("[1, 2, 3, 4]", intList.toString());
    }

    @DisplayName("GIVEN a LinkedSeq, WHEN checking if it contains an element, THEN it should return the correct result.")
    @Test
    void testContains() {
        Seq<String> list = makeList0();  // Empty list

        // Empty list
        assertFalse(list.contains("A"), "An empty list should not contain any element");

        // List with one element
        list = makeList1();
        assertTrue(list.contains("A"), "A list with one element should contain that element");
        assertFalse(list.contains("B"), "A list with one element should not contain a different element");

        // List with multiple elements
        list = makeList3();  // ["A", "B", "C"]
        assertTrue(list.contains("A"), "A list with multiple elements should contain 'A'");
        assertTrue(list.contains("B"), "A list with multiple elements should contain 'B'");
        assertFalse(list.contains("D"), "A list with multiple elements should not contain 'D'");
    }

    @DisplayName("GIVEN a LinkedSeq, WHEN getting an element by index, THEN it should return the correct element.")
    @Test
    void testGet() {
        Seq<String> list = makeList3();  // ["A", "B", "C"]

        // Test valid indices
        assertEquals("A", list.get(0), "The element at index 0 should be 'A'");
        assertEquals("B", list.get(1), "The element at index 1 should be 'B'");
        assertEquals("C", list.get(2), "The element at index 2 should be 'C'");

        // Test out-of-bound indices
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1), "Negative index should throw IndexOutOfBoundsException");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3), "Index greater than size should throw IndexOutOfBoundsException");
    }

    @DisplayName("GIVEN a LinkedSeq, WHEN an element is appended, THEN it should be added at the end of the list.")
    @Test
    void testAppend() {
        LinkedSeq<String> list = new LinkedSeq<>();

        // Append to an empty list
        list.append("A");
        assertEquals(1, list.size(), "After appending to an empty list, size should be 1");
        assertEquals("A", list.get(0), "The first element should be 'A'");

        // Append to a list with one element
        list.append("B");
        assertEquals(2, list.size(), "After appending, size should be 2");
        assertEquals("B", list.get(1), "The second element should be 'B'");

        // Append to a list with multiple elements
        list.append("C");
        assertEquals(3, list.size(), "After appending, size should be 3");
        assertEquals("C", list.get(2), "The third element should be 'C'");
    }

    @DisplayName("GIVEN a LinkedSeq, WHEN inserting an element before another, THEN it should be inserted at the correct position.")
    @Test
    void testInsertBefore() {
        LinkedSeq<String> list = new LinkedSeq<>();
        list.append("A");
        list.append("B");
        list.append("C");

        // Insert before the head
        list.insertBefore("X", "A");
        assertEquals(4, list.size(), "After inserting, size should be 4");
        assertEquals("X", list.get(0), "First element should be 'X'");

        // Insert before a middle element
        list.insertBefore("Y", "C");
        assertEquals(5, list.size(), "After inserting, size should be 5");
        assertEquals("Y", list.get(3), "Fourth element should be 'Y'");

        // Insert before the last element
        list.insertBefore("Z", "C");
        assertEquals(6, list.size(), "After inserting, size should be 6");
        assertEquals("Z", list.get(4), "Fifth element should be 'Z'");

        // Test inserting before a non-existent element (should throw exception)
        assertThrows(NoSuchElementException.class, () -> list.insertBefore("Fail", "D"), "Inserting before a non-existent element should throw an exception");
    }

    @DisplayName("GIVEN a LinkedSeq, WHEN removing an element, THEN it should be removed from the list.")
    @Test
    void testRemove() {
        LinkedSeq<String> list = new LinkedSeq<>();
        list.append("A");
        list.append("B");
        list.append("C");

        // Remove the head
        assertTrue(list.remove("A"), "Removing the head should return true");
        assertEquals(2, list.size(), "Size should be 2 after removing the head");
        assertEquals("B", list.get(0), "New head should be 'B'");

        // Remove a middle element
        assertTrue(list.remove("B"), "Removing a middle element should return true");
        assertEquals(1, list.size(), "Size should be 1 after removing the middle element");
        assertEquals("C", list.get(0), "New head should be 'C'");

        // Remove the tail
        assertTrue(list.remove("C"), "Removing the tail should return true");
        assertEquals(0, list.size(), "Size should be 0 after removing the last element");

        // Remove a non-existent element
        assertFalse(list.remove("D"), "Removing a non-existent element should return false");
    }

    @DisplayName("GIVEN two LinkedSeqs, WHEN checking equality, THEN they should be equal if they have the same elements in the same order.")
    @Test
    void testEquals() {
        LinkedSeq<Integer> list1 = new LinkedSeq<>();
        LinkedSeq<Integer> list2 = new LinkedSeq<>();

        // Both empty lists
        assertTrue(list1.equals(list2), "Two empty lists should be equal");

        // Lists with one element
        list1.append(1);
        list2.append(1);
        assertTrue(list1.equals(list2), "Lists with the same single element should be equal");

        // Lists with different sizes
        list1.append(2);
        assertFalse(list1.equals(list2), "Lists with different sizes should not be equal");

        // Lists with the same elements
        list2.append(2);
        assertTrue(list1.equals(list2), "Lists with the same elements should be equal");

        // Lists with the same elements but different order
        list2.remove(2);
        list2.append(3);
        assertFalse(list1.equals(list2), "Lists with elements in a different order should not be equal");
    }

    // TODO: Add new test cases here as you implement each method in `LinkedSeq`.  To save typing,
    // you may combine multiple tests for the _same_ method in the same @Test procedure, but be sure
    // that each test case is visibly distinct (comments are good for this, as demonstrated above).
    // You are welcome to compare against an expected `toString()` output in order to check multiple
    // aspects of the state at once (in general, later tests may make use of methods that have
    // previously been tested).  Each test procedure must describe its scenario using @DisplayName.



    /*
     * There is no need to read the remainder of this file for the purpose of completing the
     * assignment.  We have not yet covered `hashCode()` or `assertThrows()` in class.
     */

    @DisplayName("GIVEN two distinct LinkedSeqs containing equivalent values in the same order, " +
            "THEN their hash codes should be the same.")
    @Test
    void testHashCode() {
        // WHEN empty
        assertEquals(makeList0().hashCode(), makeList0().hashCode());

        // WHEN head and tail are the same
        assertEquals(makeList1().hashCode(), makeList1().hashCode());

        // WHEN there are no nodes between head and tail
        assertEquals(makeList2().hashCode(), makeList2().hashCode());

        // WHEN there are at least 3 nodes
        assertEquals(makeList3().hashCode(), makeList3().hashCode());
    }

    @DisplayName("GIVEN a LinkedSeq, THEN its iterator should yield its values in order " +
            "AND it should stop yielding after the last value.")
    @Test
    void testIterator() {
        Seq<String> list;
        Iterator<String> it;

        // WHEN empty
        list = makeList0();
        it = list.iterator();
        assertFalse(it.hasNext());
        Iterator<String> itAlias = it;
        assertThrows(NoSuchElementException.class, () -> itAlias.next());

        // WHEN head and tail are the same
        list = makeList1();
        it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertFalse(it.hasNext());

        // WHEN there are no nodes between head and tail
        list = makeList2();
        it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertTrue(it.hasNext());
        assertEquals("B", it.next());
        assertFalse(it.hasNext());
    }
}
