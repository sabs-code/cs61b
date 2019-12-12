package gitlet;



import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.util.HashSet;

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
    Commit(String branch, String logMessage, Commit parent, Stage stage,
           HashSet removed) {
        _logMessage = logMessage;
        _branch = branch;
        _parent = parent.code();
        ZonedDateTime now = ZonedDateTime.now();
        _timestamp = now.format(DateTimeFormatter.ofPattern("EEE " +
                    "MMM d HH:mm:ss yyyy xxxx"));
        HashMap<String, Blob> parentBlobs = parent._blobs;
        for (String s : parentBlobs.keySet()) {
            if (!removed.contains(s)) {
                _blobs.put(s, parentBlobs.get(s));
            }
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

    Commit(String branch, String logMessage, Commit parent, Commit mergeParent,
           Stage stage, HashSet removed) {
        this(branch, logMessage, parent, stage, removed);
        _mergeParent = mergeParent.code();
    }

    public String initCode() {
        ArrayList<Object> toHash = new ArrayList<>();
        toHash.add(_logMessage);
        toHash.add(_branch);
        toHash.add(_timestamp);
        return Utils.sha1(toHash);
    }

    public String hash() {
        ArrayList<Object> toHash = new ArrayList<>();
        toHash.add(_logMessage);
        toHash.add(_branch);
        toHash.add(_parent);
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
        return _blobs.getOrDefault(s, null);
    }

    public String code() {
        return _code;
    }

    public Commit parent() {
        if (_parent == null) {
            return null;
        }
        File parentFile = new File(".gitlet/commits/" + _parent);
        Commit parent = Utils.readObject(parentFile, Commit.class);
        return parent;
    }

    public String getTimestamp() {
        return _timestamp;
    }

    public String getLogMessage() {
        return _logMessage;
    }

    public void setMergeParent(String parentid) {
        _mergeParent = parentid;
    }

    public String getMergeParent() {
        return _mergeParent;
    }

    /** The log message associated with this commit. **/
    private String _logMessage;

    /** The timestamp of this commit. **/
    private String _timestamp;

    /** All files in this commit. **/
    private HashMap<String, Blob> _blobs = new HashMap<>();

    /** The parent of this commit. **/
    private String _parent;

    /** The branch this commit is in. **/
    private String _branch;


    private String _code;

    private ArrayList<String> _names = new ArrayList<>();

    private String _mergeParent;
}
