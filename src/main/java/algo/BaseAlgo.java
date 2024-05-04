package algo;

import board.Board;
import board.Move;

public interface BaseAlgo {
    /**
     * Returns the next move. The move must be legal.
     * Assume that the given board is not a terminal board.
     * @param board The board to return the move on.
     */
    Move nextMove(Board board);

    /**
     * Returns the analysis of current board,
     * which can be printed on the terminal for algo debugging.
     * The trace must from the last call to {@code nextMove}.
     */
    String trace();

    /**
     * Returns the next move, but with time constraint.
     * See {@code nextMove}.
     * @param time The number of seconds allowed.
     */
    Move nextMoveWithTime(Board board, int time);
}
