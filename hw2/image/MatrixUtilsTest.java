package image;

import afu.org.checkerframework.checker.oigj.qual.O;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests for MatrixUtils.java.
 *  @author Sabrina Xia
 */

public class MatrixUtilsTest {
    /** Checks correctness for Accumulate Vertical.
     */
    @Test
    public void testAccumulateVertical() {
        double tolerance = 0.01;
        double[][] input = {
                {1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000}, {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000}, {1000000, 1000000, 1000000, 1000000}
        };
        double[][] calculated = MatrixUtils.accumulateVertical(input);
        assertEquals(1000000, calculated[0][2], tolerance);
        assertEquals(2000000, calculated[1][0], tolerance);
        assertEquals(1030003, calculated[1][2], tolerance);
        assertEquals(1060005, calculated[2][1], tolerance);
        assertEquals(2133049, calculated[3][3], tolerance);
        assertEquals(2162923, calculated[5][0], tolerance);
        assertEquals(2124919, calculated[5][3], tolerance);
    }

    @Test
    public void testTranspose() {
        double tolerance = 0.01;
        double[][] input = {
                {1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000}, {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000}, {1000000, 1000000, 1000000, 1000000}
        };
        double[][] calculated = MatrixUtils.transpose(input);
        assertEquals(input.length, calculated[0].length);
        assertEquals(input[0].length, calculated.length);
        assertEquals(29515, calculated[1][3], tolerance);
        assertEquals(35399, calculated[2][4], tolerance);
    }

    @Test
    public void testAccumulate(){
        double tolerance = 0.01;
        double[][] input = {
                {1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000}, {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000}, {1000000, 1000000, 1000000, 1000000}
        };
        double[][] calculated = MatrixUtils.accumulate(input, MatrixUtils.Orientation.HORIZONTAL);
        assertEquals(1132561, calculated[2][2], tolerance);
        assertEquals(1064914, calculated[4][2],tolerance);
        assertEquals(input.length, calculated.length);
        assertEquals(input[0].length, calculated[0].length);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
