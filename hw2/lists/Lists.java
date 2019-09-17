package lists;

/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

import image.In;

/** List problem.
 *  @author Sabrina Xia
 */
class Lists {
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntListList naturalRuns(IntList L) {
        /* *Replace this body with the solution. */
        if (L==null) {return null;}
        else {
            IntList input, pointer;
            input = pointer = L;
            int last = 0;
            for (; L != null && L.head > last; L = L.tail) {
                pointer = L;
                last = L.head;
            }
            pointer.tail = null;
            return new IntListList(input, naturalRuns(L));
        }
    }
}
