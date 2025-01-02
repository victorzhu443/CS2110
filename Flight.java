package cs2110;

/**
 * A flight managed by ConAir. The category of airplane should be sufficiently large to accommodate
 * all passengers on the manifest. The airplane category is determined according to this table:
 * <pre>
 * Manifest size (x)                     | Airplane category
 * ----------------------------------------------
 * x <= 10                               |     1
 * 10 < x <= 20                          |     2
 * 20 < x <= PassengerSet.MAX_CAPACITY   |     3
 * </pre>
 */
public class Flight {

    /**
     * The origin city for the flight. E.g., "SYR". Must be non-empty and have no leading or
     * trailing whitespace.
     */
    private final String origin;

    /**
     * The destination city for the flight. E.g., "PHL". Must be non-empty, not equal to `origin`,
     * and have no leading or trailing whitespace.
     */
    private final String destination;

    /**
     * The set of passengers on this flight.
     */
    private final PassengerSet manifest;

    /**
     * The category of airplane to use for this flight. Only three values are allowed: 1, 2, or 3.
     * The value used should be as specified in the table in the comment at the top of this class.
     */
    private int airplaneCategory;

    /**
     * The start time of this flight, expressed as the number of minutes after midnight.  Must be
     * between 0 and 1439, inclusive.
     */
    private final int departureTimeMin;

    /**
     * The duration of this flight, in minutes. Must be positive.
     */
    private final int durationMin;

    /**
     * The distance in miles to fly from `origin` to `destination`. Must be between 100 and 1000,
     * inclusive.
     */
    private final int distanceInMiles;

    /**
     * Assert that this object satisfies its class invariants.
     */
    private void assertInv() {
        assert origin != null && !origin.isEmpty() && origin.trim().equals(origin) : "Invalid origin";
        assert destination != null && !destination.isEmpty() && destination.trim().equals(destination) : "Invalid destination";
        assert !origin.equals(destination) : "Origin and destination cannot be the same";
        assert departureTimeMin >= 0 && departureTimeMin <= 1439 : "Invalid departure time";
        assert durationMin > 0 : "Duration must be positive";
        assert distanceInMiles >= 100 && distanceInMiles <= 1000 : "Invalid distance";
        assert airplaneCategory >= 1 && airplaneCategory <= 3 : "Invalid airplane category";
    }

    /**
     * Create a flight from `origin` that departs at local time `departureHour`:`departureMin` and
     * takes `durationMin` to fly `distanceInMiles` to `destination`. The flight initially starts
     * with no passengers and with the smallest `airplaneCategory` (i.e., 1). Requires the given
     * `origin` and `destination` to not be empty and to not be equal, `departureHour` to be between
     * 0 and 23, `departureMin` to be between 0 and 59, `durationMin` to be positive, and
     * `distanceInMiles` to be between 100 and 1000, inclusive. Ensures that the given `origin` and
     * `destination` have no leading or trailing whitespaces before using them to create a new
     * Flight.
     */
    public Flight(String origin, String destination, int departureHour, int departureMin,
            int durationMin, int distanceInMiles) {
        this.origin = origin.trim();
        this.destination = destination.trim();
        this.departureTimeMin = departureHour * 60 + departureMin;
        this.durationMin = durationMin;
        this.distanceInMiles = distanceInMiles;
        this.manifest = new PassengerSet();
        this.airplaneCategory = 1;  // Start with the smallest airplane category
        assertInv();
    }

    /**
     * Return the origin of this flight.
     */
    public String origin() {
        return origin;
    }

    /**
     * Return the destination of this flight.
     */
    public String destination() {
        return destination;
    }

    /**
     * Return the number of minutes since midnight when this flight departs.
     */
    public int departureTimeMin() {
        return departureTimeMin;
    }

    /**
     * Return the time of day when this flight departs.
     */
    public String departureTime() {
        return formatDepartureTime();
    }

    /**
     * Return the duration, in minutes, for this flight.
     */
    public int durationMin() {
        return durationMin;
    }

    /**
     * Return the flight distance in miles from the flight's origin to its destination.
     */
    public int distanceInMiles() {
        return distanceInMiles;
    }

