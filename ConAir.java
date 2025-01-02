package cs2110;

/**
 * An instance of ConAir's flight management system.
 */
public class ConAir {

    /**
     * Maximum number of flights that ConAir will ever need to manage.
     */
    private static final int MAX_FLIGHTS = 100;

    /**
     * Set of flights that ConAir manages. Cannot be more than MAX_FLIGHTS (since ConAir is a small
     * airline). Elements `flights[0..numberOfFlights-1]` contain the distinct flights in this set,
     * none of which is null.  All elements in `flights[numberOfFlights..]` are null.
     */
    private final Flight[] flights;

    /**
     * The number of distinct flights that ConAir manages. Must be non-negative and no greater than
     * `flights.length`.
     */
    private int numberOfFlights;

    /**
     * Set of ConAir passengers, whose size cannot be more than (MAX_FLIGHTS *
     * PassengerSet.MAX_CAPACITY). A ConAir passenger is allowed to not be on any ConAir flight.
     * Elements `passengers[0..numberOfPassengers-1]` contain the distinct passengers in this set,
     * none of which is null.  All elements in `passengers[numberOfPassengers..]` are null.
     */
    private final Passenger[] passengers;

    /**
     * The number of distinct ConAir passengers. Must be non-negative and no greater than
     * `passengers.length`.
     */
    private int numberOfPassengers;


    /**
     * Assert that this class satisfies its invariants.
     */
    private void assertInv() {
        assert flights != null && flights.length <= MAX_FLIGHTS;
        assert numberOfFlights >= 0 && numberOfFlights < flights.length;
        checkDistinctElements(flights, numberOfFlights);
        checkDistinctElements(passengers, numberOfPassengers);
    }

    /**
     * Assert that there are no duplicate objects among the [0..size-1] elements in an array.
     */
    private void checkDistinctElements(Object[] objects, int size) {
        for (int i = 0; i < size; i += 1) {
            // elements in use must not be null
            assert objects[i] != null;
            for (int j = i + 1; j < size; j += 1) {
                // all flights should be distinct
                // NOTE: `equals()` here defaults to reference equality; you are not required to
                //        write `equals()` methods in this assignment
                assert !objects[i].equals(objects[j]);
            }
        }
    }

    /**
     * Create an empty set of flights.
     */
    public ConAir() {
        flights = new Flight[MAX_FLIGHTS];
        passengers = new Passenger[MAX_FLIGHTS * PassengerSet.MAX_CAPACITY];
        assertInv();
    }

    /**
     * Return the number of flights managed by ConAir.
     */
    public int numberOfFlights() {
        return numberOfFlights;
    }

    /**
     * Return the set of flights managed by ConAir.  The length of the returned array matches the
     * number of current flights.
     */
    public Flight[] flights() {
        Flight[] currentFlights = new Flight[numberOfFlights()];
        for (int i = 0; i < numberOfFlights(); i++) {
            assert flights[i] != null;
            currentFlights[i] = flights[i];
        }
        return currentFlights;
    }

    /**
     * Return the number of ConAir passengers.
     */
    public int numberOfPassengers() {
        return numberOfPassengers;
    }

    /**
     * Return the set of ConAir passengers.  The length of the returned array matches the number of
     * managed passengers (note: ConAir may manage some passengers who have not currently booked any
     * flights).  Returns an array because the airline may manage more passengers across all of its
     * flights that would fit in a `PassengerSet` (which is sized for a single flight).
     */
    public Passenger[] passengers() {
        Passenger[] currentPassengers = new Passenger[numberOfPassengers()];
        for (int i = 0; i < numberOfPassengers(); i++) {
            assert passengers[i] != null;
            currentPassengers[i] = passengers[i];
        }
        return currentPassengers;
    }

