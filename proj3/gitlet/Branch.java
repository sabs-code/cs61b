package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/** Class that represents a single branch. Used to store information
 * about branch name, branch head, and tracked files in this branch.
 * @author Sabrina Xia
 */
public class Branch implements Serializable {
    /** The sha-1 code of the head commit of this branch. **/
    private String _head;

    /** All tracked files in this branch. **/
    private HashSet<String> _tracked = new HashSet<>();

    /** Name of this branch in string. **/
    private String _name;

    /** Creates a new branch with branch name BRANCHNAME, and head commit
     * being the commit with sha-1 code COMMITID.
     */
    Branch(String branchname, String commitid) {
        _name = branchname;
        updatehead(commitid);
    }

    /** Creates a new branch with branch name BRANCHNAME and no head. **/
    Branch(String branchname) {
        _name = branchname;
    }

    /** Add the commit with sha-1 code COMMITID to this branch and update
     * the head of this branch to be this commit. Also update all tracked
     * files in this branch.
     */
    public void updatehead(String commitid) {
        _head = commitid;
        File f = new File(".gitlet/commits/" + commitid);
        Commit head = Utils.readObject(f, Commit.class);
        HashMap<String, Blob> headBlobs = head.getBlobs();
        _tracked.addAll(headBlobs.keySet());
    }

    /** Returns all tracked files in this branch. **/
    public HashSet<String> tracked() {
        return _tracked;
    }

    /** Returns true only if file with FILENAME is tracked in this branch. **/
    public boolean tracked(String filename) {
        return _tracked.contains(filename);
    }

    /** Returns the name of this branch. **/
    public String name() {
        return _name;
    }

    /** Returns the sha-1 code of the head commit of this branch. **/
    public String head() {
        return _head;
    }
}
