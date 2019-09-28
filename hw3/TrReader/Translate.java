import java.io.IOException;

/** String translation.
 *  @author your name here
 */
public class Translate {
    /** This method should return the String S, but with all characters that
     *  occur in FROM changed to the corresponding characters in TO.
     *  FROM and TO must have the same length.
     *  NOTE: You must use your TrReader to achieve this. */
    static String translate(String S, String from, String to) {
        /* NOTE: The try {...} catch is a technicality to keep Java happy. */
        char[] buffer = new char[S.length()];
        try {
            if (from.length() == 0) {
                return S;
            }
            char[] c = S.toCharArray();
            char[] f = from.toCharArray();
            char[] t = to.toCharArray();
            if (f.length != t.length) {
                throw new IOException();
            }
            for (int i = 0; i < c.length; i++) {
                for (int j = 0; j < f.length; j++) {
                    if (c[i] == f[j]) {
                        c[i] = t[j];
                        break;
                    }
                }
            }
            String s = new String(c);
            return s;
        } catch (IOException e) {
            return null;
        }
    }
    /*
       REMINDER: translate must
      a. Be non-recursive
      b. Contain only 'new' operations, and ONE other method call, and no
         other kinds of statement (other than return).
      c. Use only the library classes String, and any classes with names
         ending with "Reader" (see online java documentation).
    */
}