    /**
     * Return the set of passengers on this flight.
     */
    public PassengerSet manifest() {
        PassengerSet passengerSet = new PassengerSet();
        for (int i = 0; i < manifest.size(); i++) {
            passengerSet.add(manifest.passengers()[i]);
        }
        return passengerSet;
    }

    /**
     * Return the departure time for this flight in the `hour:min AM/PM` format, using 12-hour time.
     * For example, if `departureTimeMin` is 675, then this method returns "11:15 AM" and if
     * `departureTimeMin is 815, then this method returns "1:35 PM". Add leading zero(s) to the
     * minutes portion if the number of minutes past the hour is less than 10.
     */
    public String formatDepartureTime() {
        int hour = departureTimeMin / 60;
        int minute = departureTimeMin % 60;
        String period = (hour >= 12) ? "PM" : "AM";
        hour = (hour == 0 || hour == 12) ? 12 : hour % 12;

        return String.format("%d:%02d %s", hour, minute, period);
    }

    /**
     * Return whether `nextFlight` departs from this flight's destination within `minLayover`
     * minutes (exclusive) after our arrival time.  For example, if this flight lands at ITH at 9:00
     * and `nextFlight` departs from ITH at 9:15, then this would qualify as a "short layover" for
     * any value of `minLayover > 15`.  Returns false if `nextFlight` does not depart from our
     * destination, or if it departs before we arrive (such flights don't count as "connections").
     * (Also returns false if the layover time is at least `minLayover`).  Requires `minLayover` is
     * non-negative.
     */
    public boolean tightConnection(Flight nextFlight, int minLayover) {
        assert minLayover >= 0 : "Layover must be non-negative";

        int arrivalTime = departureTimeMin + durationMin;
        if (!destination.equals(nextFlight.origin())) {
            return false;  // Different destinations, no connection possible
        }
        int layover = nextFlight.departureTimeMin - arrivalTime;
        return layover > 0 && layover < minLayover;
    }

    /**
     * Returns whether a given passenger is on this flight.
     */
    public boolean containsPassenger(Passenger passenger) {
        return manifest.contains(passenger);
    }

    /**
     * Match the airplane category to the number of passengers, based on the number of passengers on
     * the manifest as specified in the table in the comments at the top of this class. Recall that
     * the PassengerSet _type_ is constrained to have no more than PassengerSet.MAX_CAPACITY
     * Passengers.
     */
    private void updatePlaneCategory() {
        int passengerCount = manifest.size();
        if (passengerCount <= 10) {
            airplaneCategory = 1;
        } else if (passengerCount <= 20) {
            airplaneCategory = 2;
        } else {
            airplaneCategory = 3;
        }
        assertInv();  // Ensure the airplane category is valid

    }

    /**
     * Return `true` and increment by 1 the number of flights that a given passenger is on if that
     * passenger is not already on this flight and is successfully added to the manifest for this
     * flight. Otherwise, return `false`. If necessary, the airplane category should be updated to
     * ensure sufficient capacity for the manifest, as specified in the table in the comments at the
     * top of this class.
     */
    public boolean addToManifest(Passenger passenger) {
        if (manifest.add(passenger)) {
            passenger.increaseReservations(1);
            updatePlaneCategory();
            return true;
        }
        return false;
    }

    /**
     * Return `true` and decrement by 1 the number of flights that a given passenger is on, if that
     * passenger is already on this flight. Otherwise, return `false`.  If necessary, the airplane
     * category should be updated to ensure sufficient capacity for the manifest, as specified in
     * table in the comments at the top of this class.
     */
    public boolean removeFromManifest(Passenger passenger) {
        if (manifest.remove(passenger)) {
            passenger.reduceReservations(1);
            updatePlaneCategory();
            return true;
        }
        return false;
    }

    /**
     * Return a String representation of this Flight.
     */
    @Override
    public String toString() {
        return "Flight{" +
                "Origin City: '" + origin() + '\'' +
                ", Destination City: '" + destination() + '\'' +
                ", Departure Minute: " + departureTimeMin() +
                ", Duration (mins): " + durationMin() +
                ", Distance (miles): " + distanceInMiles() +
                '}';
    }
}
