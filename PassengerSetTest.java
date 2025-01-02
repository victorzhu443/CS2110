package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PassengerSetTest {

    @DisplayName("WHEN a new PassengerSet is created, THEN its size should be zero.")
    @Test
    void testSizeAfterCreation() {
        // Invoking the constructor should yield an empty set.
        PassengerSet passengers = new PassengerSet();
        assertEquals(0, passengers.size());
    }


    // We provide a test for toString() to help achieve full line coverage
    @DisplayName("GIVEN a PassengerSet `ps` containing Passenger `p1` and Passenger `p2`, "
            + "WHEN `ps` is queried for its String representation,"
            + "THEN it returns '{p1, p2}' or '{p2, p1}.")
    @Test
    void testToString() {
        PassengerSet passengers = new PassengerSet();
        Passenger shiny = new Passenger("Shiny", "Pteranodon");
        Passenger tiny = new Passenger("Tiny", "Pteranodon");
        passengers.add(shiny);
        passengers.add(tiny);
        assertTrue(passengers.toString().equals("{Pteranodon,Shiny; Pteranodon,Tiny}")
                || passengers.toString().equals("{Pteranodon,Tiny; Pteranodon,Shiny}"));
    }

    @DisplayName("GIVEN a new PassengerSet, WHEN it is created, THEN its size should be zero.")
    @Test
    void testPassengerSetConstructor() {
        PassengerSet passengers = new PassengerSet();
        assertEquals(0, passengers.size());
        assertEquals(10, passengers.capacity());
    }

    @DisplayName("GIVEN a PassengerSet with added and removed passengers, WHEN the size is checked, THEN the correct size should be returned.")
    @Test
    void testSizeMethod() {
        PassengerSet passengers = new PassengerSet();
        Passenger passenger1 = new Passenger("Bill", "Bob");
        Passenger passenger2 = new Passenger("Hi", "Hello");

        passengers.add(passenger1);
        passengers.add(passenger2);
        assertEquals(2, passengers.size());

        passengers.remove(passenger1);
        assertEquals(1, passengers.size());
    }

    @DisplayName("GIVEN a PassengerSet with passengers, WHEN passengers() is called, THEN it should return an array of all the passengers.")
    @Test
    void testPassengersMethod() {
        PassengerSet passengers = new PassengerSet();
        Passenger passenger1 = new Passenger("John", "Doe");
        Passenger passenger2 = new Passenger("Jane", "Smith");

        passengers.add(passenger1);
        passengers.add(passenger2);

        Passenger[] passengerArray = passengers.passengers();
        assertEquals(2, passengerArray.length);
        assertEquals(true, passengerArray[0].equals(passenger1) || passengerArray[1].equals(passenger1));
        assertEquals(true,passengerArray[0].equals(passenger2) || passengerArray[1].equals(passenger2));
    }

    @DisplayName("GIVEN a new PassengerSet, WHEN capacity is checked, THEN it should return the initial capacity of 10.")
    @Test
    void testCapacityMethod() {
        PassengerSet passengers = new PassengerSet();
        assertEquals(10, passengers.capacity());
    }

    @DisplayName("GIVEN a PassengerSet with passengers, WHEN more passengers are added beyond the initial capacity, THEN the array should resize and reflect the correct size.")
    @Test
    void testAddAndResize() {
        PassengerSet passengers = new PassengerSet();
        for (int i = 0; i < 10; i++) {
            passengers.add(new Passenger("Passenger", "Number" + i));
        }

        // At this point, the array should be at capacity
        assertEquals(10, passengers.size());
        assertEquals(10, passengers.capacity());

        // Add one more passenger to trigger resizing
        passengers.add(new Passenger("Extra", "Passenger"));
        assertEquals(11, passengers.size());
        assertEquals(20, passengers.capacity());  // Capacity should have doubled
    }

    @DisplayName("GIVEN a PassengerSet with passengers, WHEN checking if a passenger is in the set, THEN contains() should return true if the passenger is present and false if not.")
    @Test
    void testContainsMethod() {
        PassengerSet passengers = new PassengerSet();
        Passenger passenger1 = new Passenger("WAS", "UP");
        Passenger passenger2 = new Passenger("Bill", "EJOE");

        passengers.add(passenger1);
        assertTrue(passengers.contains(passenger1));
        assertFalse(passengers.contains(passenger2));  // Not added yet

        passengers.add(passenger2);
        assertEquals(true,passengers.contains(passenger2));
    }

    @DisplayName("GIVEN a PassengerSet with passengers, WHEN a passenger is removed, THEN the size should decrease and contains() should return false for the removed passenger.")
    @Test
    void testRemoveMethod() {
        PassengerSet passengers = new PassengerSet();
        Passenger passenger1 = new Passenger("John", "Doe");
        Passenger passenger2 = new Passenger("Jane", "Smith");

        passengers.add(passenger1);
        passengers.add(passenger2);
        assertEquals(2, passengers.size());

        passengers.remove(passenger1);
        assertEquals(1, passengers.size());
        assertEquals(false,passengers.contains(passenger1));
        assertEquals(true,passengers.contains(passenger2));
    }


}
