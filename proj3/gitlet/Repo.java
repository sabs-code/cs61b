package gitlet;


import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/** All commits in the gitlet directory, represented as a tree.
 * @author Sabrina Xia
 */
public class Repo {
    Repo() {
        _commits = new HashMap<>();
        _branches.put("master", null);
        Commit c = new Commit("master", "initial commit");
        addCommit(c);
        _staging = new Stage();
    }

    /** Add C to commit tree and update pointers. **/
    public void addCommit(Commit c) {
        String s = c.code();
        _commits.put(s, c);
        _head = c;
        if (_branches.containsKey(c.branch())) {
            _branches.replace(c.branch(), c);
        } else {
            _branches.put(c.branch(), c);
        }
        File f = new File(".gitlet/commits/" + c.code());
        Utils.writeObject(f, Utils.serialize(c));
    }

    public Commit head() {
        return _head;
    }

    public Stage getStage() {
        return _staging;
    }

    public void add(String s) {
        if (_staging.contains(s)) {
            Blob b = new Blob(s);
            Commit curr = head();
            Blob commited = curr.getFile(s);
            if (commited.code().equals(b.code())) {
                _staging.remove(s);
            }
            Blob staged = _staging.get(s);
            if (staged.code().equals(b.code())) {
            } else {
                _staging.rewrite(s);
            }
        } else {
            _staging.add(s);
        }
    }

    public void commit(ArrayList<String> operands) {
        String log = operands.toString();
        Commit c = new Commit(_head.branch(), log, _head, _staging);
        _staging.clear();
        _commits.put(c.code(), c);
        _head = c;
        addCommit(c);
    }

    /** All commits in this gitlet directory. **/
    private HashMap<String, Commit> _commits;

    /** Head pointer of this gitlet directory. **/
    private Commit _head;

    /** Pointers to all existing branches. **/
    private HashMap<String, Commit> _branches = new HashMap<>();

    /** The current staging area. **/
    private Stage _staging;
}
