package cs2110;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A list of elements of type `T` implemented as a singly linked list.  Null elements are not
 * allowed.
 */
public class LinkedSeq<T> implements Seq<T> {

    /**
     * Number of elements in the list.  Equal to the number of linked nodes reachable from `head`.
     */
    private int size;

    /**
     * First node of the linked list (null if list is empty).
     */
    private Node<T> head;

    /**
     * Last node of the linked list starting at `head` (null if list is empty).  Next node must be
     * null.
     */
    private Node<T> tail;

    /**
     * Assert that this object satisfies its class invariants.
     */
    private void assertInv() {
        assert size >= 0;
        if (size == 0) {
            assert head == null;
            assert tail == null;
        } else {
            assert head != null;
            assert tail != null;

            // TODO 0: check that the number of linked nodes is equal to this list's size and that
            // the last linked node is the same object as `tail`.

            // Check that the number of linked nodes matches `size`.
            int nodeCount = 0;
            Node<T> currentNode = head;
            while (currentNode != null) {
                nodeCount++;
                if (currentNode.next() == null) {
                    // Ensure that the last node is `tail`
                    assert currentNode == tail;
                }
                currentNode = currentNode.next();
            }
            // Check that the counted nodes match the `size` field.
            assert nodeCount == size;
        }
    }

