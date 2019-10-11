package enigma;


import java.util.ArrayList;
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
        _plugboard = null;
    }

    /** Return all possible rotors in Machine. */
    Rotor[] allRotors() {
        return (Rotor[]) _allRotors;
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
        int pawls = _numPawls;
        if (rotors.length > _numRotors) {
            throw new EnigmaException("not enough rotor slots in machine");
        } else if (rotors.length < _numRotors) {
            throw new EnigmaException("not enough rotors");
        }
        _myRotors = new Rotor[_numRotors];
        ArrayList<Rotor> used = new ArrayList<Rotor>();
        for (int i = 0; i < rotors.length; i++) {
            String r = rotors[i];
            boolean contains = false;
            boolean moving = false;
            for (int j = 0; j < _allRotors.length; j++) {
                Rotor p = (Rotor) _allRotors[j];
                String pName = p.name();
                if (pName.equals(r)) {
                    _myRotors[i] = p;
                    contains = true;
                    if (used.contains(p)) {
                        throw new EnigmaException("repeated rotor");
                    }
                    used.add(p);
                    if (p.rotates()) {
                        moving = true;
                        pawls -= 1;
                    } else if (moving && !p.rotates()) {
                        throw new EnigmaException("fixedRotors wrong posn");
                    } else if (pawls < 0) {
                        throw new EnigmaException("too many moving rotors");
                    } else if (i == 0 && !p.reflecting()) {
                        throw new EnigmaException("leftmost not reflector");
                    } else if (i != 0 && p.reflecting()) {
                        throw new EnigmaException("reflector wrong posn");
                    }
                }
            }
            if (!contains) {
                throw new EnigmaException("Rotor" + r + "not possible");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("setting is not valid length");
        }
        for (int i = 0; i < setting.length(); i++) {
            char c = setting.charAt(i);
            if (_alphabet.toInt(c) == -1) {
                throw new EnigmaException("setting not in alphabet");
            }
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

    /** Advancing all rotors in machine if meets conditions. */
    public void advanceMachine() {
        boolean[] rotate = new boolean[_numRotors];
        rotate[_numRotors - 1] = true;
        for (int i = 1; i < _numRotors - 1; i++) {
            if (_myRotors[i + 1].atNotch()) {
                rotate[i] = true;
                rotate[i + 1] = true;
            }
        }
        for (int i = 1; i < rotate.length; i++) {
            if (rotate[i]) {
                _myRotors[i].advance();
            }
        }
    }


    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceMachine();
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        for (int i = _numRotors - 1; i >= 0; i--) {
            Rotor r = _myRotors[i];
            c = r.convertForward(c);
        }
        for (int i = 1; i < _numRotors; i++) {
            Rotor r = _myRotors[i];
            c = r.convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int toConvert = _alphabet.toInt(msg.charAt(i));
            int convertedInt = convert(toConvert);
            char converted = _alphabet.toChar(convertedInt);
            result += converted;
        }
        return result;
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
