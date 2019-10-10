package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Sabrina Xia
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors.toArray();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length > _numRotors) {
            throw new EnigmaException("not enough rotor slots in machine");
        }
        _myRotors = new Rotor[rotors.length];
        for (int i = 0; i < rotors.length; i++) {
            String r = rotors[i];
            boolean contains = false;
            for (int j = 0; j < _allRotors.length; j++) {
                Rotor p = (Rotor) _allRotors[j];
                String pName = p.name();
                if (pName.equals(r)) {
                    _myRotors[i] = p;
                    contains = true;
                }
            if (!contains) {
                throw new EnigmaException("Rotor" + r + "not in all possible rotors");
            }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() > _numRotors - 1 || setting.length() < _numRotors - 1) {
            throw new EnigmaException("setting is not valid length")
        }
        int i = 1;
        for (int j = 0; j < setting.length(); j++, i++) {
            char thisSetting = setting.charAt(j);
            _myRotors[i].set(thisSetting);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        return 0; // FIXME
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        return ""; // FIXME
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in my machine. */
    private int _numRotors;

    /** Number of pawls in my machine. */
    private int _numPawls;

    /** All rotors in my machine. */
    private Object[] _allRotors;

    /** All rotors in their slots. */
    private Rotor[] _myRotors;

    /** My plugboard. */
    private Permutation _plugboard;
}
