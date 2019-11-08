import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class ECHashStringSetTest  {

    @Test
    public void test1() {
        BSTStringSet test = new BSTStringSet();
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        List<String> asList= test.asList();
        Iterator<String> iter = asList.iterator();
        ECHashStringSet ECSet = new ECHashStringSet();
        ECSet.put("java");
        ECSet.put("python");
        ECSet.put("trees");
        ECSet.put("binary");
        ECSet.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        for (;iter.hasNext();) {
            assertTrue(ECSet.contains(iter.next()));
        }
    }
}
