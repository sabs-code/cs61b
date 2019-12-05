package gitlet;

import net.sf.saxon.trans.SymbolicName;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
            _repo.init();
        }
    }

    /** Add a file to the staging area. */
    public void add(ArrayList<String> operands) {
        String s = operands.remove(0);
        File f = new File(s);
        if (!f.exists()) {
            System.out.println("File does not exist.");
        } else {
            _repo.add(s);
        }
    }

     /** Create a new Commit. **/
    public void commit(ArrayList<String> operands) {
        if (_repo.getStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
        } else if (operands.size() == 0) {
            System.out.println("Please enter a commit message.");
        } else {
            _repo.commit(operands);
        }
    }

    public void checkout(ArrayList<String> operands) {
        String s = operands.remove(0);
        if (s.equals("--")) {
            _repo.checkoutFile(operands);
        }
    }
}
