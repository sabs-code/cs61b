package tablut;

import net.sf.saxon.functions.Empty;

import javax.naming.InitialContext;
import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author Sabrina Xia
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        for (int i = 0; i < NUM_SQUARES; i ++) {
            Piece mp = model.get(sq(i));
            _position.put(sq(i), mp);
        }
        _moveCount = model.moveCount();
        _turn = model.turn();
        _winner = model.winner();
        _repeated = model._repeated;
        _pastPositions = model._pastPositions;
    }

    /** Clears the board to the initial position. */
    void init() {
        _position = new HashMap<>();
        for (int i = 0; i < NUM_SQUARES; i++) {
            _position.put(sq(i), EMPTY);
        }
        for (int i = 0; i < INITIAL_ATTACKERS.length; i++) {
            _position.put(INITIAL_ATTACKERS[i], BLACK);
        }
        for (int i = 0; i < INITIAL_DEFENDERS.length; i++) {
            _position.put(INITIAL_DEFENDERS[i], WHITE);
        }
        _position.put(sq(4, 4), KING);
        _moveCount = 0;
        _turn = BLACK;
        _winner = null;
        _repeated = false;
        recordPosition();
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount(). */
    void setMoveLimit(int n) {
        if (2 * n <= moveCount()) {
            throw Utils.error("movecount already reached");
        }
        _movelimit = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position. */
    void recordPosition() {
        HashMap<Square, Piece> curr = new HashMap<>();
        for (int i = 0; i < NUM_SQUARES; i++) {
            curr.put(sq(i), get(sq(i)));
        }
        _pastPositions.add(curr);
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        if (_pastPositions.contains(_position)) {
            _winner = _turn.opponent();
            _repeated = true;
        }
        recordPosition();
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < NUM_SQUARES; i++) {
            if (get(sq(i)) == KING) {
                return sq(i);
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _position.get(sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _position.replace(s, p);
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        put(p, s);
        recordPosition();
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        if (!from.isRookMove(to)) {
            return false;
        } else {
            int dir = from.direction(to);
            if (dir == 0) {
                for (int i = to.row(); i > from.row(); i--) {
                    if (get(sq(from.col(), i)) != EMPTY) {
                        return false;
                    }
                }
            } else if (dir == 1) {
                for (int i = to.col(); i > from.col(); i--) {
                    if (get(sq(i, from.row())) != EMPTY) {
                        return false;
                    }
                }
            } else if (dir == 2) {
                for (int i = to.row(); i < from.row(); i++) {
                    if (get(sq(from.col(), i)) != EMPTY) {
                        return false;
                    }
                }
            } else {
                for (int i = to.col(); i < from.col(); i++) {
                    if (get(sq(i, from.row())) != EMPTY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (!isLegal(from)) {
            return false;
        } else if (!isUnblockedMove(from, to)) {
            return false;
        } else return to != THRONE || get(from) == KING;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        put(get(from), to);
        put(EMPTY, from);
        if (exists(to.col(), to.row() + 2)) {
            Square s1 = sq(to.col(), to.row() + 2);
            if (get(s1).opponent() == get(to).opponent() || s1 == THRONE) {
                capture(to, s1);
            }
        }
        if (exists(to.col(), to.row() - 2)) {
            Square s2 = sq(to.col(), to.row() - 2);
            if (get(s2).opponent() == get(to).opponent() || s2 == THRONE) {
                capture(to, s2);
            }
        }
        if (exists(to.col() + 2, to.row())) {
            Square s3 = sq(to.col() + 2, to.row());
            if (get(s3).opponent() == get(to).opponent() || s3 == THRONE) {
                capture(to, s3);
            }
        }
        if (exists(to.col() - 2, to.row())) {
            Square s4 = sq(to.col() - 2, to.row());
            if (get(s4).opponent() == get(to).opponent() || s4 == THRONE) {
                capture(to, s4);
            }
        }
        checkRepeated();
        if (!_repeated) {
            if (kingPosition() == null) {
                _winner = BLACK;
            } else if (kingPosition().isEdge()) {
                _winner = WHITE;
            }
        }
        _moveCount += 1;
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        assert get(sq0) != EMPTY;
        Piece p0 = get(sq0);
        Piece p2 = get(sq2);
        Square sq1 = sq0.between(sq2);
        Piece p1 = get(sq1);
        if (p1 != EMPTY) {
            if (sq1 == NTHRONE || sq1 == WTHRONE || sq1 == ETHRONE
                    || sq1 == STHRONE) {
                if (p0.side() != p1.side()) {
                    if (p1 != KING) {
                        if (sq0 == THRONE) {
                            if (p0 == EMPTY) {
                                put(EMPTY, sq1);
                            } else if (p1 == BLACK) {
                                put(EMPTY, sq1);
                            } else {
                                Square dia1 = sq1.diag1(sq0);
                                Square dia2 = sq1.diag2(sq0);
                                Piece d1 = get(dia1);
                                Piece d2 = get(dia2);
                                if (d1 == BLACK && d2 == BLACK) {
                                    if (get(dia1.diag1(sq0)) == BLACK
                                            || get(dia1.diag2(sq0)) == BLACK) {
                                        put(EMPTY, sq1);
                                    }
                                }
                            }
                        } else {
                            if (p0.side() == p2.side()) {
                                put(EMPTY, sq1);
                            }
                        }
                    } else {
                        Square dia1 = sq0.diag1(sq1);
                        Square dia2 = sq0.diag2(sq1);
                        Piece d1 = get(dia1);
                        Piece d2 = get(dia2);
                        if (d1 != EMPTY && d2 != EMPTY) {
                            if (d1.side() != p1.side()
                                    && d2.side() != p1.side()) {
                                if (p2 == EMPTY) {
                                    put(EMPTY, sq1);
                                }
                            }
                        }
                    }
                }
            } else if (sq1 == THRONE) {
                if (p0 == BLACK && p2 == BLACK) {
                    Square d1 = sq0.diag1(THRONE);
                    Square d2 = sq0.diag2(THRONE);
                    Piece pd1 = get(d1);
                    Piece pd2 = get(d2);
                    if (pd1 == BLACK && pd2 == BLACK) {
                        put(EMPTY, sq1);
                        _winner = BLACK;
                    }
                }
            } else {
                if (p0.side() != p1.side()) {
                    put(EMPTY, sq1);
                }
            }
        }

    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            HashMap<Square, Piece> last;
            last = _pastPositions.get(_pastPositions.size() - 1);
            _position.putAll(last);
            _turn = _turn.opponent();
            _winner = null;
            _moveCount -= 1;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (_moveCount > 0) {
            _pastPositions.remove(_pastPositions.size() -1);
        }
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _pastPositions.clear();
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        assert side != EMPTY;
        ArrayList<Move> moves = new ArrayList<>();
        HashSet<Square> pieces = pieceLocations(side);
        for (Square s : pieces) {
            SqList[] sl = ROOK_SQUARES[s.index()];
            for (SqList l : sl) {
                for (Square sq : l) {
                    if (isUnblockedMove(s, sq) && (sq != THRONE || get(s) == KING)) {
                        moves.add(mv(s, sq));
                    }
                }
            }
        }
        return moves;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return legalMoves(side).size() > 0;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> result = new HashSet<>();
        for (int i = 0; i < NUM_SQUARES; i++) {
            if (get(sq(i)).side() == side) {
                result.add(sq(i));
            }
        }
        return result;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;

    /** Positions of all pieces on the board. */
    private HashMap<Square, Piece> _position;

    /** My movelimit. */
    private int _movelimit;

    /** All my past positions. */
    private ArrayList<HashMap<Square, Piece>> _pastPositions = new ArrayList<>();


}
