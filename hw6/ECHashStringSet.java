import java.util.List;

/** A set of String values.
 *  @author
 */
class ECHashStringSet implements StringSet {
    private int bucketCount;
    private String[][] hashTable;
    private int numStrings;


    /** Creates a new empty ECHashStringSet. */
    public ECHashStringSet() {
        bucketCount = 10;
        hashTable = new String[bucketCount][1];
        numStrings = 0;
    }

    @Override
    public void put(String s) {
        numStrings += 1;
        if (numStrings / bucketCount < 5) {
            int bucket = (s.hashCode() & 0x7fffffff) % bucketCount;
            String[] curr = hashTable[bucket];
            String[] post = new String[curr.length + 1];
            System.arraycopy(curr, 0, post, 0, curr.length);
            post[curr.length] = s;
            hashTable[bucket] = post;
        } else {
            resize(bucketCount * 2);
            put(s);
        }
    }

    public void resize(int x) {
        bucketCount = x;
        String[][] result = new String[x][1];
        for (int i = 0; i < bucketCount / 2; i++) {
            String[] curr = hashTable[i];
            for (String s : curr) {
                if (s != null) {
                    int bucket = (s.hashCode() & 0x7fffffff) % bucketCount;
                    String[] pre = result[bucket];
                    String[] post = new String[pre.length + 1];
                    System.arraycopy(pre, 0, post, 0, pre.length);
                    result[bucket] = post;
                }
            }
        }
        hashTable = result;
    }

    @Override
    public boolean contains(String s) {
        int bucket = (s.hashCode() & 0x7fffffff) % bucketCount;
        String[] curr = hashTable[bucket];
        for (String value : curr) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> asList() {
        return null; // FIXME
    }
}
