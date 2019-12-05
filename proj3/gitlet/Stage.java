package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/** The staging area of this gitlet directory.
 * @author Sabrina Xia
 */
public class Stage implements Serializable {

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

    public HashMap<String, Blob> getAll() {
        return _staged;
    }

    public boolean isEmpty() {
        return _staged.isEmpty();
    }

    public void add(String filename) {
        File f = new File(filename);
        File copy = new File(".gitlet/stage/" + filename);
        byte[] contents = Utils.readContents(f);
        Utils.writeContents(copy, contents);
        Blob b = new Blob(copy.getName());
        _staged.put(filename, b);
    }

    public void rewrite(String s) {
        File staged = new File(".gitlet/stage/" + s);
        File f = new File(s);
        byte[] contents = Utils.readContents(f);
        Utils.writeContents(staged, contents);
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
