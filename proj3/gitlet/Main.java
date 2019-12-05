package gitlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Sabrina Xia
 */
public class Main {
    private static Commands _commands = new Commands();

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        ArrayList<String> operands = new ArrayList<>(Arrays.asList(args));
        try {
            _commands.process(operands);
        } catch (IllegalArgumentException | IOException e ) {
            System.out.println(e.getMessage());
        }
    }

}
