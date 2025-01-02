package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

class A1Test {

    @DisplayName("polygonArea()")
    @Nested
    class PolygonAreaTest {
        /* Note: Comparing doubles in tests can be tricky; see comment below if you'd like to
         * understand the third argument in the tests' assertions. */

        @Nested
        @DisplayName("computes the area of equilateral triangles")
        class Triangles {

            @Test
            @DisplayName("with unit sides")
            void testTriangle1() {
                assertEquals(Math.sqrt(3) / 4, A1.polygonArea(3, 1.0),
                        3 * Math.ulp(Math.sqrt(3) / 4));
            }

            @Test
            @DisplayName("with longer than unit sides")
            void testTriangle2() {
                assertEquals(Math.sqrt(3), A1.polygonArea(3, 2.0),
                        3 * Math.ulp(Math.sqrt(3)));
            }
        }

        @Nested
        @DisplayName("computes the area of squares")
        class Squares {

            @Test
            @DisplayName("with unit sides")
            void testSquare1() {
                assertEquals(1, A1.polygonArea(4, 1.0),
                        3 * Math.ulp(1));
            }

            @Test
            @DisplayName("with shorter than unit sides")
            void testSquare2() {
                assertEquals(1.0 / 9.0, A1.polygonArea(4, 1.0 / 3.0),
                        3 * Math.ulp(1.0 / 9.0));
            }
        }

        @Nested
        @DisplayName("computes the area of regular hexagons")
        class Hexagons {

            @Test
            @DisplayName("with unit sides")
            void testHexagon1() {
                assertEquals(3 * Math.sqrt(3) / 2, A1.polygonArea(6, 1.0),
                        3 * Math.ulp(3 * Math.sqrt(3) / 2));
            }
        }
    }

    /*
     * Regarding comparisons of floating-point numbers:
     * Floating-point arithmetic is subtle - two expressions that look algebraically equivalent may
     * actually produce different values on a computer because intermediate results get rounded.
     * For this reason, it is usually not appropriate to use `==` to ask whether the results of two
     * floating-point calculations are the same (there are exceptions, such as when computing with
     * integers or comparing to zero).  Instead, we ask whether the result is sufficiently close to
     * the expected value, and the third argument of `assertEquals()` is how we indicate the maximum
     * amount they are allowed to differ.  Choosing this tolerance is a deeper question than it may
     * seem, but for relatively simple equations, we don't expect more than the last few bits to be
     * different.  Since floating-point precision is _relative_, we use `Math.ulp()` to get a
     * corresponding absolute tolerance.
     * Students are **not** expected to come up with this logic on their own right now.
     */

    // TODO: Uncomment this test suite after declaring `nextCollatz()`.
    // Select all of the commented code, then press Ctrl+/ (or Command+/ on Mac).
    @Nested
    @DisplayName("nextCollatz()")
    class NextCollatzTest {
        @Nested
        @DisplayName("evaluates a 4-2-1 cycle")
        class Cycle421 {
            @Test
            @DisplayName("4 goes to 2")
            void test4() {
                assertEquals(2, A1.nextCollatz(4));
            }

            @Test
            @DisplayName("2 goes to 1")
            void test2() {
                assertEquals(1, A1.nextCollatz(2));
            }

            @Test
            @DisplayName("1 goes to 4")
            void test1() {
                assertEquals(4, A1.nextCollatz(1));
            }
        }

        @Nested
        @DisplayName("allows negative arguments")
        class CycleMinus2Minus1 {
            @Test
            @DisplayName("-2 goes to -1")
            void test2() {
                assertEquals(-1, A1.nextCollatz(-2));
            }

            @Test
            @DisplayName("-1 goes to -2")
            void test1() {
                assertEquals(-2, A1.nextCollatz(-1));
            }
        }

        @Test
        @DisplayName("has a fixed point at 0")
        void test0() {
            assertEquals(0, A1.nextCollatz(0));
        }
    }

    @Nested
    @DisplayName("collatzSum()")
    class CollatzSumTest {

