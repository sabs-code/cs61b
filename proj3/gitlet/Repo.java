package gitlet;


import com.sun.xml.internal.xsom.impl.scd.Iterators;
import edu.neu.ccs.util.FileUtilities;
import org.checkerframework.checker.units.qual.A;
import java.io.File;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.*;

/** All commits in the gitlet directory, represented as a tree.
 * @author Sabrina Xia
 */
public class Repo implements Serializable{
    Repo() {
        _commits = new HashSet<>();
        _branches.put("master", null);
        _staging = new Stage();
    }

    public void init() {
        Commit c = new Commit("master", "initial commit");
        addCommit(c);
    }

    /** Add C to commit tree and update pointers. **/
    public void addCommit(Commit c) {
        String s = c.code();
        _commits.add(s);
        _head = s;
        if (_branches.containsKey(c.branch())) {
            _branches.replace(c.branch(), c.code());
        } else {
            _branches.put(c.branch(), c.code());
        }
        File f = new File(".gitlet/commits/" + s);
        Utils.writeObject(f, c);
    }

    public Commit head() {
        File headFile = new File(".gitlet/commits/" + _head);
        Commit headCommit = Utils.readObject(headFile, Commit.class);
        return headCommit;
    }

    public Stage getStage() {
        return _staging;
    }

    public boolean hasCommit(String id) {
        return _commits.contains(id);
    }

    public Commit getCommit(String id) {
        File commitFile = new File(".gitlet/commits/" + id);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }

    public void updateUntracked(HashSet<String> fileNames) {
        if (fileNames.size() != 0) {
            for (String name : fileNames) {
                if (_trackedFiles.contains(name)) {
                    fileNames.remove(name);
                }
            }
        }
        if (fileNames.size() != 0) {
            for (String name : fileNames) {
                if (_staging.contains(name)) {
                    fileNames.remove(name);
                }
            }
        }
        _untracked.addAll(fileNames);
    }

    public void add(String s) {
        if (_staging.contains(s)) {
            Blob b = new Blob(s);
            Commit curr = head();
            Blob commited = curr.getFile(s);
            if (commited != null) {
                if (commited.code().equals(b.code())) {
                    _staging.remove(s);
                }
            }
            Blob staged = _staging.get(s);
            if (staged != null) {
                if (staged.code().equals(b.code())) {
                } else {
                    _staging.rewrite(s);
                }
            }
        } else {
            _staging.add(s);
        }
        _untracked.remove(s);
        _removed.remove(s);
        _trackedFiles.add(s);
    }

    public void commit(ArrayList<String> operands) {
        operands.remove(0);
        String log = operands.toString();
        Commit c = new Commit(head().branch(), log, head(), _staging, _removed);
        addCommit(c);
        _removed.clear();
        _staging.clear();
        _commits.add(c.code());
        _head = c.code();
    }

    public void remove(String filename) {
        Commit headCommit = head();
        if (!headCommit.getBlobs().containsKey(filename)
                && !_staging.contains(filename)) {
            System.out.println("No reason to remove the file.");
        } else if (_staging.contains(filename)) {
            _staging.remove(filename);
        } else if (headCommit.getBlobs().containsKey(filename)) {
            _removed.add(filename);
            File file = new File(filename);
            Utils.restrictedDelete(file);
        }
        _trackedFiles.remove(filename);
        _untracked.add(filename);
    }

