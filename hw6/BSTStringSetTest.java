import jdk.jfr.StackTrace;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author Sabrina Xia
 */
public class BSTStringSetTest  {
    @Test
    public void BSTtest() {
        BSTStringSet test = new BSTStringSet();
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        assertTrue(test.contains("java"));
        assertTrue(test.contains("binary"));
        assertTrue(test.contains("python"));
        assertFalse(test.contains("tree"));
    }

    @Test
    public void testAsList() {
        BSTStringSet test = new BSTStringSet();
        test.put("java");
        test.put("python");
        test.put("trees");
        test.put("binary");
        test.put("search");
        ArrayList<String> list = new ArrayList<String>();
        list.add("binary");
        list.add("java");
        list.add("python");
        list.add("search");
        list.add("trees");
        assertEquals(list, test.asList());
    }
}
