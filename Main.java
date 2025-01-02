package cs2110;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The main program for the ConAir flight management system.
 */
public class Main {

    /**
     * The entry point to the ConAir application.
     */
    public static void main(String[] args) {
        Main app = new Main();
        if (args.length > 0) {
            File file = new File("tests", args[0]);
            try {
                app.processCommands(new Scanner(file), true);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + file);
            }
        } else {
            app.processCommands(new Scanner(System.in), false);
        }
    }

    ConAir conAir;

    public Main() {
        conAir = new ConAir();
    }

    /**
     * Read commands from a Scanner and execute them. Return when the "exit" command is read. If
     * `echo` is true, print the read command after the prompt.
     */
    private void processCommands(Scanner sc, boolean echo) {
        while (true) {
            System.out.print("Enter a command: ");
            String input = sc.nextLine().trim();
            if (echo) {
                System.out.println(input);
            }
            String[] command = input.split(" ");
            switch (command[0]) {
                case "help":
                    getCommandHelp();
                    break;
                case "addflight":
                    runAddFlightCommand(command);
                    break;
                case "addpassenger":
                    runAddPassengerCommand(command);
                    break;
                case "reserveflight":
                    runMakeReservationCommand(command);
                    break;
                case "cancelreservation":
                    runCancelReservationCommand(command);
                    break;
                case "checkbadlayover":
                    runLayoverCommand(command);
                    break;
                case "countflights":
                    runCountFlightsCommand();
                    break;
                case "countseats":
                    runCountFilledSeatsCommand();
                    break;
                case "countpassengers":
                    runCountPassengersCommand();
                    break;
                case "listpassengers":
                    runListUniquePassengersCommand();
                    break;
                case "listflights":
                    runListFlightsCommand();
                    break;
                case "frequentfliers":
                    runRunFrequentFliersCommand();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println(
                            input + " is not a valid command. Run \"help\" to see valid commands");
            }
        }
    }

    /**
     * Print the list of ConAir application commands and their arguments (if any).
     */
    private void getCommandHelp() {
        String helpMsg = "\nhelp\n\tOutput a summary of all available commands\n";

        String addPassengerMsg = "addpassenger <lastName> <firstName (optional)>\n\t" +
                "Add a passenger with the specified name(s) to the customer database\n";

        String addFlightMsg = "addflight <origin> <destination> <departureHour> <departureHour>"
                + " <duration (min)> <distance>\n\t"
                + "Add a flight with the specified properties\n";

        String reserveFlightMsg = "reserveflight <flightID> <passengerID>\n\t"
                + "Add a passenger with ID passengerID to a flight with ID <flightID>\n";

        String cancelFlightMsg = "cancelreservation <flightID> <passengerID>\n\t"
                + "Remove a passenger with ID <passengerID> from a flight with ID <flightID>\n";

        String countFlightsMsg = "countflights\n\t"
                + "Return the total number of ConAir flights\n";

        String seatsFilledMsg = "countseats\n\t"
                + "Return the total number of occupied seats on all ConAir flights\n";

        String countPassengerMsg = "countpassengers\n\t"
                + "Return the number of unique passengers on all ConAir flights\n";

        String listFlightsMsg = "listflights\n\t"
                + "Return the list of ConAir flights\n";

        String listPassengerMsg = "listpassengers\n\t"
                + "Return the list of unique passengers on all ConAir flights\n";

        String checkLayoverMsg = "checkbadlayover\n\t"
                + "Check if a given passenger is on ConAir flights insufficient layover times\n";

        String frequentFlierMsg = "frequentfliers\n\t"
                + "Return the number of frequent fliers on ConAir flights\n";

        String exitMsg = "exit\n\tExit the program\n";

        StringBuilder message = new StringBuilder(helpMsg);
        message.append(addFlightMsg);
        message.append(addPassengerMsg);
        message.append(reserveFlightMsg);
        message.append(cancelFlightMsg);
        message.append(countFlightsMsg);
        message.append(seatsFilledMsg);
        message.append(countPassengerMsg);
        message.append(listFlightsMsg);
        message.append(listPassengerMsg);
        message.append(checkLayoverMsg);
        message.append(frequentFlierMsg);
        message.append(exitMsg);

        System.out.println(message);
    }

    /**
     * Let the user know if the command that they typed is invalid.
     */
    private void invalidCommand(String cmd) {
        System.out.println("Invalid " + cmd + " command. " +
                "Enter the command \"help\" for information about that command.");
    }

    /**
     * Add a flight to the set of flights that ConAir manages.
     */
    private void runAddFlightCommand(String[] command) {
        if (command.length != 7) {
            invalidCommand("addflight");
            return;
        }
        try {
            Flight flight = getFlight(command);
            if (conAir.canAddFlight()) {
                if (conAir.addFlight(flight)) {
                    System.out.println(
                            "ConAir now manages a flight from " + flight.origin() + " to "
                                    + flight.destination() + " departing at "
                                    + flight.formatDepartureTime() + " with a flight time of "
                                    + flight.durationMin() + " minutes and a distance of "
                                    + flight.distanceInMiles() + " miles.");
                } else {
                    System.out.println("ERROR: could not add a new flight.");
                }
            } else {
                System.out.println("ERROR: ConAir has no capacity to add a new flight.");
            }
        } catch (NumberFormatException numberFormatException) {
            invalidCommand("addflight");
            numberFormatException.printStackTrace();
        }
    }

    /**
     * Create a Flight object from command-line arguments.
     */
    private static Flight getFlight(String[] command) {
        String origin = command[1];
        String destination = command[2];
        int departureHour = Integer.parseInt(command[3]);
        int departureMin = Integer.parseInt(command[4]);
        int duration = Integer.parseInt(command[5]);
        int distance = Integer.parseInt(command[6]);
        Flight flight = new Flight(origin, destination, departureHour, departureMin, duration,
                distance);
        return flight;
    }

    /**
     * Run command to add a passenger to ConAir's database.
     */
    private void runAddPassengerCommand(String[] command) {
        if (command.length != 2 && command.length != 3) {
            invalidCommand("addpassenger");
            return;
        }
        boolean singleNamed = command.length == 2;
        String lastName = command[1];
        Passenger passenger;
        if (singleNamed) {
            passenger = new Passenger(lastName);
        } else {
            String firstName = command[2];
            passenger = new Passenger(firstName, lastName);
        }
        if (conAir.canAddPassenger()) {
            if (conAir.addPassenger(passenger)) {
                System.out.println(
                        "Successfully added " + passenger + " to ConAir's database.");
            } else {
                System.out.println(
                        "ERROR: Could not add " + passenger + " to ConAir's database");
            }
        } else {
            System.out.println("ERROR: insufficient capacity to add a new passenger");
        }
    }

    /**
     * Run command to add a passenger to a flight.
     */
    private void runMakeReservationCommand(String[] command) {
        if (command.length != 3) {
            invalidCommand("reserveflight");
            return;
        }
        try {
            int flightID = Integer.parseInt(command[1]);
            int passengerID = Integer.parseInt(command[2]);
            if (conAir.isValidFlightID(flightID) && conAir.isValidPassengerID(passengerID)) {
                Flight flight = conAir.getFlight(flightID);
                Passenger passenger = conAir.getPassenger(passengerID);
                if (flight.addToManifest(passenger)) {
                    System.out.println("Passenger: " + passenger + " was successfully added "
                            + "to Flight " + flightID + ": " + flight);
                } else {
                    System.out.println("Passenger: " + passenger + " could not be added "
                            + "to Flight " + flightID + ": " + flight);
                }
            } else {
                System.out.println("ERROR: Flight ID: " + flightID + " is not a valid flight ID or "
                        + "Passenger ID: " + passengerID + " is not a valid passenger ID.");
            }
        } catch (NumberFormatException numberFormatException) {
            invalidCommand("reserveflight");
            numberFormatException.printStackTrace();
        }
    }

    /**
     * Run command to remove a passenger from a flight.
     */
    private void runCancelReservationCommand(String[] command) {
        if (command.length != 3) {
            invalidCommand("cancelreservation");
            return;
        }
        try {
            int flightID = Integer.parseInt(command[1]);
            int passengerID = Integer.parseInt(command[2]);
            System.out.println("Flights: " + conAir.isValidFlightID(flightID));
            System.out.println("Passengers: " + conAir.isValidPassengerID(flightID));
            if (conAir.isValidFlightID(flightID) && conAir.isValidPassengerID(passengerID)) {
                Flight flight = conAir.getFlight(flightID);
                Passenger passenger = conAir.getPassenger(passengerID);
                if (flight.removeFromManifest(passenger)) {
                    System.out.println("Passenger: " + passenger + " was successfully removed "
                            + "from Flight: " + flight);
                } else {
                    System.out.println("Passenger: " + passenger + " could not be removed "
                            + "from Flight: " + flight);
                }
            } else {
                System.out.println("ERROR: Flight ID: " + flightID + " is not a valid flight ID or "
                        + "Passenger ID: " + passengerID + " is not a valid passenger ID.");
            }
        } catch (NumberFormatException numberFormatException) {
            invalidCommand("cancelreservation");
            numberFormatException.printStackTrace();
        }
    }

    /**
     * Run command to check if a given passenger is on ConAir flights with conflicting schedules.
     */
    private void runLayoverCommand(String[] command) {
        if (command.length != 3) {
            invalidCommand("checkbadlayover");
            return;
        }
        try {
            int passengerID = Integer.parseInt(command[1]);
            int layoverMins = Integer.parseInt(command[2]);
            Passenger passenger = conAir.getPassenger(passengerID);
            if (conAir.hasBadLayover(passenger, layoverMins)) {
                System.out.println("Passenger: " + passenger
                        + " has insufficient layover between flights.");
            } else {
                System.out.println("Passenger: " + passenger
                        + " has sufficient layover between flights.");
            }
        } catch (NumberFormatException numberFormatException) {
            invalidCommand("checkbadlayover");
            numberFormatException.printStackTrace();
        }
    }

    /**
     * Run command to count seats on ConAir flights that are filled by a passenger.
     */
    private void runCountFilledSeatsCommand() {
        System.out.println("The total number of filled seats in all ConAir scheduled flights is: " +
                conAir.getNumberOfOccupiedSeats());
    }

    /**
     * Run command to count unique ConAir passengers.
     */
    private void runCountPassengersCommand() {
        System.out.println("The number of unique ConAir passengers is: " +
                conAir.numberOfPassengers());
    }

    /**
     * Run command to list unique ConAir passengers.
     */
    private void runListUniquePassengersCommand() {
        System.out.println("The list of unique ConAir passengers:");
        System.out.println(conAir.passengersString());
    }

    /**
     * Run command to count the number of frequent fliers on ConAir flights.
     */
    private void runRunFrequentFliersCommand() {
        System.out.println(
                "There are " + conAir.frequentFliers() + " frequent fliers on ConAir flights.");
    }

    /**
     * Run command to list all the flights that ConAir manages.
     */
    private void runListFlightsCommand() {
        System.out.println("ConAir manages these flights:");
        System.out.println(conAir.flightsString());
    }

    /**
     * Run command to count the number of flights that ConAir manages.
     */
    private void runCountFlightsCommand() {
        System.out.println(
                "ConAir manages " + conAir.numberOfFlights() + " flights.");
    }
}
