package tablut;

import afu.org.checkerframework.common.value.qual.StaticallyExecutable;
import afu.org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Test makemove. */
    @Test
    public void testMakemove() {
        Board b = new Board();
        assertTrue(b.isLegal(Square.sq("a", "4")));
        b.makeMove(Move.mv("a4-c"));
        assertEquals(Piece.EMPTY, b.get(0, 3));
        assertEquals(Piece.BLACK, b.get(2, 3));
        assertEquals(Piece.WHITE, b.turn());;
    }

    @Test
    public void testMove() {
        Board b = new Board();
        b.makeMove(Move.mv("h5-6"));
        assertEquals(Piece.BLACK, b.get(7, 5));
    }

    /** Tests if Board can distinguish legal/illegal moves. */
    @Test
    public void testLegal() {
        Board b = new Board();
        assertTrue(b.isLegal(Move.mv("a4-d")));
        assertFalse(b.isLegal(Move.mv("a4-f")));
        assertTrue(b.isLegal(Move.mv("h5-6")));
    }

    @Test
    public void testlegalMoves() {
        Board b = new Board();
        assertEquals(10, b.legalMoves(Piece.BLACK).size());
    }
}