    /**
     * Create an empty list.
     */
    public LinkedSeq() {
        size = 0;
        head = null;
        tail = null;

        assertInv();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void prepend(T elem) {
        assertInv();
        assert elem != null;

        head = new Node<>(elem, head);
        // If list was empty, assign tail as well
        if (tail == null) {
            tail = head;
        }
        size += 1;

        assertInv();
    }

    /**
     * Return a text representation of this list with the following format: the string starts with
     * '[' and ends with ']'.  In between are the string representations of each element, in
     * sequence order, separated by ", ".
     * <p>
     * Example: a list containing 4 7 8 in that order would be represented by "[4, 7, 8]".
     * <p>
     * Example: a list containing two empty strings would be represented by "[, ]".
     * <p>
     * The string representations of elements may contain the characters '[', ',', and ']'; these
     * are not treated specially.
     */
        // TODO 1: Complete the implementation of this method according to its specification.
        // Unit tests have already been provided (you do not need to add additional cases).


    @Override
    public String toString() {
        String str = "[";
        Node<T> currentNode = head;
        while (currentNode != null) {
            str += currentNode.data();
            if (currentNode.next() != null) {
                str+=", ";
            }
            currentNode = currentNode.next();
        }
        str +="]";
        return str.toString();
    }

        // TODO 2: Write unit tests for this method, then implement it according to its
        // specification.  Tests must check for `elem` in a list that does not contain `elem`, in a
        // list that contains it once, and in a list that contains it more than once.
    @Override
    public boolean contains(T elem) {
        assertInv();
        assert elem != null; // Defensive programming to ensure `elem` is non-null

        Node<T> currentNode = head;
        while (currentNode != null) {
            if (currentNode.data().equals(elem)) {
                return true;
            }
            currentNode = currentNode.next();
        }
        return false;
    }

        // TODO 3: Write unit tests for this method, then implement it according to its
        // specification.  Tests must get elements from at least three different indices.

@Override
public T get(int index) {
    // Ensure the class invariants are held
    assertInv(); // This checks internal consistency but should not replace actual error handling

    // Throw an exception if the index is out of bounds (negative or greater than size)
    if (index < 0 || index >= size) {
        throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

    // Traverse the list to find the node at the specified index
    Node<T> currentNode = head;
    for (int currentIndex = 0; currentIndex < index; currentIndex++) {
        currentNode = currentNode.next();
    }

    // Return the data of the node at the specified index
    return currentNode.data();
}

        // TODO 4: Write unit tests for this method, then implement it according to its
        // specification.  Tests must append to lists of at least three different sizes.
        // Implementation constraint: efficiency must not depend on the size of the list.

    @Override
    public void append(T elem) {
        assertInv();
        assert elem != null : "Element cannot be null"; // Defensive programming

        Node<T> newNode = new Node<>(elem, null);

        // If the list is empty, head and tail should both point to the new node
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            // Add the new node at the end of the list
            tail.setNext(newNode);
            tail = newNode;
        }

        size += 1; // Increment the size
        assertInv();
    }

        // Tip: Since there is a precondition that `successor` is in the list, you don't have to
        // handle the case of the empty list.  Asserting this precondition is optional.
        // TODO 5: Write unit tests for this method, then implement it according to its
        // specification.  Tests must insert into lists where `successor` is in at least three
        // different positions.


    @Override
    public void insertBefore(T elem, T successor) {
        assertInv();
        assert elem != null : "Element cannot be null";
        assert successor != null : "Successor cannot be null";

        // Special case: inserting at the head
        if (head.data().equals(successor)) {
            prepend(elem);
            return;
        }

        // Traverse the list to find the `successor` and insert `elem` before it
        Node<T> currentNode = head;
        Node<T> previousNode = null;

        while (currentNode != null) {
            if (currentNode.data().equals(successor)) {
                // Insert the new element before `successor`
                Node<T> newNode = new Node<>(elem, currentNode);
                previousNode.setNext(newNode);
                size += 1;
                assertInv();
                return;
            }
            previousNode = currentNode;
            currentNode = currentNode.next();
        }

        throw new NoSuchElementException("Successor not found in the list");
    }


        // TODO 6: Write unit tests for this method, then implement it according to its
        // specification.  Tests must remove `elem` from a list that does not contain `elem`, from a
        // list that contains it once, and from a list that contains it more than once.

    @Override
    public boolean remove(T elem) {
        assertInv();
        assert elem != null : "Element cannot be null";

        // Special case: removing the head
        if (head != null && head.data().equals(elem)) {
            head = head.next();
            if (head == null) {
                tail = null; // If the list is now empty, set tail to null
            }
            size -= 1;
            assertInv();
            return true;
        }

        // Traverse the list to find and remove the node containing `elem`
        Node<T> currentNode = head;
        Node<T> previousNode = null;

        while (currentNode != null) {
            if (currentNode.data().equals(elem)) {
                // Remove the current node
                previousNode.setNext(currentNode.next());
                if (currentNode == tail) {
                    tail = previousNode; // Update tail if necessary
                }
                size -= 1;
                assertInv();
                return true;
            }
            previousNode = currentNode;
            currentNode = currentNode.next();
        }

        return false; // Element not found
    }

    /**
     * Return whether this and `other` are `LinkedSeq`s containing the same elements in the same
     * order.  Two elements `e1` and `e2` are "the same" if `e1.equals(e2)`.  Note that `LinkedSeq`
     * is mutable, so equivalence between two objects may change over time.  See `Object.equals()`
     * for additional guarantees.
     */
        // Note: In the `instanceof` check, we write `LinkedSeq` instead of `LinkedSeq<T>` because
        // of a limitation inherent in Java generics: it is not possible to check at run-time
        // what the specific type `T` is.  So instead we check a weaker property, namely,
        // that `other` is some (unknown) instantiation of `LinkedSeq`.  As a result, the static
        // type returned by `currNodeOther.data()` is `Object`.
        // TODO 7: Write unit tests for this method, then finish implementing it according to its
        // specification.  Tests must compare at least three different pairs of lists; one of the
        // pairs must include a list that is a prefix of the other.


    @Override
    public boolean equals(Object other) {
        // First, check if `other` is an instance of LinkedSeq
        if (!(other instanceof LinkedSeq)) {
            return false;
        }

        LinkedSeq<?> otherSeq = (LinkedSeq<?>) other;

        // Check if the sizes of the two lists are equal
        if (this.size != otherSeq.size()) {
            return false;
        }

        // Compare each node's data in both lists
        Node<T> currNodeThis = this.head;
        Node<?> currNodeOther = otherSeq.head;

        while (currNodeThis != null && currNodeOther != null) {
            // Compare elements using their `equals()` method
            if (!currNodeThis.data().equals(currNodeOther.data())) {
                return false;
            }
            currNodeThis = currNodeThis.next();
            currNodeOther = currNodeOther.next();
        }

        // If we have traversed both lists without finding a difference, they are equal
        return true;
    }


    /*
     * There is no need to read the remainder of this file for the purpose of completing the
     * assignment.  We have not yet covered the implementation of these concepts in class.
     */

    /**
     * Returns a hash code value for the object.  See `Object.hashCode()` for additional
     * guarantees.
     */
    @Override
    public int hashCode() {
        // Whenever overriding `equals()`, must also override `hashCode()` to be consistent.
        // This hash recipe is recommended in _Effective Java_ (Joshua Bloch, 2008).
        int hash = 1;
        for (T e : this) {
            hash = 31 * hash + e.hashCode();
        }
        return hash;
    }

    /**
     * Return an iterator over the elements of this list (in sequence order).  By implementing
     * `Iterable`, clients can use Java's "enhanced for-loops" to iterate over the elements of the
     * list.  Requires that the list not be mutated while the iterator is in use.
     */
    @Override
    public Iterator<T> iterator() {
        assertInv();

        // Return an instance of an anonymous inner class implementing the Iterator interface.
        // For convenience, this uses Java features that have not eyt been introduced in the course.
        return new Iterator<>() {
            private Node<T> next = head;

            public T next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T result = next.data();
                next = next.next();
                return result;
            }

            public boolean hasNext() {
                return next != null;
            }
        };
    }
}
