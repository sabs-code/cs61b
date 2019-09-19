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

        int[] arrInput3 = {0, 1, 2, 3, 4};
        IntList input3 = IntList.list(arrInput3);
        int[][] arrResult3 = {{0, 1, 2, 3, 4}};
        IntListList result3 = IntListList.list(arrResult3);

        int[] arrInput4 = {7, 3, 2, 3, 6, 7, 8, 2, 3, 6, 8, 4, 1, 2, 4, 3, 0, 1, 2};
        IntList input4 = IntList.list(arrInput4);
        int[][] arrResult4 = {{7}, {3}, {2, 3, 6, 7, 8}, {2, 3, 6, 8}, {4}, {1, 2, 4}, {3}, {0, 1, 2}};
        IntListList result4 = IntListList.list(arrResult4);

        assertEquals(result, Lists.naturalRuns(input));
        assertEquals(result2, Lists.naturalRuns(input2));
        assertEquals(result3, Lists.naturalRuns(input3));
        assertEquals(result4, Lists.naturalRuns(input4));
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