        @Test
        @DisplayName("computes the sum of 4-2-1")
        void test4() {
            assertEquals(7, A1.collatzSum(4));
        }

        @Test
        @DisplayName("computes the sum of a power of 2")
        void test32() {
            assertEquals(63, A1.collatzSum(32));
        }

        @Test
        @DisplayName("computes the sum of a sequence longer than its seed")
        void test9() {
            assertEquals(339, A1.collatzSum(9));
        }
    }

    @Nested
    @DisplayName("med3()")
    class Med3Test {

        @Test
        @DisplayName("computes the median of distinct, sorted values")
        void testSorted() {
            assertEquals(2, A1.med3(1, 2, 3));
        }

        @Test
        @DisplayName("computes the median of distinct, unsorted values")
        void testUnsorted() {
            // Observe here how a single test case can make multiple assertions.
            assertEquals(2, A1.med3(2, 1, 3));
            assertEquals(2, A1.med3(1, 3, 2));
            assertEquals(2, A1.med3(3, 2, 1));
        }

        @Test
        @DisplayName("computes the median in the presence of negative numbers")
        void testNegative() {
            assertEquals(0, A1.med3(-3, 0, 4));
        }

        @Test
        @DisplayName("computes the median when two values are duplicates")
        void testDup2() {
            assertEquals(1, A1.med3(1, 2, 1));
            assertEquals(2, A1.med3(2, 2, 1));
        }

        @Test
        @DisplayName("computes the median when all three values are the same")
        void testDup3() {
            assertEquals(1, A1.med3(1, 1, 1));
        }

