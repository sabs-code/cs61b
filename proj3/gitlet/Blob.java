package gitlet;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/** Represents and stores the contents of a file.
 * @author Sabrina Xia
 */
public class Blob implements Serializable {

    /** Serializes the file. **/
    Blob(String file) {
        _name = file;
        File f = new File(file);
        _contents = Utils.readContents(f);
        _code = hash();
    }

    /** Return the file name of this blob. **/
    public String name() {
        return _name;
    }

    /** Return the sha-1 code of this file. **/
    public String code() {
        return _code;
    }

    public String hash() {
        ArrayList<Object> toHash = new ArrayList<>();
        toHash.add(_name);
        toHash.add(_contents);
        return Utils.sha1(toHash);
    }

    /** This blob's file name. **/
    private String _name;

    /** Sha-1 code of this blob's serialized file. **/
    private String _code;

    private byte[] _contents;
}
