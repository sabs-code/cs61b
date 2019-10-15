package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Sabrina Xia
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of TOALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */



    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkTransformAB() {
        perm = new Permutation("(AB)", UPPER);
        String to = "BA" + UPPER_STRING.substring(2, 26);
        checkPerm("A & B permutation", UPPER_STRING, to);
    }

    @Test
    public void checkTransformAZ() {
        perm = new Permutation("(AZ)", UPPER);
        String to = "Z" + UPPER_STRING.substring(1, 25) + "A";
        checkPerm("A & Z permutation", UPPER_STRING, to);
    }

    @Test
    public void checkTransformLong() {
        perm = new Permutation("(AUFHSL) (BEIRQ)", UPPER);
        String to = "UECDIHGSRJKAMNOPBQLTFVWXYZ";
        checkPerm("two cycles", UPPER_STRING, to);
        assertFalse(perm.derangement());
    }

    @Test
    public void checkShortAlphabet() {
        Alphabet al = new Alphabet("NHPQSUCM");
        String from = al.strAlphabet();
        perm = new Permutation("(HQUCMNSP)", al);
        assertEquals(8, perm.size());
        assertEquals(1, perm.permute(2));
        assertEquals(1, perm.invert(3));
        assertTrue(perm.derangement());
    }

    @Test (expected = EnigmaException.class)
    public void checkCycles() {
        perm = new Permutation("(sdhui)", UPPER);
    }



}
