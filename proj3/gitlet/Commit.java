package gitlet;


import com.sun.xml.internal.xsom.impl.scd.Iterators;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

/** Represents a single commit. Stores all information in this commit.
 * @author Sabrina Xia
 */
public class Commit implements Serializable {

    Commit(String branch, String logMessage) {
        _logMessage = logMessage;
        _branch = branch;
        _parent = null;
        _timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        _code = initCode();
    }

    /** Creates a new Commit. **/
    Commit(String branch, String logMessage, Commit parent, Stage stage) {
        _logMessage = logMessage;
        _branch = branch;
        _parent = parent;
        ZonedDateTime now = ZonedDateTime.now();
        _timestamp = now.format(DateTimeFormatter.ofPattern("EEE " +
                    "MMM d HH:mm:ss yyyy xxxx"));
        HashMap<String, Blob> parentBlobs = parent._blobs;
        for (String s : parentBlobs.keySet()) {
            _blobs.put(s, parentBlobs.get(s));
        }
        for (String filename: stage.getAll().keySet()) {
            if (_blobs.containsKey(filename)) {
                _blobs.replace(filename, stage.get(filename));
            } else {
                _blobs.put(filename, stage.get(filename));
            }
        }
        _code = hash();
    }

    public String initCode() {
        ArrayList<Object> toHash = new ArrayList<>();
        toHash.add(_logMessage);
        toHash.add(_branch);
        toHash.add(_timestamp);
        return Utils.sha1(toHash);
    }

    public String hash() {
        ArrayList<String> toHash = new ArrayList<>();
        toHash.add(_logMessage);
        toHash.add(_branch);
        toHash.add(_parent._code);
        toHash.add(_timestamp);
        for (String s : _blobs.keySet()) {
            Blob b = _blobs.get(s);
            toHash.add(b.code());
        }
        return Utils.sha1(toHash);
    }

    public HashMap<String, Blob> getBlobs() {
        return _blobs;
    }

    public String branch() {
        return _branch;
    }

    public Blob getFile(String s) {
        return _blobs.get(s);
    }

    public String code() {
        return _code;
    }

    /** The log message associated with this commit. **/
    private String _logMessage;

    /** The timestamp of this commit. **/
    private String _timestamp;

    /** All files in this commit. **/
    private HashMap<String, Blob> _blobs = new HashMap<>();

    /** The parent of this commit. **/
    private transient Commit _parent;

    /** The branch this commit is in. **/
    private String _branch;

    private String _code;

    private ArrayList<String> _names = new ArrayList<>();
}
