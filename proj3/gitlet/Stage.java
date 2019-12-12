package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/** The staging area of this gitlet directory.
 * @author Sabrina Xia
 */
public class Stage implements Serializable {

    /** All files staged and their contents in blobs, together in a
     * Hashmap. **/
    private HashMap<String, Blob> _staged;


    Stage() {
        _staged = new HashMap<>();
    }

    /** Returns true only if file S is staged. **/
    public boolean contains(String s) {
        return _staged.containsKey(s);
    }

    /** Returns the Blob of file S in this stage. **/
    public Blob get(String s) {
        return _staged.getOrDefault(s, null);
    }

    /** Returns all file names and their Blobs(contents) in a Hashmap. **/
    public HashMap<String, Blob> getAll() {
        return _staged;
    }

    public boolean isEmpty() {
        return _staged.isEmpty();
    }

    public void add(String filename) {
        File f = new File(filename);
        File copy = new File(".gitlet/stage/" + filename);
        Object contents = Utils.readContents(f);
        Utils.writeContents(copy, contents);
        Blob b = new Blob(copy.getName());
        _staged.put(filename, b);
    }

    public void rewrite(String s) {
        File staged = new File(".gitlet/stage/" + s);
        File f = new File(s);
        Object contents = Utils.readContents(f);
        Utils.writeContents(staged, contents);
    }

    public void remove(String s) {
        _staged.remove(s);
        File toRemove = new File(".gitlet/stage/" + s);
        toRemove.delete();
    }

    public void clear() {
        for (String s : _staged.keySet()) {
            File f = new File(".gitlet/stage/" + s);
            f.delete();
        }
        _staged.clear();
    }
}
