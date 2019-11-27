import java.util.ArrayList;
import java.util.Arrays;

/** A partition of a set of contiguous integers that allows (a) finding whether
 *  two integers are in the same partition set and (b) replacing two partitions
 *  with their union.  At any given time, for a structure partitioning
 *  the integers 1-N, each partition is represented by a unique member of that
 *  partition, called its representative.
 *  @author Sabrina Xia
 */
public class UnionFind {

    private int[][]_partition;

    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        _partition = new int[N + 1][];
        for (int i = 1; i <= N; i++) {
            _partition[i] = new int[] {i};
        }
    }

    /** Return the representative of the partition currently containing V.
     *  Assumes V is contained in one of the partitions.  */
    public int find(int v) {
        for (int i = 1; i < _partition.length; i++) {
            int[] arr = _partition[i];
            for (int k : arr) {
                if (k == v) {
                    return i;
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    /** Return true iff U and V are in the same partition. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single partition, returning its representative. */
    public int union(int u, int v) {
        int urep = find(u);
        int vrep = find(v);
        int[] uarr = _partition[urep];
        int[] varr = _partition[vrep];
        int[] union = new int[uarr.length + varr.length];
        int i = 0;
        for (; i < uarr.length; i++) {
            union[i] = uarr[i];
        }
        for (int k = 0; k < varr.length; k++, i++) {
            union[i] = varr[k];
        }
        _partition[urep] = union;
        _partition[vrep] = new int[] {};
        return urep;
    }
}
