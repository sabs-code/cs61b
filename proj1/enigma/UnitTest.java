package enigma;

import net.sf.saxon.functions.UpperCase;
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
            "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV)   " +
                    " (JZ) (S)      ", UPPER);
    Rotor I = new MovingRotor("I", pI, "Q");
    Permutation pII = new Permutation(
            "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ)   (GR) (NT) (A) (Q)", UPPER);
    Rotor II = new MovingRotor("II", pII, "E");
    Permutation pIII = new Permutation(
            "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER);
    Rotor III = new MovingRotor("III", pIII, "V");
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
        rotors.add(II);
        rotors.add(III);
        rotors.add(B);
        String[] cycles = new String[] {"FIXVYOMW", "CDKLHUP", "ESZ", "BJ", "GR", "NT", "A", "Q"};
        assertEquals(cycles.length, pII.cycles().length);
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
        rotors.add(II);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 2, rotors);
        String[] inserted = new String[] {"B", "Beta", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("UDO");
        assertEquals(24, m.convert(6));
        assertEquals(25, m.convert(4));
        assertEquals(17, m.convert(19));
        assertEquals(23, m.convert(0));
        m.setRotors("UDO");
        assertEquals("YZRX", m.convert("GETA"));
    }

    @Test
    public void checkLowerCase() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(II);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 2, rotors);
        String[] inserted = new String[] {"B", "Beta", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("UDO");
        assertEquals("YZRX", m.convert("geta"));

    }

    @Test
    public void checkLong() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(II);
        rotors.add(III);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 5, 3, rotors);
        String[] inserted = new String[] {"B", "Beta", "I", "II", "III"};
        m.insertRotors(inserted);
        Permutation plugboard = new Permutation("(TD) (KC) (JZ)", UPPER);
        m.setPlugboard(plugboard);
        m.setRotors("AAAA");
        assertEquals("HGJNB", m.convert("Iwass"));
        assertEquals("OKDWA", m.convert("cared"));
        assertEquals("LBFKU", m.convert("ofcod"));
        assertEquals("CMUTJZUIO", m.convert("inginJava"));
        assertEquals("XTYQFBDZRGBYFZCASYRU", m.convert("IwasscaredofusingGit"));
        assertEquals("UAAFWOAGFKOCJGMUMOPCHTAVRSA",
                m.convert("andstartingalltheseprojects"));
        assertEquals("HXHFRUXOFCBLRYSDXFCZXGVFANA",
                m.convert("Compilerkeepsgettingmadatme"));
        assertEquals("CNBZHSNQMCMNIRWMTTTQBRNKRXDRPN",
                m.convert("Nowmyprojectonlyrunsinmydreams"));
    }


    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class, UnitTest.class);
    }


}


