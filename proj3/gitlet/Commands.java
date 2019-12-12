package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

/** Gitlet commands.
 * @author Sabrina Xia
 */
public class Commands implements Serializable {
    /** Repo that each command manipulates. */
    private Repo _repo;

    /** Creates a new Commands instance if there isn't one. Otherwise,
     * creates a Commands that has the same repo as before.
     */
    Commands() {
        File command = new File(".gitlet/command");
        if (command.exists()) {
            Commands c = Utils.readObject(command, Commands.class);
            _repo = c._repo;
        } else {
            _repo = new Repo();
        }
    }


    /** Processes the command according to OPERANDS. **/
    public void process(ArrayList<String> operands) throws IOException {
        if (operands.size() == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String s = operands.remove(0);
        if (s.equals("init")) {
            init();
        } else {
            if (!_repo.initialized()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
            if (s.equals("add")) {
                add(operands);
            } else if (s.equals("commit")) {
                commit(operands);
            } else if (s.equals("checkout")) {
                checkout(operands);
            } else if (s.equals("rm")) {
                remove(operands);
            } else if (s.equals("log")) {
                log();
            } else if (s.equals("global-log")) {
                globalLog();
            } else if (s.equals("find")) {
                find(operands);
            } else if (s.equals("status")) {
                _repo.status();
            } else if (s.equals("branch")) {
                branch(operands);
            } else if (s.equals("rm-branch")) {
                rmBranch(operands);
            } else if (s.equals("reset")) {
                reset(operands);
            } else if (s.equals("merge")) {
                merge(operands);
            } else {
                System.out.println("No command with that name exists.");
            }
        }
    }

    /** Initializes a gitlet directory. Errors if there already exists one. **/
    public void init() throws IOException {
        if (Files.exists(Paths.get(".gitlet"))) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
        } else {
            Files.createDirectory(Paths.get(".gitlet"));
            Files.createDirectory(Paths.get(".gitlet/commits"));
            Files.createDirectory(Paths.get(".gitlet/stage"));
            Files.createDirectory(Paths.get(".gitlet/untracked"));
            _repo.init();
        }
    }

    /** List all files in the current working directory and notify Repo. */
    public void checkUntracked() {
        File curr = new File(".");
        HashSet<String> allFiles = allFiles(curr);
        _repo.updateUntracked(allFiles);
    }

    /** Helper method that returns an Hashset of all files in DIR. **/
    public HashSet<String> allFiles(File dir) {
        HashSet<String> files = new HashSet<>();
        File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            for (File f: allFiles) {
                if (f.isDirectory() && !f.isHidden()) {
                    files.addAll(allFiles(f));
                } else if (f.isFile()) {
                    files.add(f.getName());
                }
            }
        }
        return files;
    }

    /** Add a file to the staging area according to OPERANDS. */
    public void add(ArrayList<String> operands) {
        String s = operands.remove(0);
        File f = new File(s);
        if (!f.exists()) {
            System.out.println("File does not exist.");
        } else {
            _repo.add(s);
        }
    }

     /** Create a new Commit according to OPERANDS. **/
    public void commit(ArrayList<String> operands) {
        if (_repo.nochange()) {
            System.out.println("No changes added to the commit.");
        } else if (operands.size() == 0 || operands.get(0).length() == 0) {
            System.out.println("Please enter a commit message.");
        } else {
            _repo.commit(operands);
        }
    }

    /** Untrack the file in OPERANDS. **/
    public void remove(ArrayList<String> operands) {
        String file = operands.remove(0);
        _repo.remove(file);
    }

    /** Make repo print gitlet log. */
    public void log() {
        _repo.log();
    }

    /** Prints all commits in repo. **/
    public void globalLog() {
        _repo.globalLog();
    }

    /** Find all commits that have log message same as OPERANDS
     * in repo.
     */
    public void find(ArrayList<String> operands) {
        _repo.find(operands.get(0));
    }

    /** Checks out file, or file in commit, or branch according to
     * OPERANDS. **/
    public void checkout(ArrayList<String> operands) {
        String s = operands.remove(0);
        if (s.equals("--")) {
            _repo.checkoutFile(operands);
        } else if (operands.size() == 2) {
            if (!operands.get(0).equals("--")) {
                System.out.println("Incorrect operands.");
            } else {
                _repo.checkoutFileinCommit(s, operands.remove(1));
            }
        } else if (operands.size() == 0) {
            _repo.checkoutBranch(s);
        }
    }

    /** Creates a new branch in repo with name according to OPERANDS. **/
    public void branch(ArrayList<String> operands) {
        String branchname = operands.remove(0);
        _repo.createBranch(branchname);
    }

    /** Removes the branch named in OPERANDS in repo. **/
    public void rmBranch(ArrayList<String> operands) {
        String branchname = operands.remove(0);
        _repo.removeBranch(branchname);
    }

    /** Reset the repo/working directory according to OPERANDS. **/
    public void reset(ArrayList<String> operands) {
        String commitid = operands.remove(0);
        _repo.reset(commitid);
    }

    /** Merges the repo's current branch with the branch given
     * in OPERANDS.
     */
    public void merge(ArrayList<String> operands) {
        String branchname = operands.remove(0);
        _repo.merge(branchname);
    }
}

