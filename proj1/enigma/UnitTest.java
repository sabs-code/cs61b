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
            "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER);
    Rotor I = new MovingRotor("I", pI, "Q");
    Permutation pII = new Permutation(
            "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)", UPPER);
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
        Machine m = new Machine(UPPER, 4, 3, rotors);
        String[] inserted = new String[] {"B", "III", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("AAA");
        assertEquals(1, m.convert(0));
        m.setRotors("VAA");
        assertEquals(3, m.convert(0));
        m.setRotors("AAQ");
        assertEquals(12, m.convert(0));
        m.setRotors("AEQ");
        assertEquals(20, m.convert(0));
        m.setRotors("UDO");
        assertEquals(8, m.convert(6));
        assertEquals(21, m.convert(4));
        assertEquals(2, m.convert(19));
        assertEquals(21, m.convert(0));
        m.setRotors("UDO");
        assertEquals("IVCV", m.convert("GETA"));
        Permutation plug = new Permutation("(GA)", UPPER);
        m.setPlugboard(plug);
        m.setRotors("UDO");
        assertEquals(8, m.convert(0));
        assertEquals(21, m.convert(4));
        assertEquals(2, m.convert(19));
        assertEquals(21, m.convert(6));
        m.setRotors("UDO");
        assertEquals("IVCV", m.convert("AETG"));
    }

    @Test
    public void checkFixedRotors() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(II);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 2, rotors);
        String[] inserted = new String[] {"B", "beta", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("UDO");
        assertEquals(8, m.convert(6));
        assertEquals(12, m.convert(4));
        assertEquals(23, m.convert(19));
        assertEquals(1, m.convert(0));
        assertEquals(3, m.convert(0));
        m.setRotors("UDO");
        assertEquals("IMXAD", m.convert("GETAA"));
    }

    @Test
    public void checkLowerCase() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(II);
        rotors.add(beta);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 2, rotors);
        String[] inserted = new String[] {"B", "beta", "II", "I"};
        m.insertRotors(inserted);
        m.setRotors("UDO");
        assertEquals("imxad", m.convert("getta"));

    }


    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class);
    }


}


