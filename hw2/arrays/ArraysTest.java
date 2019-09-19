package arrays;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    /** Checks the correctness for methods in Array.java
     */

    @Test
    public void testCatenate() {
        int[] result = {1, 2, 3, 4, 5, 6};
        int[] input1 = {1, 2, 3, 4};
        int[] input2 = {5, 6};
        int[] input3 = {};
        int[] input4 = {1, 2, 3, 4, 5, 6};
        int[] input5 = {1, 2, 3, 4, 5, 6};
        int[] input6 = {};
        assertArrayEquals(result, Arrays.catenate(input1, input2));
        assertArrayEquals(result, Arrays.catenate(input3, input4));
        assertArrayEquals(result, Arrays.catenate(input5, input6));
    }

    @Test
    public void testRemove() {
        int[] result1 = {1, 2, 5, 6};
        int[] input1 = {1, 2, 3, 4, 5, 6};
        int[] input2 = {1, 2, 3, 4, 5};
        int[] result3 = {1, 2,5};
        int[] result4 = {1};
        assertArrayEquals(result1, Arrays.remove(input1, 2, 2));
        assertArrayEquals(result3, Arrays.remove(input2, 2, 2));
        assertArrayEquals(result4, Arrays.remove(input2, 1, 4));
    }

    @Test
    public void testNaturalRuns() {
        int[] input = {1, 3, 7, 5, 4, 6, 9, 10};
        int[] result1 = {1, 3, 7};
        int[] result2 = {5};
        int[] result3 = {4, 6, 9, 10};
        assertArrayEquals(result1, Arrays.naturalRuns(input)[0]);
        assertArrayEquals(result2, Arrays.naturalRuns(input)[1]);
        assertArrayEquals(result3, Arrays.naturalRuns(input)[2]);
        assertEquals(3, Arrays.naturalRuns(input).length);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
