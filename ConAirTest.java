package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConAirTest {
    @DisplayName("GIVEN passengers on multiple flights, WHEN checking for frequent fliers, THEN it should return the correct count of passengers on more than five flights.")
    @Test
    void testFrequentFliers() {
        ConAir conAir = new ConAir();
        Passenger frequentPassenger = new Passenger("Frequent", "Flyer");
        Passenger regularPassenger = new Passenger("Regular", "Passenger");

        // Add both passengers
        conAir.addPassenger(frequentPassenger);
        conAir.addPassenger(regularPassenger);

        // Create multiple flights for the frequent flier
        for (int i = 0; i < 6; i++) {
            Flight flight = new Flight("City" + i, "City" + (i + 1), 9, 30, 300, 400);
            conAir.addFlight(flight);
            flight.addToManifest(frequentPassenger);
        }

        // Regular passenger is only on one flight
        Flight regularFlight = new Flight("CityA", "CityB", 10, 30, 200, 500);
        conAir.addFlight(regularFlight);
        regularFlight.addToManifest(regularPassenger);

        // Test frequent fliers
        assertEquals(1, conAir.frequentFliers());
    }

    @DisplayName("GIVEN a passenger with connecting flights, WHEN checking for bad layovers, THEN it should return true if layovers are too short.")
    @Test
    void testHasBadLayover() {
        ConAir conAir = new ConAir();
        Passenger passenger = new Passenger("John", "Doe");

        // Add passenger
        conAir.addPassenger(passenger);

        // Create connecting flights with short layover
        Flight flight1 = new Flight("NYC", "LAX", 9, 30, 300, 400);
        Flight flight2 = new Flight("LAX", "SFO", 15, 0, 200, 500); // Tight layover
        conAir.addFlight(flight1);
        conAir.addFlight(flight2);

        flight1.addToManifest(passenger);
        flight2.addToManifest(passenger);

        // Test for bad layover
        assertEquals(true,conAir.hasBadLayover(passenger, 60));
        assertEquals(false,conAir.hasBadLayover(passenger, 15));
    }
}
