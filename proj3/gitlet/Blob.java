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
        byte[] contents = Utils.readContents(f);
        _code = Utils.sha1(contents, _name);
    }

    /** Return the file name of this blob. **/
    public String name() {
        return _name;
    }

    /** Return the sha-1 code of this file. **/
    public String code() {
        return _code;
    }

    /** This blob's file name. **/
    private String _name;

    /** Sha-1 code of this blob's serialized file. **/
    private String _code;
}
