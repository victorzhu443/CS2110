package cs2110;

/**
 * A passenger on one of ConAir's flights.
 */
public class Passenger {

    /**
     * Flag that is set to true if Passenger has only one name.
     */
    private final boolean singleNamed;

    /**
     * State that stores the passenger's first name.
     * Legal values: Any non-null string. May be empty if the passenger has no first name. No leading or trialing whitespace.
     */
    private String firstName;

    /**
     * State that stores the passenger's last name.
     * Legal values: Any non-null string. May be empty if the passenger has no first name. No leading or trialing whitespace.
     */
    private String lastName;

    /**
     * The number of flights reserved by the passenger.
     * Legal values: Non-negative integers.
     */
    private int flightCount;

    /**
     * Create a new Passenger with a first name and a last name. Requires that the first name and
     * last name are not empty. Ensures that input first name and last name have no leading or
     * trailing whitespaces before using them to create a new Passenger.
     */
    public Passenger(String firstName, String lastName) {
        this.firstName = firstName != null ? firstName.trim() : ""; // firstName can be null
        this.lastName = lastName.trim();
        this.flightCount = 0;
        if (this.firstName != null) {singleNamed = false;}
        else {singleNamed = true;}
        assertInv();
    }

    /**
     * Asserts the class invariant:
     * 1. firstName and lastName are not null.
     * 2. flightCount is non-negative.
     */
    private void assertInv() {
        assert firstName != null : "First name cannot be null.";
        assert lastName != null : "Last name cannot be null.";
        assert flightCount >= 0 : "Flight count must be non-negative.";
    }


    /**
     * Create a new Passenger with a single name. Requires that the single name is not empty.
     * Ensures that the input single name has no leading or trailing spaces before using it to
     * create a new Passenger and that the created single-named Passenger's first name is empty.
     */
    public Passenger(String singleName) {
        singleNamed = true;
        this.firstName = "";
        this.lastName = singleName.trim();
        this.flightCount = 0;
        assertInv();
    }

    /**
     * Return the first name of this Passenger if `singleNamed == false`. Otherwise, return an empty
     * String.
     */
    public String firstName() {
        return this.firstName;
    }

    /**
     * Return the last name of this Passenger or their single name if `singleNamed == true`. Will
     * not be empty.
     */
    public String lastName() {
        return this.lastName;
    }

    /**
     * Return the number of flights that this customer has currently reserved.
     */
    public int flightCount() {
        return  this.flightCount;
    }

    /**
     * Return an identifier for this Passenger, obtained from the last name and first name (if the
     * Passenger has one). If the passenger has a first name, return the last name and the first
     * name separated by a comma. Otherwise, return only the last name or single name.
     */
    public String fullID() {
        // Observe that, by invoking methods instead of referencing this fields, this method was
        // implemented without knowing how you will name your fields.
        if (singleNamed) {
            return lastName();
        } else {
            return lastName() + "," + firstName();
        }
    }

    /**
     * Increase the number of flights that this customer has reserved by `reservationIncrement`.
     * This method will be invoked, for example, if a Passenger reserves flights. Requires that
     * `reservationIncrement` is positive.
     */
    public void increaseReservations(int reservationIncrement) {
        assert reservationIncrement > 0;
        flightCount += reservationIncrement;
        assertInv();
    }

    /**
     * Reduce the number of flights that this customer has reserved by `reservationDecrement`. This
     * method can be invoked, for example, if a Passenger completes or cancels flights. Requires
     * `reservationDecrement` to be less or equal to the number of flights that the Passenger
     * currently has reserved. Requires `reservationDecrement` to be positive.
     */
    public void reduceReservations(int reservationDecrement) {
        assert reservationDecrement > 0;
        assert reservationDecrement <= flightCount;
        flightCount -= reservationDecrement;
        assertInv();
    }

    /**
     * Return the full ID of this Passenger as its String representation.
     */
    @Override
    public String toString() {
        return fullID();
    }
}