    public void log() {
        for (Commit c = head(); c != null; c = c.parent()) {
            System.out.println("===");
            System.out.println("commit " + c.code());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getLogMessage());
            System.out.println();
        }
    }

    public void globalLog() {
        HashSet<String> printed = new HashSet<>();
        for (String branch : _branches.keySet()) {
            String commitid = _branches.get(branch);
            for (Commit c = getCommit(commitid); c != null; c = c.parent()) {
                if (!printed.contains(c.code())) {
                    System.out.println("===");
                    System.out.println("commit " + c.code());
                    System.out.println("Date: " + c.getTimestamp());
                    System.out.println(c.getLogMessage());
                    System.out.println();
                    printed.add(c.code());
                }
            }
        }
    }

    public void find(String logMessage) {
        boolean exist = false;
        for (String commitid : _commits) {
            Commit c = getCommit(commitid);
            if (c.getLogMessage().equals(logMessage)) {
                System.out.println(c.code());
                exist = true;
            }
        }
        if (!exist) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() {
        System.out.println("=== Branches ===");
        ArrayList<String> helper = new ArrayList<>(_branches.keySet());
        Collections.sort(helper);
        for (String branch : helper) {
            if (branch.equals(head().branch())) {
                System.out.println("*" + helper.remove(branch));
            } else {
                System.out.println(helper.remove(branch));
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        helper.addAll(_staging.getAll().keySet());
        ArrayList<String> modified = new ArrayList<>();
        ArrayList<String> deleted = new ArrayList<>();
        for (String file : helper) {
            File staged = new File(".gitlet/stage/" + file);
            File working = new File(file);
            if (!working.exists()) {
                deleted.add(file);
                helper.remove(file);
            } else if (!Utils.readContentsAsString(staged).equals(
                    Utils.readContentsAsString(working))) {
                modified.add(file);
                helper.remove(file);
            }
        }
        Collections.sort(helper);
        for (String s : helper) {
            System.out.println(s);
            helper.remove(s);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        helper.addAll(_removed);
        Collections.sort(helper);
        for (String s : helper) {
            System.out.println(s);
            helper.remove(s);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        ArrayList<String> untracked = new ArrayList<>(_untracked);
        Collections.sort(untracked);
        for (String s : untracked) {
            System.out.println(s);
        }
    }

    public void checkoutFile(ArrayList<String> operands) {
        HashMap<String, Blob> headFileToBlobs = head().getBlobs();
        String filename = operands.remove(0);
        if (!headFileToBlobs.containsKey(filename)) {
            throw Utils.error("File does not exist in that commit.");
        } else {
            Blob blobHeadCommit = headFileToBlobs.get(filename);
            File toOverwrite = new File(blobHeadCommit.name());
            Utils.writeContents(toOverwrite, blobHeadCommit.getContents());
        }
    }

    public void checkoutFileinCommit(String commitid, String filename) {
        if (commitid.length() < 40) {
            for (String s : _commits) {
                if (s.substring(0, commitid.length()).equals(commitid)) {
                    commitid = s;
                    break;
                }
            }
        }
        Commit targetCommit = getCommit(commitid);
        if (!targetCommit.getBlobs().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
        } else {
            Blob targetFile = targetCommit.getBlobs().get(filename);
            File toOverwrite = new File(targetFile.name());
            Utils.writeContents(toOverwrite, targetFile.getContents());
        }
    }

    public void checkoutBranch(String branchName) {
        if (!_branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else if (head().branch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
        } else {
            String commitid = _branches.get(branchName);
            Commit commit = getCommit(commitid);
            HashMap<String, Blob> blobsInCommit = commit.getBlobs();
            for (String filename : blobsInCommit.keySet()) {
                if (_untracked.contains(filename)) {
                    File curr = new File(filename);
                    byte[] currInBytes = Utils.serialize(curr);
                    byte[] commitInBytes = (byte[]) blobsInCommit.get(
                            filename).getContents();
                    if (!Arrays.equals(currInBytes, commitInBytes)) {
                        System.out.println("There is an untracked file in "
                               + "the way; delete it or add it first.");
                        System.exit(0);
                    }
                }
            }
            for (String filename : blobsInCommit.keySet()) {
                File curr = new File(filename);
                Object commited = blobsInCommit.get(filename).getContents();
                Utils.writeContents(curr, commited);
            }
            for (String filename : _trackedFiles) {
                if (!blobsInCommit.keySet().contains(filename)) {
                    Utils.restrictedDelete(new File(filename));
                }
            }
            _staging.clear();
            _head = commitid;
        }
    }

    /** All commits in this gitlet directory. **/
    private HashSet<String> _commits;

    /** Commit id of the head of this gitlet directory. **/
    private String _head;

    /** Pointers to all existing branches. **/
    private HashMap<String, String> _branches = new HashMap<>();

    /** The current staging area. **/
    private Stage _staging;

    /** File names of untracked files. **/
    private HashSet<String> _untracked = new HashSet<>();

    private HashSet<String> _trackedFiles = new HashSet<>();

    private HashSet<String> _removed = new HashSet<>();

}
