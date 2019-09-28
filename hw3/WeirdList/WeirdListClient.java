/** Functions to increment and sum the elements of a WeirdList. */
class WeirdListClient {

    private static class Add implements IntUnaryFunction {
        private int _toAdd;
        private int _sum;
        public Add(int x) {
            _toAdd = x;
            _sum = 0;
        }
        public int apply(int x) {
            _sum += x;
            x += _toAdd;
            return x;
        }
    }



    /** Return the result of adding N to each element of L. */
    static WeirdList add(WeirdList L, int n) {
        Add a = new Add(n);
        return L.map(a);
    }


    /** Return the sum of all the elements in L. */
    static int sum(WeirdList L) {
        Add a = new Add(0);
        L.map(a);
        return a._sum;
    }

    /* IMPORTANT: YOU ARE NOT ALLOWED TO USE RECURSION IN ADD AND SUM
     *
     * As with WeirdList, you'll need to add an additional class or
     * perhaps more for WeirdListClient to work. Again, you may put
     * those classes either inside WeirdListClient as private static
     * classes, or in their own separate files.

     * You are still forbidden to use any of the following:
     *       if, switch, while, for, do, try, or the ?: operator.
     *
     * HINT: Try checking out the IntUnaryFunction interface.
     *       Can we use it somehow?
     */
}
