package algo.random;

import algo.BaseAlgo;
import board.Board;
import board.Move;

import java.util.List;
import java.util.Random;

public class RandomAlgo implements BaseAlgo {
    private final Random rng = new Random();

    @Override
    public Move nextMove(Board board) {
        List<Move> actions = board.actions();
        int nextInt = this.rng.nextInt(actions.size());
        return actions.get(nextInt);
    }

    @Override
    public String trace() {
        return "No trace to print";
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        return this.nextMove(board);
    }
}
