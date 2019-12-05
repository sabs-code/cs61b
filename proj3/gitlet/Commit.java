package gitlet;


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
        _blobs = new HashMap<>();
        _logMessage = logMessage;
        _branch = branch;
        _parent = null;
        _timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        _code = Utils.sha1(_logMessage, _branch, _timestamp, _blobs);
    }

    /** Creates a new Commit. **/
    Commit(String branch, String logMessage, Commit parent, Stage s) {
        _logMessage = logMessage;
        _branch = branch;
        _parent = parent;
        ZonedDateTime now = ZonedDateTime.now();
        _timestamp = now.format(DateTimeFormatter.ofPattern("EEE " +
                    "MMM d HH:mm:ss yyyy xxxx"));
        for (File f : s.getAll()) {
            _names.add(f.getName());
            Blob b = new Blob(f.getName());
            _blobs.put(b.code(), b);
        }
        _code = Utils.sha1(_logMessage, _branch, _parent, _timestamp, _blobs);
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
    private HashMap<String, Blob> _blobs;

    /** The parent of this commit. **/
    private transient Commit _parent;

    /** The branch this commit is in. **/
    private String _branch;

    private String _code;

    private ArrayList<String> _names = new ArrayList<>();
}
