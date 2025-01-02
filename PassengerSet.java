package cs2110;

/**
 * A mutable set of passengers on a flight.
 */
public class PassengerSet {

    /**
     * Maximum size of any PassengerSet.
     */
    public static final int MAX_CAPACITY = 40;

    // Implementation: PassengerSet is implemented as a resizable array data structure.
    // Implementation constraint: do not use any classes from java.util.
    // Implementation constraint: assert preconditions for all method parameters and assert that the
    //     invariant is satisfied at least at the end of every constructor or method that mutates a
    //     field.

    /**
     * Array containing the passengers in the set.  Elements `passengers[0..size-1]` contain the
     * distinct passengers in this set, none of which is null.  All elements in `passengers[size..]`
     * are null. The length of `passengers` is the current capacity of the data structure. That
     * length is initially 10, can double in size to meet passenger demand, but should never exceed
     * 40. Two passengers `p1` and `p2` are distinct if `p1.equals(p2)` is false. You are not
     * required to write `equals()` methods for this assignment.
     */
    private Passenger[] passengers;

    /**
     * The number of distinct passengers in this set. Must be non-negative and no greater than
     * `passengers.length`. This size is used in other classes, e.g., to determine what airplane to
     * use for a flight.
     */
    private int size;

    /**
     * Assert that a Passenger object satisfies its class invariants.
     */
    private void assertInv() {
        assert passengers != null;
        assert passengers.length >= 10;
        assert passengers.length <= MAX_CAPACITY;
        assert size >= 0;
        assert size <= passengers.length;

        // Make sure no null elements
        for (int i = 0; i < size; i++) {
            assert passengers[i] != null;

            // Check that passengers are all distinct
            for (int j = i + 1; j < size; j++) {
                assert !passengers[i].equals(passengers[j]);
            }
        }

        // Make sure everything else is null
        for (int i = size; i < passengers.length; i += 1) {
            assert passengers[i] == null;
        }
    }

    /**
     * Create an empty set of passengers.
     * Initial capacity of 10, no passengers.
     */
    public PassengerSet() {
        passengers = new Passenger[10];
        size = 0;
        assertInv();
    }

    /**
     * Return the number of passengers in this set.
     */
    public int size() {
        return size;
    }

    /**
     * Return a new array containing all passengers in this set.
     */
    public Passenger[] passengers() {
        Passenger[] result = new Passenger[size];
        for (int i = 0; i < size; i++) {
            result[i] = passengers[i];
        }
        return result;
    }

    /**
     * Return the capacity of the backing array for this set. This is a helper method (for use by
     * the class implementer, not by clients), but it has default/package visibility to enable
     * testing.
     */
    int capacity() {
        return passengers.length;
    }

    /**
     * Return whether a given passenger is successfully added to this set. If this set was already
     * full, simply return `false` without modifying the set. Effect: Add given passenger to the
     * set.  Requires that the given passenger is not null and is not already in this set.
     */
    public boolean add(Passenger passenger) {
        assert passenger != null : "Passenger cannot be null.";
        assert !contains(passenger) : "Passenger is already in the set.";

        if (size == passengers.length) {
            if (size >= MAX_CAPACITY) {
                return false;  // No more capacity available
            }
            resize();  // Double the array size if there's still room
        }

        passengers[size] = passenger;
        size++;
        assertInv();
        return true;

    }

    /**
     * Helper method to resize the backing array by doubling its size.
     */
    private void resize() {
        int newCapacity = Math.min(passengers.length * 2, MAX_CAPACITY);  // Double the size but not beyond MAX_CAPACITY
        Passenger[] newPassengers = new Passenger[newCapacity];
        for (int i = 0; i < size; i++) {
            newPassengers[i] = passengers[i];  // Copy existing passengers
        }
        passengers = newPassengers;
    }

    /**
     * Return whether this PassengerSet contains a given Passenger.
     */
    public boolean contains(Passenger passenger) {
        assert passenger != null : "Passenger cannot be null.";
        for (int i = 0; i < size; i++) {
            if (passengers[i].equals(passenger)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether a given passenger is successfully removed from this set. Effect: If the given
     * passenger is in this PassengerSet, remove the passenger from the set and return true.
     * Otherwise, return false. Requires that the given passenger is not null.
     */
    public boolean remove(Passenger passenger) {
        assert passenger != null : "Passenger cannot be null.";
        for (int i = 0; i < size; i++) {
            if (passengers[i].equals(passenger)) {
                // Shift the remaining elements to fill the gap
                for (int j = i; j < size - 1; j++) {
                    passengers[j] = passengers[j + 1];
                }
                passengers[size - 1] = null;  // Clear the last element
                size--;
                assertInv();  // Check that the invariant holds
                return true;
            }
        }
        return false;  // Passenger not found
    }

    /**
     * Return a String representation of this PassengerSet. The returned string will be enclosed in
     * curly braces, and will contain the String representation of each Passenger in the set,
     * separated by a semicolon and a space ("; "). The order of Passenger String representations in
     * the returned String is not specified.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        for (int i = 0; i < size; i += 1) {
            if (i > 0) {
                builder.append("; ");
            }
            builder.append(passengers[i]);
        }
        builder.append("}");
        return builder.toString();
    }
}
