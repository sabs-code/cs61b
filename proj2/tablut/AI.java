package tablut;

import java.util.List;

import static java.lang.Math.*;

import static tablut.Square.NUM_SQUARES;
import static tablut.Square.sq;
import static tablut.Board.THRONE;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Sabrina Xia
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move m = findMove();
        _controller.reportMove(m);
        String s;
        if (m.from().col() == m.to().col()) {
            s = String.format("%s-%c", m.from(), (char) m.to().row() + '1');
        } else {
            s = String.format("%s-%c", m.from(), (char) m.to().col() + 'a');
        }
        return s;
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (_myPiece == BLACK) {
            findMove(b, maxDepth(b), true, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            findMove(b, maxDepth(b), true, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        } else {
            int value;
            if (sense == 1) {
                List<Move> legalMoves = board.legalMoves(WHITE);
                if (legalMoves.size() == 0) {
                    return staticScore(board);
                }
                for (Move m : legalMoves) {
                    board.makeMove(m);
                    value = findMove(board, depth - 1, false, -1,
                            alpha, beta);
                    board.undo();
                    if (value > alpha) {
                        if (saveMove) {
                            _lastFoundMove = m;
                        }
                        alpha = value;
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
                return alpha;
            } else {
                List<Move> legalMoves = board.legalMoves(BLACK);
                if (legalMoves.size() == 0) {
                    return staticScore(board);
                }
                for (Move m : legalMoves) {
                    board.makeMove(m);
                    value = findMove(board, depth - 1, false, 1,
                            alpha, beta);
                    board.undo();
                    if (value < beta) {
                        if (saveMove) {
                            _lastFoundMove = m;
                        }
                        beta = value;
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
                return beta;
            }
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        return 4;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        if (board.winner() == WHITE) {
            return WINNING_VALUE;
        } else if (board.winner() == BLACK) {
            return -1 * WINNING_VALUE;
        } else if (!board.hasMove(BLACK)) {
            return WINNING_VALUE;
        } else if (!board.hasMove(WHITE)) {
            return -1 * WINNING_VALUE;
        }
        else {
            Square kingSq = board.kingPosition();
            int kScore;
            kScore = (int) Math.pow(kingSq.col() - 4, 2) + (int) Math.pow(kingSq.row() - 4, 2);
            if (emptyCol(board, kingSq) || emptyRow(board, kingSq)) {
                kScore *= 5;
            } else if (halfEmptyCol(board, kingSq) || halfEmptyRow(board, kingSq)) {
                kScore *= 3;
            }
            int white = 0;
            int black = 0;
            for (int i = 0; i < NUM_SQUARES; i++) {
                if (board.get(sq(i)) == WHITE) {
                    white += 1;
                } else if (board.get(sq(i)) == BLACK) {
                    black += 1;
                }
            }
            return kScore + white - black;
        }
    }

    /** Return true if SQ is the only occupied square in its row.*/
    boolean emptyCol(Board b, Square sq) {
        for (int i = 0; i < 9; i ++) {
            if (i == sq.row()) {
                continue;
            } else {
                if (b.get(sq(sq.col(), i)) != EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Return true if SQ is the only occupied square in its col.*/
    boolean emptyRow(Board b, Square sq) {
        for (int i = 0; i < 9; i ++) {
            if (i == sq.col()) {
                continue;
            } else {
                if (b.get(sq(i, sq.row())) != EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Return true if one side of SQ's col is empty.*/
    boolean halfEmptyCol(Board b, Square sq) {
        boolean side1, side2;
        side1 = side2 = true;
        for (int i = 0; i < sq.row(); i++) {
            if (b.get(sq(sq.col(), i)) != EMPTY) {
                side1 = false;
            }
        }
        for (int i = 8; i > sq.row(); i--) {
            if (b.get(sq(sq.col(), i)) != EMPTY) {
                side2= false;
            }
        }
        if (side1 || side2) {
            return true;
        }
        return false;
    }

    /** Return true if one side of SQ's row is empty.*/
    boolean halfEmptyRow(Board b, Square sq) {
        boolean side1, side2;
        side1 = side2 = true;
        for (int i = 0; i < sq.col(); i++) {
            if (b.get(sq(i, sq.row())) != EMPTY) {
                side1 = false;
            }
        }
        for (int i = 8; i > sq.col(); i--) {
            if (b.get(sq(i, sq.row())) != EMPTY) {
                side2 = false;
            }
        }
        if (side1 || side2) {
            return true;
        }
        return false;
    }
}
