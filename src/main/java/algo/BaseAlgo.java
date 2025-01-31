package algo;

import board.Board;

import java.util.List;

public interface BaseAlgo {
    /**
     * Returns the next move. The move must be legal.
     * Assume that the given board is not a terminal board.
     * @param board The board to return the move on.
     */
    byte nextMove(Board board);

    /**
     * Returns the analysis of current board,
     * which can be printed on the terminal for algo debugging.
     * The trace must from the last call to {@code nextMove}.
     */
    String trace();

    /**
     * Returns the next move, but with time constraint.
     * See {@code nextMove}.
     * @param time The number of milliseconds allowed.
     */
    byte nextMoveWithTime(Board board, int time);

    /**
     * Returns the prediction on the next moves from this move,
     * based on the last search.
     * Includes the next move to be made by this algo.
     */
    List<Byte> getMovePredictions();

    /**
     * Thinks while the opponent is making a move.
     * The agent must be able to handle the states itself.
     */
    void ponder();

    /**
     * Stops pondering.
     * This allows graceful exit of the pondering process.
     * This method must be called only when {@code ponder} has been called.
     */
    void stopPondering();
}
