package tablut;

import java.util.List;

import static java.lang.Math.*;

import static tablut.Piece.*;
import static tablut.Square.*;

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
        return m.toString();
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
            findMove(b, maxDepth(b), true, -1, Integer.MIN_VALUE,
                    Integer.MAX_VALUE);
        } else {
            findMove(b, maxDepth(b), true, 1, Integer.MIN_VALUE,
                    Integer.MAX_VALUE);
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
        } else {
            Square kingSq = board.kingPosition();
            int kScore = 1000;
            kScore *= (int) Math.pow(kingSq.col() - 4, 2)
                    + (int) Math.pow(kingSq.row() - 4, 2) + 1;
            if (emptyCol(board, kingSq) == 0 || emptyRow(board, kingSq) == 0) {
                return WILL_WIN_VALUE;
            } else {
                kScore -= emptyCol(board, kingSq) * 2;
                kScore -= emptyRow(board, kingSq) * 2;
            }
            int white = 0;
            int black = 0;
            int blackScore = 0;
            for (int i = 0; i < NUM_SQUARES; i++) {
                if (board.get(sq(i)) == WHITE) {
                    white += 1;
                } else if (board.get(sq(i)) == BLACK) {
                    black += 1;
                    int k = Math.abs(sq(i).col() - kingSq.col())
                            + Math.abs(sq(i).row() - kingSq.row());
                    blackScore += k;
                }
            }
            return 100 * kScore + 10 * white - 10 * black + 100 * blackScore;
        }
    }

    /** Return number of occupied squares in SQ's col in B.*/
    int emptyCol(Board b, Square sq) {
        int result = 0;
        for (int i = 0; i < 9; i++) {
            if (i == sq.row()) {
                continue;
            } else {
                if (b.get(sq(sq.col(), i)) != EMPTY) {
                    result += 1;
                }
            }
        }
        return result;
    }

    /** Return number of occupied squares in SQ's col in B.*/
    int emptyRow(Board b, Square sq) {
        int result = 0;
        for (int i = 0; i < 9; i++) {
            if (i == sq.col()) {
                continue;
            } else {
                if (b.get(sq(i, sq.row())) != EMPTY) {
                    result += 1;
                }
            }
        }
        return result;
    }
}
