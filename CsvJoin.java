package cs2110;

import java.io.*;
import java.util.*;

public class CsvJoin {

    /**
     * Load a table from a Simplified CSV file and return a row-major list-of-lists representation.
     * The CSV file is assumed to be in the platform's default encoding. Throws an IOException if
     * there is a problem reading the file.
     */
    public static Seq<Seq<String>> csvToList(String file) throws IOException {
        Seq<Seq<String>> table = new LinkedSeq<>();
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Split by comma (handling empty columns)
                String[] values = line.split(",", -1);
                Seq<String> row = new LinkedSeq<>();
                for (String value : values) {
                    row.append(value); // Append each value to the row
                }
                table.append(row); // Append the row to the table
            }
        }
        return table;
    }

    /**
     * Return the left outer join of tables `left` and `right`, joined on their first column. Result
     * will represent a rectangular table, with empty strings filling in any columns from `right`
     * when there is no match. Requires that `left` and `right` represent rectangular tables with at
     * least 1 column.
     */
    public static Seq<Seq<String>> join(Seq<Seq<String>> left, Seq<Seq<String>> right) {
        assert isValidTable(left) : "Left table must be rectangular and have at least 1 column";
        assert isValidTable(right) : "Right table must be rectangular and have at least 1 column";

        Seq<Seq<String>> result = new LinkedSeq<>();

        for (Seq<String> leftRow : left) {
            String leftKey = leftRow.get(0); // Key in the first column of the left table
            boolean matchFound = false;

            for (Seq<String> rightRow : right) {
                String rightKey = rightRow.get(0); // Key in the first column of the right table

                if (leftKey.equals(rightKey)) {
                    Seq<String> combinedRow = new LinkedSeq<>();
                    // Add all columns from the left row
                    for (String value : leftRow) {
                        combinedRow.append(value);
                    }
                    // Add all columns from the right row except the first one (since it's redundant)
                    for (int i = 1; i < rightRow.size(); i++) {
                        combinedRow.append(rightRow.get(i));
                    }
                    result.append(combinedRow);
                    matchFound = true;
                }
            }

            // If no match found, pad with empty strings
            if (!matchFound) {
                Seq<String> combinedRow = new LinkedSeq<>();
                // Add all columns from the left row
                for (String value : leftRow) {
                    combinedRow.append(value);
                }
                // Add empty strings for columns in the right table
                if (right.size() > 0) {
                    int emptyColumns = right.get(0).size() - 1; // Ignore the first column
                    for (int i = 0; i < emptyColumns; i++) {
                        combinedRow.append("");
                    }
                }
                result.append(combinedRow);
            }
        }
        return result;
    }

    // Helper function to check if a table is rectangular
    private static boolean isValidTable(Seq<Seq<String>> table) {
        if (table.size() == 0) return true; // Empty table is considered valid
        int numColumns = table.get(0).size();
        for (Seq<String> row : table) {
            if (row.size() != numColumns) {
                return false;
            }
        }
        return true;
    }

    /**
     * Main method that merges two CSV files using a left outer join, and outputs the resulting CSV.
     */
    public static void main(String[] args) {
        // Check if exactly two arguments are provided
        if (args.length != 2) {
            System.err.println("Usage: cs2110.CsvJoin <left_table.csv> <right_table.csv>");
            System.exit(1); // Exit with status code 1
        }

        // Get the left and right file paths from the command-line arguments
        String leftFile = args[0];
        String rightFile = args[1];


        //Hardcode
        //String leftFile = "input-tests/example/input1.csv";
        //String rightFile = "input-tests/example/input2.csv";

        try {
            // Load the two CSV files into lists of lists
            Seq<Seq<String>> leftTable = csvToList(leftFile);
            Seq<Seq<String>> rightTable = csvToList(rightFile);

            // Check if tables are valid (rectangular)
            if (!isValidTable(leftTable) || !isValidTable(rightTable)) {
                System.err.println("Error: Input tables are not rectangular.");
                System.exit(1);
            }

            // Perform the join
            Seq<Seq<String>> resultTable = join(leftTable, rightTable);

            // Print the result table in CSV format
            for (Seq<String> row : resultTable) {
                for (int i = 0; i < row.size(); i++) {
                    System.out.print(row.get(i));
                    if (i < row.size() - 1) {
                        System.out.print(","); // Comma between columns
                    }
                }
                System.out.println(); // New line for each row
            }
        } catch (IOException e) {
            System.err.println("Error: Could not read input tables.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
