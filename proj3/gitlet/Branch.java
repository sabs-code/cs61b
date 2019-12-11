package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Branch implements Serializable {
    private String _head;

    private HashSet<String> _tracked = new HashSet<>();

    private String _name;

    Branch(String branchname, String commitid) {
        _name = branchname;
        updatehead(commitid);
    }

    Branch(String branchname) {
        _name = branchname;
    }

    public void updatehead(String commitid) {
        _head = commitid;
        File f = new File(".gitlet/commits/" + commitid);
        Commit head = Utils.readObject(f, Commit.class);
        HashMap<String, Blob> headBlobs = head.getBlobs();
        _tracked.addAll(headBlobs.keySet());
    }

    public HashSet<String> tracked() {
        return _tracked;
    }

    public boolean tracked(String filename) {
        return _tracked.contains(filename);
    }

    public String name() {
        return _name;
    }

    public String head() {
        return _head;
    }
}
