package cs2110;

import java.lang.Math;
import java.util.Scanner;
import java.util.Random;

/**
 * Collection of misc. static functions for showcasing the capabilities of Java in a procedural
 * context.
 */
public class A1 {

    public static void main(String[] args) {
        Random rand = new Random();

        int rand1 = 1;
        int rand2 = 1;
        int rand3 = 1;
        int[] n3;
        n3 = new int[3];
        n3[0] = rand1;
        n3[1] = rand2;
        n3[2] = rand3;

        System.out.println("Time for some mathmatical nonsense!");
        System.out.println(
                "Starting out with three integers " + rand1 + " " + rand2
                        + " " + rand3 + " ");
        System.out.println("We check if any are palindromes and add one to the palindromes. ");
        for (int i = 0; i < 3; i++) {
            if (A1.isPalindrome(Integer.toString(n3[i]))) {
                n3[i] += 1;
            }
        }
        System.out.println("New three random integers: " + n3[0] + " " + n3[1] + " " + n3[2] + " ");
        System.out.println("Now we do the next collatz of each: " + A1.nextCollatz(n3[0]) + " "
                + A1.nextCollatz(n3[1]) + " " + A1.nextCollatz(n3[2]));
        int median = A1.med3(A1.nextCollatz(n3[0]), A1.nextCollatz(n3[1]), A1.nextCollatz(n3[2]));
        System.out.println("And we take the median of the three: " + median);
        int sum = A1.collatzSum(median);
        System.out.println("And we find the collatz sum of the median: " + sum);
        System.out.println("And OOPSIE! Finally find the area of the polygon with " + sum
                + " sides and side length 1: " + A1.polygonArea(sum, 1));

    }

    /**
     * Return the area of a regular polygon with `nSides` sides of length `sideLength`. Units of
     * result are the square of the units of `sideLength`. Requires `nSides` is at least 3,
     * `sideLength` is non-negative.
     */
    public static double polygonArea(int nSides, double sideLength) {
        double area;
        area = 0.25 * sideLength * sideLength * nSides / (java.lang.Math.tan(
                (java.lang.Math.PI) / nSides));
        return area;

    }

    /**
     * Return the next term in the Collatz sequence after the argument.  If the argument is even,
     * the next term is it divided by 2.  If the argument is odd, the next term is 3 times it plus
     * 1.  Requires magnitude of odd `x` is less than `Integer.MAX_VALUE/3` (otherwise overflow is
     * possible).
     */
    // int argument and returns an int.

    /**
     * Return the sum of the Collatz sequence starting at `seed` and ending at 1 (inclusive).
     * Requires `seed` is positive, sum does not overflow.
     */
    public static int collatzSum(int seed) {
        // Implementation constraint: Use a while-loop.  Call `nextCollatz()` to
        // advance the sequence.
        int sum = seed;
        while (seed != 1) {
            sum += nextCollatz(seed);
            seed = nextCollatz(seed);
        }
        return sum;
    }

    public static int nextCollatz(int current) {

        if ((current % 2) == 0) {
            current = current / 2;
        }
        else {
            current = current * 3 + 1;
        }
        return current;
    }


    /**
     * Return the median value among `{a, b, c}`.  The median has the property that at least half of
     * the elements are less than or equal to it and at least half of the elements are greater than
     * or equal to it.
     */
    public static int med3(int a, int b, int c) {
        // Implementation constraint: Do not call any other methods.
        //consider all cases of equal vs not equal

        //case 1
        if ((a <= b) && (b <= c)) {
            return b;
        }
        //case 2
        if ((a <= c) && (b >= c)) {
            return c;
        }
        //case 3
        if ((b <= a) && (a <= c)) {
            return a;
        }
        //case 4
        if ((b <= c) && (c <= a)) {
            return c;
        }
        //case 5
        if ((c <= a) && (a <= b)) {
            return a;
        }
        //case 6
        return b;
    }


    /**
     * Return whether the closed intervals `[lo1, hi1]` and `[lo2, hi2]` overlap.  Two intervals
     * overlap if there exists a number contained in both of them.  Notation: the interval `[lo,
     * hi]` contains all numbers greater than or equal to `lo` and less than or equal to `hi`.
     * Requires `lo1` is less than or equal to `hi1` and `lo2` is less than or equal to `hi2`.
     */
    public static boolean intervalsOverlap(int lo1, int hi1, int lo2, int hi2) {
        // Implementation constraint: Use a single return statement to return
        // the value of a Boolean expression; do not use an if-statement.
        //GOOGLE CALENDAR inspiration

        boolean overlap = false;

        if ((Math.max(hi1, hi2) - Math.min(lo1, lo2)) <= ((hi1 - lo1) + (hi2 - lo2))) {
            overlap = true;
        }

        if (hi1 == lo2 || hi1 == hi2 || lo1 == lo2 || lo1 == hi2) {
            overlap = true;
        }
        return overlap;
    }

    /**
     * Return the approximation of pi computed from the sum of the first `nTerms` terms of the
     * Madhava-Leibniz series.  This formula states that pi/4 = 1 - 1/3 + 1/5 - 1/7 + 1/9 - ...
     * Requires `nTerms` is non-negative.
     */
    public static double estimatePi(int nTerms) {
        // Implementation constraint: Use a for-loop.  Do not call any other
        // methods (including `Math.pow()`)

        double value = 0.0;

        for (int i = 0; i < nTerms; i++) {
            if ((i % 2) == 0) {
                value = value + 1 / (1.0 + 2.0 * i);
            } else {
                value = value - 1 / (1.0 + 2.0 * i);
            }
        }
        return value * 4; //since the formula is four pi/4


    }

    /**
     * Returns whether the sequence of characters in `s` is equal (case-sensitive) to that sequence
     * in reverse order.
     */
    public static boolean isPalindrome(String s) {
        // Implementation constraint: Use the `charAt()` and `length()` methods
        // of the `String` class.
        String reverse = "";
        int length = s.length();
        boolean value = true;

        //getting reverse String
        for (int i = 0; i < length; i++) {
            reverse += s.charAt(length - 1 - i);
        }

        boolean palindrome = true;

        //checking if String palindrome
        for (int i = 0; i < length; i++) {
            if (reverse.charAt(i) != s.charAt(i)) {
                palindrome = false;
            }
        }

        return palindrome;
    }

    /**
     * Return an order confirmation message in English containing the order ID and the number of
     * items it contains.  Message shall handle item plurality properly (e.g. "1 item" vs. "3
     * items") and shall surround the order ID in single quotes. Examples:
     * <pre>
     * formatConfirmation("123ABC", 1) should return
     *   "Order '123ABC' contains 1 item."
     * formatConfirmation("XYZ-999", 3)" should return
     *   "Order 'XYZ-999' contains 3 items."
     * </pre>
     * Requires `orderId` only contains digits, hyphens, or letters 'A' - 'Z'; `itemCount` is
     * non-negative.
     */
    public static String formatConfirmation(String orderId, int itemCount) {
        // Implementation constraint: Use Java's ternary operator (`?:`) to give "item" the
        // appropriate plurality.

        String output = "";
        String end = ((itemCount > 1) || (itemCount == 0)) ? "items" : "item";

        output += "Order " + "\'" + orderId + "\' " + "contains " + itemCount + " " + end + ".";
        return output;
    }

    // prints a result computed from the values they return.  It should not depend on any
    // program arguments.
}
