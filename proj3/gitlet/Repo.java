package gitlet;


import com.sun.xml.internal.xsom.impl.scd.Iterators;
import edu.neu.ccs.HexXShort;
import edu.neu.ccs.util.FileUtilities;
import org.antlr.v4.runtime.misc.Pair;
import org.checkerframework.checker.units.qual.A;
import java.io.File;
import java.lang.Object;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.*;

/** All commits in the gitlet directory, represented as a tree.
 * @author Sabrina Xia
 */
public class Repo implements Serializable{
    Repo() {
        _commits = new HashSet<>();
        _branches.put("master", new Branch("master"));
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
        File f = new File(".gitlet/commits/" + s);
        Utils.writeObject(f, c);
        if (_branches.containsKey(c.branch())) {
            Branch b = _branches.get(c.branch());
            b.updatehead(c.code());
        } else {
            _branches.put(c.branch(), new Branch(c.branch(), c.code()));
        }
    }

    public Commit head() {
        return getCommit(_head);
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
        _untracked = new HashSet<>();
        HashSet<String> helper1 = new HashSet<>(fileNames);
        Branch headBranch;
        if (_head == null) {
            headBranch = _branches.get("master");
        } else {
            headBranch = _branches.get(head().branch());
        }
        if (fileNames.size() != 0) {
            for (String name : fileNames) {
                if (headBranch.tracked(name)) {
                    helper1.remove(name);
                }
            }
        }
        HashSet<String> helper2 = new HashSet<>(helper1);
        if (fileNames.size() != 0) {
            for (String name : fileNames) {
                if (_staging.contains(name)) {
                    helper2.remove(name);
                }
            }
        }
        _untracked.addAll(helper1);
    }

    public boolean nochange() {
        return _removed.isEmpty() && _staging.isEmpty();
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
        _removed.remove(s);
        _untracked.remove(s);
    }

