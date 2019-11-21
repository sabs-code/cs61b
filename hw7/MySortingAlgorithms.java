import java.util.*;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 1; i < k; i++) {
                int curr = array[i];
                int j = i - 1;
                for (; j >= 0 && array[j] > curr; j--) {
                    array[j + 1] = array[j];
                }
                array[j + 1] = curr;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int j = 0; j < k; j++) {
                int min = j;
                for (int i = j; i < k; i++) {
                    if (array[i] < array[min]) {
                        min = i;
                    }
                }
                int temp = array[j];
                array[j] = array[min];
                array[min] = temp;
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int[] arr = new int[k];
            System.arraycopy(array, 0, arr, 0, k);
            if (k == 1) {
            } else {
                int middle = k / 2;
                int[] first = new int[middle];
                System.arraycopy(arr, 0, first, 0, middle);
                int[] second = new int[arr.length - middle];
                System.arraycopy(arr, middle, second, 0, second.length);
                sort(first, middle);
                sort(second, second.length);
                merge(arr, first, second);
            }
            System.arraycopy(arr, 0, array, 0, k);
        }

        public void merge(int[] array, int[] arr1, int[] arr2) {
            int j, k, i;
            i = j = k = 0;
            while (j < arr1.length && k < arr2.length) {
                if (arr1[j] < arr2[k]) {
                    array[i] = arr1[j];
                    j++;
                } else {
                    array[i] = arr2[k];
                    k++;
                }
                i++;
            }
            while (j < arr1.length) {
                array[i] = arr1[j];
                i++;
                j++;
            }
            while (k < arr2.length) {
                array[i] = arr2[k];
                i++;
                k++;
            }
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            int[] arr = new int[k];
            System.arraycopy(a, 0, arr, 0, k);
            Queue<Integer>[] bins =new Queue[10];
            for (int i = 0; i < 10; i++) {
                bins[i] = new ArrayDeque<>();
            }
            boolean stop = false;
            int div = 1;
            int temp;
            while (!stop) {
                stop = true;
                for (int i : arr) {
                    temp = i / div;
                    bins[temp % 10].add(i);
                    if (stop && temp > 0) {
                        stop = false;
                    }
                }
                int index= 0;
                for (int i = 0; i < 10; i ++) {
                    while (!bins[i].isEmpty()) {
                        arr[index] = bins[i].remove();
                        index += 1;
                    }
                }
                div *= 10;
            }
            System.arraycopy(arr, 0, a, 0, k);
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
