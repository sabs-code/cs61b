/** HW #7, Two-sum problem.
 * @author Sabrina Xia
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        SortingAlgorithm s = new MySortingAlgorithms.MergeSort();
        s.sort(A, A.length);
        s.sort(B, B.length);
        int[] both = new int[A.length + B.length];
        System.arraycopy(A, 0, both, 0, A.length);
        System.arraycopy(A, 0, both, A.length, B.length);
        int indexA = 0;
        int indexB = both.length - 1;
        while (indexA < A.length && indexB < B.length) {
            if (both[indexA] + both[indexB] == m) {
                return true;
            } else if (both[indexA] + both[indexB] < m) {
                indexA++;
            } else {
                indexB++;
            }
        }
        return false;
    }

}
