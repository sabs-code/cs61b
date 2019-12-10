package gitlet;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import net.sf.saxon.trans.SymbolicName;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.DoublePredicate;

/** Gitlet commands.
 * @author Sabrina Xia
 */
public class Commands implements Serializable{

    private Repo _repo;

    Commands() {
        File command = new File(".gitlet/command");
        if (command.exists()) {
            Commands c = Utils.readObject(command, Commands.class);
            _repo = c._repo;
        } else {
            _repo = new Repo();
        }
    }


    /** Processes the command according to operands. **/
    public void process(ArrayList<String> operands) throws IOException {
        String s = operands.remove(0);
        if (s.equals("init")) {
            init();
        } else if (s.equals("add")) {
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
        }
    }

    /** Initializes a gitlet directory. Errors if there already exists one. **/
    public void init() throws IOException {
        if (Files.exists(Paths.get(".gitlet"))) {
            System.out.println("A Gitlet version-control system already " +
                    "exists in the current directory.");
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

    /** Helper method that returns an Hashset of all files in Dir. **/
    public HashSet<String> allFiles(File dir) {
        HashSet<String> files = new HashSet<>();
        File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            for (File f: allFiles) {
                if (f.isDirectory()) {
                    files.addAll(allFiles(f));
                } else if (f.isFile()) {
                    files.add(f.getName());
                }
            }
        }
        return files;
    }

    /** Add a file to the staging area according to operands. */
    public void add(ArrayList<String> operands) {
        String s = operands.remove(0);
        File f = new File(s);
        if (!f.exists()) {
            System.out.println("File does not exist.");
        } else {
            _repo.add(s);
        }
    }

     /** Create a new Commit according to operands. **/
    public void commit(ArrayList<String> operands) {
        if (_repo.getStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
        } else if (operands.size() == 0) {
            System.out.println("Please enter a commit message.");
        } else {
            _repo.commit(operands);
        }
    }

    /** Untrack the file in operands. **/
    public void remove(ArrayList<String> operands) {
        try {
            String file = operands.remove(0);
            _repo.remove(file);
        } catch (NullPointerException ignored) {
        }
    }

    /** Make repo print gitlet log. */
    public void log() {
        _repo.log();
    }

    public void globalLog() {
        _repo.globalLog();
    }

    public void find(ArrayList<String> operands) {
        _repo.find(operands.get(0));
    }

    public void checkout(ArrayList<String> operands) {
        String s = operands.remove(0);
        if (s.equals("--")) {
            _repo.checkoutFile(operands);
        } else if (operands.size() == 2) {
            if (!_repo.hasCommit(s)) {
                System.out.println("No commit with that id exists.");
            } else {
                _repo.checkoutFileinCommit(s, operands.remove(1));
            }
        } else if (operands.size() == 0) {
            _repo.checkoutBranch(s);
        }
    }
}
