package enigma;

import com.sun.xml.internal.xsom.impl.scd.Iterators;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Sabrina Xia
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        String settings = settings(m);
        setUp(m, settings);
        String encrypted = "";
        _input.useDelimiter("\\s");
        while (_input.hasNext()) {
            String converted = m.convert(_input.next());
            encrypted += converted;
        }
        printMessageLine(encrypted);
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.nextLine();
            _alphabet = new Alphabet(alpha);
            int numRotors = _config.nextInt();
            _config.skip("\\s");
            int numPawls = _config.nextInt();
            ArrayList<Rotor> rotors = new ArrayList<Rotor>();
            while (_config.hasNextLine()) {
                rotors.add(readRotor());
            }
            Machine m = new Machine(_alphabet, numRotors, numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = "";
            while (!_config.hasNext("\\s")) {
                name += _config.next();
            }
            _config.skip("\\s");
            String type = _config.next();
            String notch = "";
            while (_config.hasNext("^\\s")) {
                notch += _config.next();
            }
            _config.skip("\\s");
            String cycles = _config.nextLine();
            if (!_config.hasNext("[A-Z]")) {
                cycles += _config.nextLine();
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            if (type.equals("M")) {
                return new MovingRotor(name, perm, notch);
            }
            else if (type.equals("N")) {
                return new FixedRotor(name, perm);
            } else {
                return new Reflector(name, perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Insert rotors & plugboard specified in input file and return settings. */
    public String settings(Machine m) {
        if (!_input.hasNext("\\*")) {
            throw new EnigmaException("input wrong format");
        }
        _input.next();
        String[] rotors = new String[m.numRotors()];
        for(int i = 0; i < m.numRotors(); i++) {
            String rotorName = "";
            _input.skip("\\s");
            while (_input.hasNext("[a-zA-Z]")) {
                rotorName += _input.next();
            }
            rotors[i] = rotorName;
        }
        m.insertRotors(rotors);
        _input.skip("\\s");
        String next = "";
        if (!_input.hasNext("[A-Z]")) {
            throw new EnigmaException("input wrong format");
        }
        while (!_input.hasNext("\\s")) {
            next += _input.next();
        }
        String plugboardCycle = _input.nextLine();
        plugboardCycle.trim();
        if (plugboardCycle.length() > 0) {
            if (plugboardCycle.startsWith("(")) {
                Permutation plugboard = new Permutation(plugboardCycle, _alphabet);
                m.setPlugboard(plugboard);
            } else {
                throw new EnigmaException("input wrong format");
            }
        }
        return next;
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String encrypted = "";
        for (int i = 0; i < msg.length(); i++) {
            encrypted += msg.charAt(i);
            if (encrypted.length() == 5) {
                _output.print(encrypted);
                encrypted = "";
            }
        }
        if (encrypted.length() > 0) {
            _output.print(encrypted);
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
