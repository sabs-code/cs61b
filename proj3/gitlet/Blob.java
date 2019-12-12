package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;

/** Represents and stores the contents of a file.
 * @author Sabrina Xia
 */
public class Blob implements Serializable {

    /** Unserializes the file. **/
    Blob(String file) {
        _name = file;
        File f = new File(file);
        _contents = Utils.readContents(f);
        _contentsAsString = Utils.readContentsAsString(f);
        _code = hash();
    }

    /** Return the file name this blob corresponds to. **/
    public String name() {
        return _name;
    }

    /** Return the sha-1 code of this file. **/
    public String code() {
        return _code;
    }

    /** Returns the contents of this blob in a byte array. **/
    public Object getContents() {
        return _contents;
    }

    /** Computes and returns the unique hash code of this blob. **/
    public String hash() {
        ArrayList<Object> toHash = new ArrayList<>();
        toHash.add(_name);
        toHash.add(_contents);
        return Utils.sha1(toHash);
    }

    /** Returns the contents of this blob in String. **/
    public String getContentAsString() {
        return _contentsAsString;
    }

    /** This blob's file name. **/
    private String _name;

    /** Sha-1 code of this blob's serialized file. **/
    private String _code;

    /** Contents of this blob/file in byte array. **/
    private byte[] _contents;

    /** Contents of this blob/file in string. **/
    private String _contentsAsString;
}
