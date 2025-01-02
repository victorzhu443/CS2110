package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FlightTest {


    @DisplayName("GIVEN a Flight, WHEN queried for its string representation, "
            + "THEN it should return a String that shows the origin, destination, departure "
            + "minute, duration (in minutes), and distance (in miles)")
    @Test
    void testToString() {
        Flight flight = new Flight("Ithaca", "Dallas", 3, 45, 120, 400);
        String expectedOutput = "Flight{Origin City: 'Ithaca', Destination City: 'Dallas', "
                + "Departure Minute: 225, Duration (mins): 120, Distance (miles): 400}";
        assertEquals(expectedOutput, flight.toString());
    }

    @DisplayName("GIVEN valid input, WHEN a flight is created, THEN it should initialize correctly")
    @Test
    void testFlightConstructor() {
        Flight flight = new Flight("NYC", "LAX", 9, 30, 300, 400);
        assertEquals("NYC", flight.origin());
        assertEquals("LAX", flight.destination());
        assertEquals(9 * 60 + 30, flight.departureTimeMin());
        assertEquals("9:30 AM",flight.departureTime());
        //formatDeparture Time test
        assertEquals("9:30 AM",flight.formatDepartureTime());
        assertEquals(300, flight.durationMin());
        assertEquals(400, flight.distanceInMiles());
    }



    @DisplayName("GIVEN two flights, WHEN layover time is checked, THEN it should return the correct tight connection status")
    @Test
    void testTightConnection() {
        Flight flight1 = new Flight("NYC", "LAX", 9, 30, 300, 400);
        Flight flight2 = new Flight("LAX", "SFO", 15, 0, 200, 300);

        // Layover is 1 hour (60 minutes), so tight connection for a minLayover of 120 minutes
        assertEquals(true,flight1.tightConnection(flight2, 120));
        // Layover is 1 hour, not tight if minLayover is 30 minutes
        assertEquals(false,flight1.tightConnection(flight2, 30));

        // No connection: different origin/destination
        Flight flight3 = new Flight("NYC", "MIA", 15, 0, 200, 300);
        assertEquals(false,flight1.tightConnection(flight3, 30));
    }

    @DisplayName("GIVEN a flight with passengers, WHEN checking if a passenger is on the flight, THEN it should return correct results")
    @Test
    void testContainsPassenger() {
        Flight flight = new Flight("NYC", "LAX", 9, 30, 300, 400);
        Passenger passenger1 = new Passenger("John", "Doe");
        Passenger passenger2 = new Passenger("Jane", "Smith");

        flight.addToManifest(passenger1);

        // Passenger is on the flight
        assertEquals(true, flight.containsPassenger(passenger1));
        // Passenger is not on the flight
        assertEquals(false,flight.containsPassenger(passenger2));
    }


    @DisplayName("GIVEN a flight, WHEN a passenger is added, THEN the passenger should be added to the manifest")
    @Test
    void testAddToManifest() {
        Flight flight = new Flight("NYC", "LAX", 9, 30, 300, 400);
        Passenger passenger = new Passenger("John", "Doe");

        // Add passenger to the flight
        assertEquals(true,flight.addToManifest(passenger));
        assertEquals(true,flight.containsPassenger(passenger));
    }

    @DisplayName("GIVEN a flight with passengers, WHEN a passenger is removed, THEN the passenger should no longer be in the manifest")
    @Test
    void testRemoveFromManifest() {
        Flight flight = new Flight("NYC", "LAX", 9, 30, 300, 400);
        Passenger passenger = new Passenger("John", "Doe");

        flight.addToManifest(passenger);
        assertTrue(flight.containsPassenger(passenger));

        // Remove passenger from the flight
        assertEquals(true,flight.removeFromManifest(passenger));
        assertEquals(false,flight.containsPassenger(passenger));
    }

    @DisplayName("GIVEN a flight with passengers, WHEN the manifest is retrieved, THEN it should return a copy of the correct passengers.")
    @Test
    void testManifest() {
        // Create a flight and add passengers
        Flight flight = new Flight("NYC", "LAX", 9, 30, 300, 400);
        Passenger passenger1 = new Passenger("John", "Doe");
        Passenger passenger2 = new Passenger("Jane", "Smith");

        flight.addToManifest(passenger1);
        flight.addToManifest(passenger2);

        // Retrieve the manifest
        PassengerSet manifest = flight.manifest();

        // Verify the manifest contains the correct passengers
        assertEquals(2, manifest.size());
        assertTrue(manifest.contains(passenger1));
        assertTrue(manifest.contains(passenger2));

        // Verify that modifying the returned manifest does not affect the original manifest
        manifest.remove(passenger1);
        assertEquals(false,manifest.contains(passenger1));
        assertEquals(true,flight.manifest().contains(passenger1));
    }

    @DisplayName("GIVEN a flight with a specific departure time, WHEN formatDepartureTime is called, THEN it should return the correct formatted time in 12-hour AM/PM format.")
    @Test
    void testFormatDepartureTime() {
        // Test for midnight (12:00 AM)
        Flight flight3 = new Flight("NYC", "LAX", 0, 0, 300, 400);  // 12:00 AM
        assertEquals("12:00 AM", flight3.formatDepartureTime());

        // Test for noon (12:00 PM)
        Flight flight4 = new Flight("NYC", "LAX", 12, 0, 300, 400);  // 12:00 PM
        assertEquals("12:00 PM", flight4.formatDepartureTime());

        // Test for afternoon
        Flight flight5 = new Flight("NYC", "LAX", 16, 45, 300, 400);  // 4:45 PM
        assertEquals("4:45 PM", flight5.formatDepartureTime());

        // Test for single-digit minute and morning
        Flight flight6 = new Flight("NYC", "LAX", 11, 9, 300, 400);  // 11:09 AM
        assertEquals("11:09 AM", flight6.formatDepartureTime());
    }


}
