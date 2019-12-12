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
    /** Creates a new commit in branch BRANCH, with log message being
     * LOGMESSAGE. Only used when creating the first commit in gitlet.
     */
    Commit(String branch, String logMessage) {
        _logMessage = logMessage;
        _branch = branch;
        _parent = null;
        _timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        _code = initCode();
    }

    /** Creates a new commit in branch named BRANCH, with log message being
     * LOGMESSAGE, parent commit being PARENT. All files/blobs in STAGE should
     * be added and tracked, as well as files in parent commit. All files in
     * REMOVED should not be tracked in this commit.
     */
    Commit(String branch, String logMessage, Commit parent, Stage stage,
           HashSet removed) {
        _logMessage = logMessage;
        _branch = branch;
        _parent = parent.code();
        ZonedDateTime now = ZonedDateTime.now();
        _timestamp = now.format(DateTimeFormatter.ofPattern("EEE "
                + "MMM d HH:mm:ss yyyy xxxx"));
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

    /** Create a new merge commit in branch named BRANCH, with logMessage being
     * LOGMESSAGE, first parent being PARENT and merge parent being
     * MERGEPARENT. All files in STAGE should be added and commited. All files
     * in REMOVED should not be tracked.
     */
    Commit(String branch, String logMessage, Commit parent, Commit mergeParent,
           Stage stage, HashSet removed) {
        this(branch, logMessage, parent, stage, removed);
        _mergeParent = mergeParent.code();
    }

    /** Returns the unique hashcode of the first commit in gitlet. **/
    public String initCode() {
        ArrayList<Object> toHash = new ArrayList<>();
        toHash.add(_logMessage);
        toHash.add(_branch);
        toHash.add(_timestamp);
        return Utils.sha1(toHash);
    }

    /** Computes and returns the hash code of this commit. **/
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

    /** Returns all files and its corresponding blobs in a Hashmap. **/
    public HashMap<String, Blob> getBlobs() {
        return _blobs;
    }

    /** Returns the name of the branch that this commit is in. **/
    public String branch() {
        return _branch;
    }

    /** Returns the hashcode(sha-1) of this commit. **/
    public String code() {
        return _code;
    }

    /** Returns the parent commit of this commit. Returns null if this is the
     * first commmit in gitlet.
     */
    public Commit parent() {
        if (_parent == null) {
            return null;
        }
        File parentFile = new File(".gitlet/commits/" + _parent);
        Commit parent = Utils.readObject(parentFile, Commit.class);
        return parent;
    }

    /** Returns the timestamp of this commit. **/
    public String getTimestamp() {
        return _timestamp;
    }

    /** Returns the log message of this commit. **/
    public String getLogMessage() {
        return _logMessage;
    }

    /** Returns the code of second(merge) parent of this commit in a string
     * if this is a merge commit. If not, return null.
     */
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

    /** The unique sha-1 code. **/
    private String _code;

    /** The second(merge) parent of this commit. Not null only if
     * this commit is a merge commit.
     */
    private String _mergeParent;
}
