package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PassengerTest {

    @DisplayName("WHEN a Passenger object is created with a specified first name and last name "
            + "(with no leading or trailing spaces), THEN its first name will match the specified "
            + "first name AND its last name will match the specified last name AND it will nave no "
            + "reservations.")
    @Test
    void testConstructorTwoNames() {
        // the common case with one-word first and last names
        Passenger passenger = new Passenger("Micky", "Mouse");
        assertEquals("Mouse", passenger.lastName());
        assertEquals("Micky", passenger.firstName());
        assertEquals(0, passenger.flightCount());
    }

    @DisplayName("WHEN a Passenger object is created with a single specified name (with no leading "
            + "or trailing spaces), THEN its first name will be empty AND its last name will match "
            + "the specified name.")
    @Test
    void testConstructorOneName() {
        // in some cultures, people have only one name
        // also names can have spaces (regardless of culture)
        Passenger passenger = new Passenger("T Rex");
        assertEquals("T Rex", passenger.lastName());
        assertEquals("", passenger.firstName());
    }

    @DisplayName("GIVEN a passenger with a first name and last name, WHEN fullID() is called, THEN it should return 'lastName,firstName'")
    @Test
    void testFullIDWithMoreThanOneName() {
        Passenger passenger = new Passenger("Bob", "Joe");
        System.out.println(passenger.fullID());
        assertEquals("Joe,Bob", passenger.fullID());
    }

    @DisplayName("GIVEN a passenger with a single name, WHEN fullID() is called, THEN it should return the last name")
    @Test
    void testFullIDWithSingleName() {
        Passenger passenger = new Passenger("Roar");
        assertEquals("Roar", passenger.fullID());
    }

    @DisplayName("GIVEN a passenger with leading/trailing spaces in their first name, WHEN fullID() is called, THEN it should trim the spaces and return 'lastName,firstName'")
    @Test
    void testFullIDWithWhitespaceInName() {
        Passenger passenger = new Passenger(" Bob ", " Joe ");
        System.out.println(passenger.fullID());
        assertEquals("Joe,Bob", passenger.fullID());
    }

    @DisplayName("GIVEN a newly constructed Passenger,"
            + "WHEN their number of flights is incremented by a positive number `x`, "
            + "THEN the resulting number of flights should be the sum of the previous number of "
            + "flights and `x`")
    @Test
    void testIncrementReservations() {
        Passenger passenger = new Passenger("Arnie", "Argentinosaurus");

        passenger.increaseReservations(2);
        assertEquals(2, passenger.flightCount());

        passenger.increaseReservations(3);
        assertEquals(5, passenger.flightCount());
    }

    @DisplayName("GIVEN a Passenger with a positive number of reservations,"
            + "WHEN their number of flights is decremented by a positive number `x`, "
            + "THEN the resulting number of flights should be the result of the subtracting `x`"
            + "from the previous number of flights")
    @Test
    void testDecrementReservations() {
        Passenger passenger = new Passenger("Buddy", "Pteranodon");
        passenger.increaseReservations(20);

        passenger.reduceReservations(7);
        assertEquals(13, passenger.flightCount());

        passenger.reduceReservations(3);
        assertEquals(10, passenger.flightCount());
    }

    // We provide a test for `toString()` to help you achieve full method coverage.
    @DisplayName("GIVEN a Passenger, WHEN queried for its string representation, "
            + "THEN it should return the result of `fullID()`")
    @Test
    void testToString() {
        Passenger passenger = new Passenger("Selma", "Cimolestes");
        assertEquals(passenger.fullID(), passenger.toString());
    }
}