        @Test
        @DisplayName("computes the median in the presence of extreme values")
        void testExtreme() {
            assertEquals(2, A1.med3(Integer.MIN_VALUE, 2, Integer.MAX_VALUE));
            assertEquals(Integer.MAX_VALUE,
                    A1.med3(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
    }

    @Nested
    @DisplayName("intervalsOverlap()")
    class IntervalsOverlapTest {

        @Nested
        @DisplayName("detects overlaps")
        class Overlapping {

            @Test
            @DisplayName("when intervals partially overlap")
            void testPartial() {
                assertTrue(A1.intervalsOverlap(1, 3, 2, 4));
                assertTrue(A1.intervalsOverlap(-4, -2, -3, -1));
            }

            @Test
            @DisplayName("when intervals only overlap at boundary")
            void testBoundary() {
                assertTrue(A1.intervalsOverlap(1, 2, 2, 3));
                assertTrue(A1.intervalsOverlap(-3, -2, -2, -1));
            }

            @Test
            @DisplayName("when one interval is contained within the other")
            void testContained() {
                assertTrue(A1.intervalsOverlap(0, 3, 1, 2));
                assertTrue(A1.intervalsOverlap(-2, -1, -3, 0));
                assertTrue(A1.intervalsOverlap(2, 3, 1, 3));
            }

            @Test
            @DisplayName("when intervals only contain a single point")
            void testSingle() {
                assertTrue(A1.intervalsOverlap(1, 1, 1, 1));
                assertTrue(A1.intervalsOverlap(1, 1, 0, 2));
                assertTrue(A1.intervalsOverlap(-2, 0, -1, -1));
            }
        }

        @Nested
        @DisplayName("detects no overlaps")
        class NonOverlapping {

            @Test
            @DisplayName("when first interval is to the left of the second")
            void testLeftRight() {
                assertFalse(A1.intervalsOverlap(-4, -2, 2, 4));
            }

            @Test
            @DisplayName("when first interval is to the right of the second")
            void testRightLeft() {
                assertFalse(A1.intervalsOverlap(-4, -2, -10, -5));
                assertFalse(A1.intervalsOverlap(4, 12, -10, -5));
            }

            @Test
            @DisplayName("when intervals only contain a single point")
            void testSingle() {
                assertFalse(A1.intervalsOverlap(1, 1, 2, 2));
                assertFalse(A1.intervalsOverlap(2, 2, 1, 1));
            }
        }
    }

    @Nested
    @DisplayName("estimatePi()")
    class EstimatePiTest {

        @Test
        @DisplayName("can sum zero terms")
        void test0() {
            assertEquals(0, A1.estimatePi(0));
        }

        @Test
        @DisplayName("returns the correct first term")
        void test1() {
            assertEquals(4, A1.estimatePi(1));
        }

        @Test
        @DisplayName("matches the example in the specification")
        void testExample() {
            // Two terms
            assertEquals(8.0 / 3.0, A1.estimatePi(2),
                    3 * Math.ulp(8.0 / 3.0));

            // All terms in API example
            assertEquals(1052.0 / 315.0, A1.estimatePi(5),
                    3 * Math.ulp(1052.0 / 315.0));
        }

        @Test
        @DisplayName("agrees with CAS result for a large number of terms")
        void test100() {
            // 100 terms (matches many digits with isolated errors)
            assertEquals(3.1315929035585528, A1.estimatePi(100),
                    3 * Math.ulp(3.1315929035585528));
        }
    }

    @Nested
    @DisplayName("isPalindrome()")
    class IsPalindromeTest {

        @Nested
        @DisplayName("detects palindromes")
        class Palindromes {

            @Test
            @DisplayName("in words of even length")
            void testEven() {
                assertTrue(A1.isPalindrome("abba"));
            }

            @Test
            @DisplayName("in words of odd length")
            void testOdd() {
                assertTrue(A1.isPalindrome("aba"));
            }

            @Test
            @DisplayName("when the string is empty")
            void testEmpty() {
                assertTrue(A1.isPalindrome(""));
            }

            @Test
            @DisplayName("when the string only contains a single letter")
            void testSingle() {
                assertTrue(A1.isPalindrome("a"));
            }

            @Test
            @DisplayName("in words containing digits")
            void testDigits() {
                assertTrue(A1.isPalindrome("121"));
                assertTrue(A1.isPalindrome("11"));
            }

            @Test
            @DisplayName("in words with non-ASCII characters")
            void testUnicode() {
                // No supplemental characters
                assertTrue(A1.isPalindrome("二零零二"));
            }

            @Test
            @DisplayName("in longer, mixed-case words")
            void testLong() {
                assertTrue(A1.isPalindrome("GohangasalamIImalasagnahoG"));
            }
        }

        @Nested
        @DisplayName("detects non-palindromes")
        class NonPalindromes {

            @Test
            @DisplayName("in words of even length")
            void testEven() {
                assertFalse(A1.isPalindrome("ab"));
            }

            @Test
            @DisplayName("in words of odd length")
            void testOdd() {
                assertFalse(A1.isPalindrome("abcda"));
            }

            @Test
            @DisplayName("in words that are palindromes expect for the first or last letter")
            void testOffBy1() {
                assertFalse(A1.isPalindrome("cabba"));
                assertFalse(A1.isPalindrome("abbac"));
            }

            @Test
            @DisplayName("in words containing digits")
            void testDigits() {
                assertFalse(A1.isPalindrome("1337"));
            }

            @Test
            @DisplayName("in words with non-ASCII characters")
            void testUnicode() {
                // No supplemental characters
                assertFalse(A1.isPalindrome("年月日"));
            }

            @Test
            @DisplayName("where only the case is different")
            void testCase() {
                assertFalse(A1.isPalindrome("GoHangASalamiImALasagnaHog"));
            }
        }
    }

    @Nested
    @DisplayName("formatConfirmation()")
    class FormatConfirmationTest {

        @Test
        @DisplayName("matches singular example from spec")
        void testSpecSingular() {
            assertEquals("Order '123ABC' contains 1 item.", A1.formatConfirmation("123ABC", 1));
        }

        @Test
        @DisplayName("matches plural example from spec")
        void testSpecPlural() {
            assertEquals("Order 'XYZ-999' contains 3 items.", A1.formatConfirmation("XYZ-999", 3));
        }

        @Test
        @DisplayName("treats 0 as plural")
        void test0() {
            assertEquals("Order 'AMZN1' contains 0 items.", A1.formatConfirmation("AMZN1", 0));
        }
    }
}
