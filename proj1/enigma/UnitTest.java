package enigma;

import net.sf.saxon.functions.UpperCase;
import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the enigma package.
 *  @author Sabrina Xia
 */
public class UnitTest {
    Permutation pI = new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER);
    Rotor I = new MovingRotor("I", pI, "Q");
    Permutation pII = new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)", UPPER);
    Rotor II = new MovingRotor("II", pII, "E");
    Permutation pIII = new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER);
    Rotor III = new MovingRotor("III", pIII, "V");
    Permutation pB = new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) "
            + "(MP) (RX) (SZ) (TV)", UPPER);
    Rotor B = new Reflector("B", pB);


    @Test
    public void checkInsertRotor() {
        Collection<Rotor> rotors = new ArrayList<Rotor>(12);
        rotors.add(I);
        rotors.add(II);
        rotors.add(III);
        rotors.add(B);
        Machine m = new Machine(UPPER, 4, 3, rotors);
        String[] inserted = new String[] {"B", "III", "II", "I"};
        m.insertRotors(inserted);
    }


    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class);
    }


}


