package enigma;

import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the enigma package.
 *  @author Sabrina Xia
 */
public class UnitTest {
    Permutation pI = new Permutation(
            "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV)   "
                    + " (JZ) (S)      ", UPPER);
    Rotor I = new MovingRotor("I", pI, "Q");
    Permutation pII = new Permutation(
            "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ)   (GR) (NT) (A) (Q)", UPPER);
    Rotor ii0 = new MovingRotor("II", pII, "E");
    Permutation pIII = new Permutation(
            "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER);
    Rotor iii0 = new MovingRotor("III", pIII, "V");
    Permutation pB = new Permutation(
            "(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) "
            + "(MP) (RX) (SZ) (TV)", UPPER);
    Rotor B = new Reflector("B", pB);
    Permutation pBeta = new Permutation(
            "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER);
    Rotor beta = new FixedRotor("Beta", pBeta);

    @Test
    public void checkMachine() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(ii0);
        rotors.add(iii0);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 3, rotors);
        String[] inserted = new String[] {"B", "III", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("AAA");
        assertEquals(12, m.convert(0));
        m.setRotors("VAA");
        assertEquals(2, m.convert(0));
        m.setRotors("AAQ");
        assertEquals(2, m.convert(0));
        m.setRotors("AEQ");
        assertEquals(12, m.convert(0));
        m.setRotors("UDO");
        assertEquals(2, m.convert(6));
        assertEquals(3, m.convert(4));
        assertEquals(5, m.convert(19));
        assertEquals(20, m.convert(0));
        m.setRotors("UDO");
        assertEquals("CDFU", m.convert("GETA"));
    }

    @Test
    public void checkFixedRotors() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(ii0);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 2, rotors);
        String[] inserted = new String[] {"B", "Beta", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("UDO");
        assertEquals(24, m.convert(6));
        assertEquals(25, m.convert(4));
        assertEquals(17, m.convert(19));
        assertEquals(14, m.convert(0));
        m.setRotors("UDO");
        assertEquals("YZRO", m.convert("GETA"));
    }

    Alphabet lower = new Alphabet("abcdefghijklmnopqrstuvwxyz");
    Permutation pI0 = new Permutation("(aeltphqxru) (bknw) "
            + "(cmoy) (dfg) (iv) (jz) (s) ", lower);
    Rotor i = new MovingRotor("I", pI0, "q");
    Permutation pII0 = new Permutation(
            "(fixvyomw) (cdklhup) (esz) (bj) (gr) (nt) (a) (q)", lower);
    Rotor ii = new MovingRotor("II", pII0, "e");
    Permutation pB0 = new Permutation(
            "(ae) (bn) (ck) (dq) (fu) (gy) (hw) (ij) (lo) "
                    + "(mp) (rx) (sz) (tv)", lower);
    Rotor b = new Reflector("B", pB0);
    Permutation pBeta0 = new Permutation(
            "(albevfcyodjwugnmqtzskpr) (hix)", lower);
    Rotor beta0 = new FixedRotor("Beta", pBeta0);

    @Test
    public void checkLowerCase() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(i);
        rotors.add(ii);
        rotors.add(beta0);
        rotors.add(b);
        Machine m = new Machine(lower, 4, 2, rotors);
        String[] inserted = new String[] {"B", "Beta", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("udo");
        assertEquals("yzro", m.convert("geta"));

    }


    @Test
    public void checkLong() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(ii0);
        rotors.add(iii0);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 5, 3, rotors);
        String[] inserted = new String[] {"B", "Beta", "I", "II", "III"};
        m.insertRotors(inserted);
        Permutation plugboard = new Permutation("(TD) (KC) (JZ)", UPPER);
        m.setPlugboard(plugboard);
        m.setRotors("AAAA");
        assertEquals("HGJNB", m.convert("Iwass".toUpperCase()));
        assertEquals("OKDWA", m.convert("cared".toUpperCase()));
        assertEquals("LBFKU", m.convert("ofcod".toUpperCase()));
        assertEquals("CMUTJZUIO", m.convert("inginJava".toUpperCase()));
        assertEquals("XTYQFBDZRGBYFZCASYRU",
                m.convert("IwasscaredofusingGit".toUpperCase()));
        assertEquals("UAAFWOAGFKOCJGMUMOPCHTAVRSA",
                m.convert("andstartingalltheseprojects".toUpperCase()));
        assertEquals("HXHFRUXOFCBLRYSDXFCZXGVFANA",
                m.convert("Compilerkeepsgettingmadatme".toUpperCase()));
        assertEquals("CNBZHSNQMCMNIRWMTTTQBRNKRXDRPN",
                m.convert("Nowmyprojectonlyrunsinmydreams".toUpperCase()));
    }


    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class,
                UnitTest.class);
    }


}


