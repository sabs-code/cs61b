package lists;

import image.In;
import org.junit.Test;
import static org.junit.Assert.*;

/** Test for List.java
 *
 *  @author Sabrina Xia
 */

public class ListsTest {
    /** checks correctness of Lists.naturalRuns method
     */
    @Test
    public void testNaturalRuns() {
        int[] arrInput = {1, 3, 7, 5, 4, 6, 9, 10, 10, 11};
        IntList input = IntList.list(arrInput);
        int[][] arrResult = {{1, 3, 7}, {5}, {4, 6, 9, 10}, {10, 11}};
        IntListList result = IntListList.list(arrResult);

        int[] arrInput2 = {1, 1, 1, 1, 1};
        IntList input2 = IntList.list(arrInput2);
        int[][] arrResult2= {{1}, {1}, {1}, {1}, {1}};
        IntListList result2 = IntListList.list(arrResult2);

        assertEquals(result, Lists.naturalRuns(input));
        assertEquals(result2, Lists.naturalRuns(input2));
    }
    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
