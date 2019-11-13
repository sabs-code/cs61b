package tablut;


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
        assertEquals(Piece.WHITE, b.turn());
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
        assertTrue(Square.exists(0, 0));
    }

    @Test
    public void testundo() {
        Board b = new Board();
        b.makeMove(Move.mv("d1-2"));
        b.makeMove(Move.mv("e6-f"));
        b.makeMove(Move.mv("a6-e"));
        b.undo();
        b.undo();
        b.undo();
        assertEquals(0, b.moveCount());
        assertEquals(null, b.winner());
        assertTrue(b.isLegal(Move.mv("d1-2")));
    }

}


