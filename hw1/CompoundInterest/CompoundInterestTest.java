import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.

        assertEquals(0, 0); */

        assertEquals(6, CompoundInterest.numYears(2025));
        assertEquals(31, CompoundInterest.numYears(2050));
    }

    @Test
    public void testFutureValue() {
        double tolerance = 0.01;
        assertEquals(12.54, CompoundInterest.futureValue(10, 12, 2021), tolerance);
        assertEquals(10, CompoundInterest.futureValue(10, 0, 2088), tolerance);
        assertEquals(34.98, CompoundInterest.futureValue(20, 15, 2023), tolerance);
        assertEquals(8.1, CompoundInterest.futureValue(10, -10, 2021), tolerance);

    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(11.80, CompoundInterest.futureValueReal(10, 12, 2021, 3), tolerance);
        assertEquals(7.621, CompoundInterest.futureValueReal(10, -10, 2021, 3),tolerance);
        assertEquals(13.307, CompoundInterest.futureValueReal(10, 12, 2021, -3), tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(16550, CompoundInterest.totalSavings(5000, 2021, 10), tolerance);
        assertEquals(2710, CompoundInterest.totalSavings(1000, 2021, -10), tolerance);
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(15571.895, CompoundInterest.totalSavingsReal(5000, 2021, 10, 3), tolerance);
        assertEquals(2549.839, CompoundInterest.totalSavingsReal(1000, 2021, -10, 3), tolerance);
        assertEquals(17557.895, CompoundInterest.totalSavingsReal(5000, 2021, 10, -3), tolerance);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
