import java.io.IOException;
import java.io.Reader;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Sabrina Xia
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    public TrReader(Reader str, String from, String to) {
        _source = str;
        _from = from;
        _to = to;
    }

    Reader _source;
    String _from;
    String _to;


    public int read(char[] cbuf, int off, int len) throws IOException {
        int j = off;
        int i = 0;
        for (; i < len; i++, j++) {
            char c = (char) _source.read();
            String s = Character.toString(c);
            s = Translate.translate(s, _from, _to);
            c = s.charAt(0);
            cbuf[j] = c;
        }
        return i;
    }

    @Override
    public void close() throws IOException {
        _source = null;
    }

}