    /**
     * Return whether ConAir manages a given flight.
     */
    public boolean managesFlight(Flight flight) {
        for (int i = 0; i < numberOfFlights; i += 1) {
            if (flights[i].equals(flight)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether ConAir has capacity to manage more flights.
     */
    public boolean canAddFlight() {
        return numberOfFlights < flights.length - 1;
    }

    /**
     * Add a given flight to the set that ConAir manages and return `true` if the given flight was
     * not already being managed by ConAir. Otherwise, return false. Requires that ConAir has
     * capacity to add more flights.
     */
    public boolean addFlight(Flight flight) {
        assert canAddFlight();
        if (!managesFlight(flight)) {
            flights[numberOfFlights] = flight;
            numberOfFlights += 1;
            assertInv();
            return true;
        }
        return false;
    }

    /**
     * Returns whether a given flight ID is for a flight that ConAir manages.
     */
    public boolean isValidFlightID(int id) {
        return id >= 0 && id < numberOfFlights;
    }

    /**
     * Return the Flight corresponding to a flight ID. Requires that the ID is for a flight that
     * ConAir manages.
     */
    public Flight getFlight(int id) {
        assert isValidFlightID(id);
        return flights[id];
    }

    /**
     * Return whether a given passenger is one of ConAir's passengers.
     */
    public boolean isConAirPassenger(Passenger passenger) {
        assert passenger != null;
        for (int i = 0; i < numberOfPassengers; i++) {
            if (getPassenger(i).equals(passenger)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether ConAir has capacity for more passengers.
     */
    public boolean canAddPassenger() {
        return numberOfPassengers < passengers.length - 1;
    }

    /**
     * Add a given passenger to the set of ConAir passengers and return `true` if that passenger was
     * not already a ConAir passenger. Otherwise, return false. Requires that ConAir has capacity to
     * add a passenger.
     */
    public boolean addPassenger(Passenger passenger) {
        assert canAddPassenger();
        if (!isConAirPassenger(passenger)) {
            passengers[numberOfPassengers] = passenger;
            numberOfPassengers += 1;
            assertInv();
            return true;
        }
        return false;
    }

    /**
     * Returns whether a given passenger ID is for one of ConAir's passengers.
     */
    public boolean isValidPassengerID(int id) {
        return id >= 0 && id < numberOfPassengers;
    }

    /**
     * Return the ConAir passenger at index `id` in the set of all ConAir passengers, which is
     * stored in `passengers`. Requires `id` to be non-negative but less than the size of
     * `passengers`.
     */
    public Passenger getPassenger(int id) {
        assert isValidPassengerID(id);
        return passengers[id];
    }

    /**
     * Return the number of occupied seats on all of ConAir's flights. For example, if ConAir
     * currently only manages two flights `f1` and `f2`, then this method should return the sum of
     * the number of passengers in `f1` and `f2`, even if some passengers are on both flights.
     */
    public int getNumberOfOccupiedSeats() {
        int passengerCount = 0;
        for (int i = 0; i < numberOfFlights; i += 1) {
            passengerCount += flights[i].manifest().size();
        }
        return passengerCount;
    }

    /**
     * Return the count of unique passengers who are on more than five flights.
     */
    public int frequentFliers() {
        int frequentFliersCount = 0;

        for (int i = 0; i < numberOfPassengers; i++) {
            Passenger passenger = passengers[i];
            if (passenger.flightCount() > 5) {
                frequentFliersCount++;
            }
        }

        return frequentFliersCount;
    }

    /**
     * Returns whether a given passenger is on at least two connecting flights whose layover time is
     * less than `layoverMins` minutes. Requires `layoverMins` is non-negative.  A pair of flights
     * is "connecting" if one departs from the other's destination and departs no earlier than the
     * other's arrival time.  Assumes connecting flights depart on the same date.
     */
    public boolean hasBadLayover(Passenger passenger, int layoverMins) {
        assert layoverMins >= 0 : "Layover time must be non-negative";

        // Loop over all flights
        for (int i = 0; i < numberOfFlights; i++) {
            Flight flight1 = flights[i];

            if (flight1.containsPassenger(passenger)) {
                for (int j = 0; j < numberOfFlights; j++) {
                    if (i != j) {
                        Flight flight2 = flights[j];

                        if (flight2.containsPassenger(passenger) &&
                                flight1.tightConnection(flight2, layoverMins)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    /**
     * Return a String representation of the flights that ConAir manages.
     */
    public String flightsString() {
        StringBuilder builder = new StringBuilder("{\n");
        for (int i = 0; i < numberOfFlights(); i++) {
            builder.append(i + ": " + getFlight(i) + "\n");
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Return a String representation of all ConAir passengers.
     */
    public String passengersString() {
        StringBuilder builder = new StringBuilder("{\n");
        for (int i = 0; i < numberOfPassengers(); i++) {
            builder.append(i + ": " + getPassenger(i) + "\n");
        }
        builder.append("}");
        return builder.toString();
    }





}