    public void commit(ArrayList<String> operands) {
        String log = operands.remove(0);
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
            Branch b = _branches.get(branch);
            String commitid = b.head();
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
                helper.remove(branch);
            } else {
                System.out.println(branch);
                helper.remove(branch);
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
        HashMap<String, Blob> headBlobs = head().getBlobs();
        for (String file : headBlobs.keySet()) {
            File curr = new File(file);
            if (!curr.exists()) {
                deleted.add(file);
                continue;
            }
            byte[] currContents = Utils.readContents(curr);
            byte[] commited = (byte[]) headBlobs.get(file).getContents();
            if (!Arrays.equals(currContents, commited)) {
                modified.add(file);
            }
        }
        helper.addAll(modified);
        helper.addAll(deleted);
        Collections.sort(helper);
        for (String file : helper) {
            if (modified.contains(file)) {
                System.out.println(file + " (modified)");
            } else {
                System.out.println(file + " (deleted)");
            }
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String s : _untracked) {
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

    public void checkoutCommit(Commit commit) {
        HashMap<String, Blob> blobsInCommit = commit.getBlobs();
        for (String filename : blobsInCommit.keySet()) {
            if (_untracked.contains(filename)) {
                File curr = new File(filename);
                byte[] currInBytes = Utils.readContents(curr);
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
        HashSet<String> trackedFiles = _branches.get(head().branch()).tracked();
        for (String filename : trackedFiles) {
            if (!blobsInCommit.containsKey(filename)) {
                Utils.restrictedDelete(new File(filename));
            }
        }
        _staging.clear();
        _head = commit.code();
    }

    public void checkoutBranch(String branchName) {
        if (!_branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else if (head().branch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
        } else {
            String commitid = _branches.get(branchName).head();
            Commit commit = getCommit(commitid);
            checkoutCommit(commit);
        }
    }

    public void createBranch(String branchname) {
        if (_branches.containsKey(branchname)) {
            System.out.println("A branch with that name already exists.");
        } else {
            Branch b = new Branch(branchname, _head);
            _branches.put(branchname, b);
        }
    }

    public void removeBranch(String branchname) {
        if (!_branches.containsKey(branchname)) {
            System.out.println("A branch with that name does not exist.");
        } else if (head().branch().equals(branchname)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _branches.remove(branchname);
        }
    }

    public void reset(String commitid) {
        if (!_commits.contains(commitid)) {
            System.out.println("No commit with that id exists.");
        } else {
            if (commitid.length() < 40) {
                for (String s : _commits) {
                    if (s.substring(0, commitid.length()).equals(commitid)) {
                        commitid = s;
                        break;
                    }
                }
            }
            Commit commit = getCommit(commitid);
            checkoutCommit(commit);
        }
    }

    public void merge(String branchname) {
        if (!nochange()) {
            System.out.println("You have uncommitted changes.");
        } else if (!_branches.containsKey(branchname)) {
            System.out.println("A branch with that name does not exist.");
        } else if (head().branch().equals(branchname)) {
            System.out.println("Cannot merge a branch with itself.");
        } else {
            Branch target = _branches.get(branchname);
            String splitPoint = findSplitPoint(_head, target.head());
            if (splitPoint.equals(target.head())) {
                System.out.println("Given branch is an ancestor of the current branch.");
            } else if (splitPoint.equals(_head)) {
                _head = target.head();
                System.out.println("Current branch fast-forwarded.");
            } else {
                Commit spCommit = getCommit(splitPoint);
                Commit branch = getCommit(target.head());
                HashMap<String, String> compare = helperCompare(spCommit, branch);
                for (String filename : compare.keySet()) {
                    if (compare.get(filename).equals("rm")) {
                        remove(filename);
                    } else if (compare.get(filename).equals("checkout and stage")) {
                        File curr = new File(filename);
                        byte[] overwrite = (byte[]) branch.getBlobs().get(filename).getContents();
                        Utils.writeContents(curr, (Object) overwrite);
                        add(filename);
                    } else if (compare.get(filename).equals("conflict")) {
                        String newContent = "<<<<<<< HEAD" + "\n";
                        File curr = new File(filename);
                        if (curr.exists()) {
                            newContent += Utils.readContentsAsString(curr);
                        }
                        newContent += "=======" + "\n";
                        if (branch.getBlobs().containsKey(filename)) {
                            newContent += branch.getBlobs().get(filename).getContentAsString();
                        }
                        newContent += ">>>>>>>";
                        Utils.writeContents(curr, newContent);
                    }
                }
            }
        }
    }

    public HashMap<String, String> helperCompare(Commit sp, Commit branch) {
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, Blob> spBlobs = sp.getBlobs();
        HashMap<String, Blob> branchBlobs = branch.getBlobs();
        for (String filename : spBlobs.keySet()) {
            File curr = new File(filename);
            if (!branchBlobs.containsKey(filename)) {
                if (curr.exists()) {
                    byte[] currContents = Utils.readContents(curr);
                    byte[] spContents = (byte[]) spBlobs.get(filename).getContents();
                    if (Arrays.equals(currContents, spContents)) {
                        result.put(filename, "rm");
                    } else {
                        result.put(filename, "conflict");
                    }
                    if (_untracked.contains(filename)) {
                        System.out.println("There is an untracked file in the way; delete it or add it first.");
                        System.exit(0);
                    }
                } else {
                    result.put(filename, "same");
                }
            } else {
                if (!curr.exists()) {
                    byte[] branchContents = (byte[]) branchBlobs.get(filename).getContents();
                    byte[] spContents = (byte[]) spBlobs.get(filename).getContents();
                    if (Arrays.equals(branchContents, spContents)) {
                        result.put(filename, "same");
                    } else {
                        result.put(filename, "conflict");
                        if (_untracked.contains(filename)) {
                            System.out.println("There is an untracked file in the way; delete it or add it first.");
                            System.exit(0);
                        }
                    }
                } else {
                    byte[] currContents = Utils.readContents(curr);
                    byte[] branchContents = (byte[]) branchBlobs.get(filename).getContents();
                    byte[] spContents = (byte[]) spBlobs.get(filename).getContents();
                    if (!Arrays.equals(branchContents, spContents)
                            && Arrays.equals(spContents, currContents)) {
                        result.put(filename, "checkout and stage");
                        if (_untracked.contains(filename)) {
                            System.out.println("There is an untracked file in the way; delete it or add it first.");
                            System.exit(0);
                        }
                    } else if (Arrays.equals(branchContents, spContents)
                            && !Arrays.equals(currContents, spContents)) {
                        result.put(filename, "same");
                    } else if (Arrays.equals(currContents, branchContents)) {
                        result.put(filename, "same");
                    } else if (!Arrays.equals(currContents, spContents)
                            && !Arrays.equals(branchContents, spContents)
                            && !Arrays.equals(currContents, branchContents)) {
                        result.put(filename, "conflict");
                        if (_untracked.contains(filename)) {
                            System.exit(0);
                        }
                    }
                }
            }
        }
        for (String filename : branchBlobs.keySet()) {
            if (!spBlobs.containsKey(filename)) {
                File curr = new File(filename);
                if (!curr.exists()) {
                    result.put(filename, "checkout and stage");
                } else {
                    byte[] currContents = Utils.readContents(curr);
                    byte[] branchContents = (byte[]) branchBlobs.get(filename).getContents();
                    if (!Arrays.equals(currContents, branchContents)) {
                        result.put(filename, "conflict");
                    }
                }
            }
        }
        return result;
    }

    public String findSplitPoint(String first, String second) {
        HashMap<String, Integer> helper = new HashMap<>();
        int i = 0;
        for (Commit c = getCommit(first); c != null; c = c.parent(), i++) {
            helper.put(c.code(), i);
            if (c.getMergeParent() != null) {
                helper.put(c.getMergeParent(), -1);
            }
        }
        HashMap<String, Integer> ancestors= new HashMap<>();
        int j = 0;
        for (Commit c = getCommit(second); c != null; c = c.parent(), j++) {
            if (helper.containsKey(c.getMergeParent())) {
                String id = c.getMergeParent();
                ancestors.put(id, helper.get(id));
            } else if (helper.containsKey(c.code())) {
                ancestors.put(c.code(), j);
            }
            if (ancestors.size() == 2) {
                break;
            }
        }
        Integer index = Integer.MAX_VALUE;
        String result = null;
        for (String id : ancestors.keySet()) {
            if (ancestors.get(id) < index) {
                result = id;
                index = ancestors.get(id);
            }
        }
        return result;
    }

    /** All commits in this gitlet directory. **/
    private HashSet<String> _commits;

    /** Commit id of the head of this gitlet directory. **/
    private String _head;

    /** Pointers to all existing branches. **/
    private HashMap<String, Branch> _branches = new HashMap<>();

    /** The current staging area. **/
    private Stage _staging;

    /** File names of untracked files. **/
    private HashSet<String> _untracked;

    private HashSet<String> _removed = new HashSet<>();

}
