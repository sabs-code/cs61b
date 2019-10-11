package enigma;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Sabrina Xia
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        String used = "";
        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (used.indexOf(c) != -1) {
                throw new EnigmaException("invalid alphabet");
            }
            used += c;
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        return _chars.indexOf(ch) > -1;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw new EnigmaException("index out of range of alphabet");
        }
        return _chars.charAt(index);
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        ch = Character.toUpperCase(ch);
        int result = _chars.indexOf(ch);
        if (result == -1) {
            throw new EnigmaException(ch + "not in alphabet");
        }
        return result;
    }

    /** Returns the characters in this alphabet in a string. */
    String strAlphabet() {
        return _chars;
    }

    /** Characters of this alphabet. */
    private String _chars;

}
