package arrays;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
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
        int[] result1 = {3, 4, 5, 6};
        int[] input1 = {1, 2, 3, 4, 5, 6};
        int[] result2 = {};
        assertArrayEquals(result1, Arrays.remove(input1, 2, 2));
        assertArrayEquals(result2, Arrays.remove(input1, 6, 6));
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
