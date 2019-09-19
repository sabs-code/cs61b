package arrays;
import static java.lang.System.arraycopy;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author Sabrina Xia
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        /* *Replace this body with the solution. */
        int[] result = new int[A.length + B.length];
        arraycopy(A, 0, result, 0, A.length);
        arraycopy(B, 0, result, A.length, B.length);
        return result;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        int[] result = new int[A.length-len];
        arraycopy(A, 0, result, 0, start);
        if (start+len == A.length) {return result;}
        else {
            arraycopy(A, start+len, result, start, A.length-start-len);
            return result;
        }
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */

    static int[][] naturalRuns(int[] A) {
        if (A.length == 0) {return null;}
        int count = 0;
        int last = A[0];
        for (int i = 0; i < A.length; i ++) {
            if (A[i] <= last) {
                count += 1;
            }
            last = A[i];
        }
        int[][] result = new int[count][];
        last = A[0];
        for (int i = 0; i < count; i++) {
            int j = 0;
            for (; j < A.length;) {
                j += 1;
                if (j == A.length) {break;}
                else if (A[j] <= last) {
                    last = A[j];
                    break;
                }
                else {
                    last = A[j];
                }
            }
            result[i] = Utils.subarray(A, 0, j);
            A = Utils.subarray(A, j, A.length-j);
        }
        return result;
    }
}
