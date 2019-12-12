package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** The repository of this gitlet directory. Includes all information about
 * commit tree, all branches, current branch, staging area, etc.
 * @author Sabrina Xia
 */
public class Repo implements Serializable {
    /** Creates a new repo with only one master branch and no commit. **/
    Repo() {
        _commits = new HashSet<>();
        _branches.put("master", new Branch("master"));
        _staging = new Stage();
    }

    /** Initializes this repo to be ready for gitlet. **/
    public void init() {
        Commit c = new Commit("master", "initial commit");
        addCommit(c);
        _headBranch = _branches.get("master");
        _initialized = true;
    }

    /** Returns true only if this repo has been initialized. **/
    public boolean initialized() {
        return _initialized;
    }

    /** Add C to commit tree and update current branch's head or
     * create new branch. **/
    public void addCommit(Commit c) {
        String s = c.code();
        _commits.add(s);
        File f = new File(".gitlet/commits/" + s);
        Utils.writeObject(f, c);
        if (_branches.containsKey(c.branch())) {
            Branch b = _branches.get(c.branch());
            b.updatehead(c.code());
        } else {
            _branches.put(c.branch(), new Branch(c.branch(), c.code()));
        }
    }

    /** Returns the head commit of this repo. **/
    public Commit head() {
        return getCommit(_headBranch.head());
    }

    /** Returns the commit with sha-1 code ID. **/
    public Commit getCommit(String id) {
        File commitFile = new File(".gitlet/commits/" + id);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }

