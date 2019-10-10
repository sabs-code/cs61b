package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Sabrina Xia
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles.split("\\)");
        _numCycles = _cycles.length;
        for (int i = 0; i < _numCycles; i++) {
            _cycles[i] = _cycles[i].replaceAll("\\(", " ");
            _cycles[i] = _cycles[i].trim();
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] newCycle = new String[_numCycles + 1];
        System.arraycopy(_cycles, 0, newCycle, 0, _numCycles);
        newCycle[_numCycles + 1] = cycle;
        _cycles = newCycle;
        _numCycles += 1;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        String alpha = _alphabet.strAlphabet();
        char ch = alpha.charAt(p);
        char cResult = ch;
        for (int i = 0; i < _numCycles; i++) {
            String thisCycle = _cycles[i];
            int index = thisCycle.indexOf(ch);
            if (index == -1) {
                continue;
            }
            else if (index == thisCycle.length() - 1) {
                cResult = thisCycle.charAt(0);
            } else {
                cResult = thisCycle.charAt(index + 1);
            }
        }
        int result = alpha.indexOf(cResult);
        return result;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        String alpha = _alphabet.strAlphabet();
        char ch = alpha.charAt(c);
        char cResult = ch;
        for (int i = 0; i < _numCycles; i++) {
            String thisCycle = _cycles[i];
            int index = thisCycle.indexOf(ch);
            if (index == -1) {
                continue;
            }
            else if (index == 0) {
                cResult = thisCycle.charAt(thisCycle.length() - 1);
            } else {
                cResult = thisCycle.charAt(index - 1);
            }
        }
        int result = alpha.indexOf(cResult);
        return result;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        String alpha = _alphabet.strAlphabet();
        int i = alpha.indexOf(p);
        int intResult = permute(i);
        char result = alpha.charAt(intResult);
        return result;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        String alpha = _alphabet.strAlphabet();
        int i = alpha.indexOf(c);
        int intResult = invert(i);
        char result = alpha.charAt(intResult);
        return result;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        String alpha = _alphabet.strAlphabet();
        for (int i = 0; i < alpha.length(); i++) {
            if (i == permute(i)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Permutation cycles in a String Array. */
    private String[] _cycles;

    /** Number of elements in _CYCLES */
    private int _numCycles;

    /** Characters that map to themselves (self loops). */
    private String _selfloops;
}
