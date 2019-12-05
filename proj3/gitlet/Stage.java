package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** The staging area of this gitlet directory.
 * @author Sabrina Xia
 */
public class Stage {

    /** All files added to the stage. **/
    private HashMap<String, Blob> _staged;

    private ArrayList<File> _stagedFiles;


    Stage() {
        _staged = new HashMap<>();
        _stagedFiles = new ArrayList<>();
    }

    public boolean contains(String s) {
        return _staged.containsKey(s);
    }

    public Blob get(String s) {
        return _staged.get(s);
    }

    public ArrayList<File> getAll() {
        return _stagedFiles;
    }

    public boolean isEmpty() {
        return _staged.isEmpty();
    }

    public void add(String s) {
        File f = new File(s);
        File copy = new File(".gitlet/stage/" + s);
        Utils.writeContents(copy, f);
        Blob b = new Blob(copy.getName());
        _staged.put(s, b);
        _stagedFiles.add(copy);
    }

    public void rewrite(String s) {
        File staged = new File(".gitlet/stage/" + s);
        File f = new File(s);
        Utils.writeContents(staged, f);
    }

    public void remove(String s) {
        File toRemove = new File(".gitlet/stage/" + s);
        Utils.restrictedDelete(toRemove);
    }

    public void clear() {
        _staged.clear();
        while (_stagedFiles.size() > 0) {
            File f = _stagedFiles.remove(0);
            Utils.restrictedDelete(f);
        }
    }
}