    /** Called everytime there is a new gitlet command. Update all untracked
     * files by going through FILENAMES and checking if each one is tracked in
     * the current branch.
     */
    public void updateUntracked(HashSet<String> fileNames) {
        _untracked = new HashSet<>();
        HashSet<String> helper1 = new HashSet<>(fileNames);
        if (fileNames.size() != 0) {
            for (String name : fileNames) {
                if (_headBranch.tracked(name)) {
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
        _untracked.addAll(helper2);
    }

    /** Returns true if the staging area is empty and no file was removed. **/
    public boolean nochange() {
        return _removed.isEmpty() && _staging.isEmpty();
    }

    /** Adds file with filename S to the staging area. Add or update(overwrite)
     * according to the contents of this file.
     */
    public void add(String s) {
        Commit curr = head();
        if (curr.getBlobs().containsKey(s)) {
            Blob b = curr.getBlobs().get(s);
            byte[] commited = (byte[]) b.getContents();
            File f = new File(s);
            byte[] currContents = Utils.readContents(f);
            if (_staging.contains(s)) {
                if (Arrays.equals(commited, currContents)) {
                    _staging.remove(s);
                } else {
                    _staging.rewrite(s);
                }
            } else {
                if (!Arrays.equals(commited, currContents)) {
                    _staging.add(s);
                }
            }
        } else {
            if (_staging.contains(s)) {
                _staging.rewrite(s);
            } else {
                _staging.add(s);
            }
        }
        _removed.remove(s);
        _untracked.remove(s);
    }

    /** Makes a new commit with log message according to OPERANDS. **/
    public void commit(ArrayList<String> operands) {
        String log = operands.remove(0);
        Commit c = new Commit(_headBranch.name(), log, head(), _staging,
                _removed);
        addCommit(c);
        _removed.clear();
        _staging.clear();
    }

    /** Removes the file named FILENAME from repo. Remove from staging area if
     * currently staged, mark it to be untracked in next commit by adding
     * FILENAME to _removed if it is tracked by current commit.
     */
    public void remove(String filename) {
        Commit headCommit = head();
        if (!headCommit.getBlobs().containsKey(filename)
                && !_staging.contains(filename)) {
            System.out.println("No reason to remove the file.");
        } else if (_staging.contains(filename)) {
            _staging.remove(filename);
        }
        if (headCommit.getBlobs().containsKey(filename)) {
            _removed.add(filename);
            File file = new File(filename);
            file.delete();
        }
    }

    /** Prints information about each commit starting with current one,
     * and going backwards following each one's parent. **/
    public void log() {
        for (Commit c = head(); c != null; c = c.parent()) {
            System.out.println("===");
            System.out.println("commit " + c.code());
            if (c.getMergeParent() != null) {
                String s = "Merge: ";
                s += c.parent().code().substring(0, 7);
                s += " ";
                s += c.getMergeParent().substring(0, 7);
                System.out.println(s);
            }
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getLogMessage());
            System.out.println();
        }
    }

    /** Prints information for all commits. Order does not matter. **/
    public void globalLog() {
        for (String commitid : _commits) {
            Commit c = getCommit(commitid);
            System.out.println("===");
            System.out.println("commit " + c.code());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getLogMessage());
            System.out.println();
        }
    }

    /** Finds and prints all commit ids that have logmessage same as
     * LOGMESSAGE.
     */
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

    /** Prints the current status of this repo. Prints all existing branches,
     * all staged files, and all removed files since last commit.
     */
    public void status() {
        System.out.println("=== Branches ===");
        ArrayList<String> helper = new ArrayList<>(_branches.keySet());
        Collections.sort(helper);
        for (String branch : helper) {
            if (branch.equals(head().branch())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        helper.clear();
        helper.addAll(_staging.getAll().keySet());
        ArrayList<String> helper1 = new ArrayList<>(helper);
        ArrayList<String> modified = new ArrayList<>();
        ArrayList<String> deleted = new ArrayList<>();
        for (String file : helper1) {
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
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        helper.clear();
        helper.addAll(_removed);
        Collections.sort(helper);
        for (String s : helper) {
            System.out.println(s);
        }
        System.out.println();
        helperStatus(modified, deleted);
    }

    /** Helper method that prints the current status of this repo.
     * Prints all files tracked but modified in working directory after
     * updating MODIFIED and DELETED. Prints all untracked files in current
     * branch.
     */
    public void helperStatus(ArrayList<String> modified,
                             ArrayList<String> deleted) {
        ArrayList<String> helper = new ArrayList<>();
        System.out.println("=== Modifications Not Staged For Commit ===");
        HashMap<String, Blob> headBlobs = head().getBlobs();
        for (String file : headBlobs.keySet()) {
            File curr = new File(file);
            if (!curr.exists()) {
                if (!deleted.contains(file)) {
                    deleted.add(file);
                    continue;
                }
            }
            byte[] currContents = Utils.readContents(curr);
            byte[] commited = (byte[]) headBlobs.get(file).getContents();
            if (!Arrays.equals(currContents, commited)) {
                if (!modified.contains(file)) {
                    modified.add(file);
                }
            }
        }
        helper.addAll(modified);
        helper.addAll(deleted);
        Collections.sort(helper);
        for (String file : helper) {
            if (modified.contains(file)) {
                System.out.println(file + " (modified)");
            } else if (!_removed.contains(file)) {
                System.out.println(file + " (deleted)");
            }
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        helper.clear();
        helper.addAll(_untracked);
        Collections.sort(helper);
        for (String s : helper) {
            System.out.println(s);
        }
    }

    /** Updates the file named OPERANDS in working directory according to
     * contents in the current commit.
     */
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

    /** Updates the file named FILENAME in working directory according to
     * its contents in the commit with sha-1 code COMMITID.
     */
    public void checkoutFileinCommit(String commitid, String filename) {
        if (commitid.length() < Utils.UID_LENGTH) {
            for (String s : _commits) {
                if (s.substring(0, commitid.length()).equals(commitid)) {
                    commitid = s;
                    break;
                }
            }
        }
        if (!_commits.contains(commitid)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
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

    /** Updates all files in working directory according to contents in
     * COMMIT. */
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
        HashSet<String> trackedFiles = _headBranch.tracked();
        for (String filename : trackedFiles) {
            if (!blobsInCommit.containsKey(filename)) {
                File f = new File(filename);
                f.delete();
            }
        }
        _staging.clear();
    }

    /** Updates all files in working directory according to branch BRANCHNAME's
     * head commit.
     */
    public void checkoutBranch(String branchName) {
        if (!_branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else if (_headBranch.name().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
        } else {
            String commitid = _branches.get(branchName).head();
            Commit commit = getCommit(commitid);
            checkoutCommit(commit);
            _headBranch = _branches.get(branchName);
        }
    }

    /** Creates a new branch named BRANCHNAME with head commit being the
     * current commit.
     */
    public void createBranch(String branchname) {
        if (_branches.containsKey(branchname)) {
            System.out.println("A branch with that name already exists.");
        } else {
            Branch b = new Branch(branchname, _headBranch.head());
            _branches.put(branchname, b);
        }
    }

    /** Removes the branch named BRANCHNAME but keeps all commits in this
     * branch.
     */
    public void removeBranch(String branchname) {
        if (!_branches.containsKey(branchname)) {
            System.out.println("A branch with that name does not exist.");
        } else if (head().branch().equals(branchname)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _branches.remove(branchname);
        }
    }

    /** Reset (updates) all files in working directory according to commit
     * with sha-1 code COMMITID.
     */
    public void reset(String commitid) {
        if (!_commits.contains(commitid)) {
            System.out.println("No commit with that id exists.");
        } else {
            if (commitid.length() < Utils.UID_LENGTH) {
                for (String s : _commits) {
                    if (s.substring(0, commitid.length()).equals(commitid)) {
                        commitid = s;
                        break;
                    }
                }
            }
            Commit commit = getCommit(commitid);
            checkoutCommit(commit);
            _headBranch.updatehead(commitid);
        }
    }

    /** Helper method for merge that checks if merging with BRANCHNAME
     * is invalid.
     */
    public void helperMerge(String branchname) {
        if (!nochange()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!_branches.containsKey(branchname)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (_headBranch.name().equals(branchname)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /** Merges current branch with branch named BRANCHNAME. **/
    public void merge(String branchname) {
        helperMerge(branchname);
        boolean conflict = false;
        Branch target = _branches.get(branchname);
        String splitPoint = findSplitPoint(_headBranch.head(), target.head());
        if (splitPoint.equals(target.head())) {
            System.out.println("Given branch is an ancestor of the "
                    + "current branch.");
            System.exit(0);
        } else if (splitPoint.equals(_headBranch.head())) {
            _headBranch.updatehead(target.head());
            checkoutCommit(head());
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        } else {
            Commit spCommit = getCommit(splitPoint);
            Commit branch = getCommit(target.head());
            HashMap<String, String> compare = helperCompare(spCommit, branch);
            for (String filename : compare.keySet()) {
                if (compare.get(filename).equals("rm")) {
                    remove(filename);
                } else if (compare.get(filename).equals("checkout stage")) {
                    File curr = new File(filename);
                    byte[] overwrite = (byte[]) branch.getBlobs().
                            get(filename).getContents();
                    Utils.writeContents(curr, (Object) overwrite);
                    add(filename);
                } else if (compare.get(filename).equals("conflict")) {
                    conflict = true;
                    String newContent = "<<<<<<< HEAD\n";
                    File curr = new File(filename);
                    if (curr.exists()) {
                        newContent += Utils.readContentsAsString(curr);
                    }
                    newContent += "=======\n";
                    if (branch.getBlobs().containsKey(filename)) {
                        newContent += branch.getBlobs().get(filename).
                                getContentAsString();
                    }
                    newContent += ">>>>>>>\n";
                    Utils.writeContents(curr, newContent);
                    add(filename);
                }
            }
        }
        String log = "Merged " + branchname + " into "
                + _headBranch.name() + ".";
        Commit mergeParent = getCommit(target.head());
        Commit c = new Commit(_headBranch.name(), log, head(), mergeParent,
                _staging, _removed);
        addCommit(c);
        _removed.clear();
        _staging.clear();
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Helper method that compares all tracked files in splitpoint commit SP,
     * current commit, and given commit BRANCH.
     * @return comparision result of each file in hashmap
     */
    public HashMap<String, String> helperCompare(Commit sp, Commit branch) {
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, Blob> spBlobs = sp.getBlobs();
        HashMap<String, Blob> branchBlobs = branch.getBlobs();
        for (String filename : spBlobs.keySet()) {
            File curr = new File(filename);
            if (!branchBlobs.containsKey(filename)) {
                if (curr.exists()) {
                    byte[] currContents = Utils.readContents(curr);
                    byte[] spContents = (byte[]) spBlobs.get(filename)
                            .getContents();
                    if (Arrays.equals(currContents, spContents)) {
                        result.put(filename, "rm");
                    } else {
                        result.put(filename, "conflict");
                    }
                    checkUntracked(filename);
                } else {
                    result.put(filename, "same");
                }
            } else {
                helperCompare1(result, curr, filename, branchBlobs, spBlobs);
            }
        }
        for (String filename : branchBlobs.keySet()) {
            if (!spBlobs.containsKey(filename)) {
                File curr = new File(filename);
                if (!curr.exists()) {
                    result.put(filename, "checkout stage");
                    checkUntracked(filename);
                } else {
                    byte[] currContents = Utils.readContents(curr);
                    byte[] branchContents = (byte[]) branchBlobs.get(filename)
                            .getContents();
                    if (!Arrays.equals(currContents, branchContents)) {
                        result.put(filename, "conflict");
                        checkUntracked(filename);
                    }
                }
            }
        }
        return result;
    }

    /** Helper method that continues helperCompare. Updates RESULT by comparing
     * CURR named FILENAME with file named FILENAME in BRANCHBLOBS and SPBLOBS.
     */
    public void helperCompare1(HashMap<String, String> result, File curr,
                               String filename, HashMap<String, Blob>
                                       branchBlobs,
                               HashMap<String, Blob> spBlobs) {
        if (!curr.exists()) {
            byte[] branchContents = (byte[]) branchBlobs.get(filename).
                    getContents();
            byte[] spContents = (byte[]) spBlobs.get(filename).getContents();
            if (Arrays.equals(branchContents, spContents)) {
                result.put(filename, "same");
            } else {
                result.put(filename, "conflict");
                checkUntracked(filename);
            }
        } else {
            byte[] currContents = Utils.readContents(curr);
            byte[] branchContents = (byte[]) branchBlobs.get(filename).
                    getContents();
            byte[] spContents = (byte[]) spBlobs.get(filename).getContents();
            if (!Arrays.equals(branchContents, spContents)
                    && Arrays.equals(spContents, currContents)) {
                result.put(filename, "checkout stage");
                checkUntracked(filename);
            } else if (Arrays.equals(branchContents, spContents)
                    && !Arrays.equals(currContents, spContents)) {
                result.put(filename, "same");
            } else if (Arrays.equals(currContents, branchContents)) {
                result.put(filename, "same");
            } else if (!Arrays.equals(currContents, spContents)
                    && !Arrays.equals(branchContents, spContents)
                    && !Arrays.equals(currContents, branchContents)) {
                result.put(filename, "conflict");
                checkUntracked(filename);
            }
        }
    }

    /** Helper method that checks if file named FILENAME is untracked. **/
    public void checkUntracked(String filename) {
        if (_untracked.contains(filename)) {
            System.out.println("There is an untracked file in "
                    + "the way; delete it or add it first.");
            System.exit(0);
        }
    }

    /** Finds and returns the splitpoint commit of FIRST and SECOND. **/
    public String findSplitPoint(String first, String second) {
        HashMap<String, Integer> helper = new HashMap<>();
        int i = 0;
        for (Commit c = getCommit(first); c != null; c = c.parent(), i++) {
            helper.put(c.code(), i);
            if (c.getMergeParent() != null) {
                helper.put(c.getMergeParent(), -1);
            }
        }
        HashMap<String, Integer> ancestors = new HashMap<>();
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

    /** The current branch of this repo. **/
    private Branch _headBranch;

    /** Pointers to all existing branches. **/
    private HashMap<String, Branch> _branches = new HashMap<>();

    /** The current staging area. **/
    private Stage _staging;

    /** File names of untracked files, updated each time there is a
     * gitlet command. **/
    private HashSet<String> _untracked;

    /** Removed files since last commit. Will be cleared after each new
     * commit.
     */
    private HashSet<String> _removed = new HashSet<>();

    /** Whether if this repo is initialized. **/
    private boolean _initialized = false;

}
