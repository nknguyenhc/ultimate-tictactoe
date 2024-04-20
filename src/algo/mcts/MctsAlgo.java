package algo.mcts;

import algo.BaseAlgo;
import board.Board;
import board.Move;

public class MctsAlgo implements BaseAlgo {
    /** Number of epochs of simulation. */
    private final int epochs = 100000;

    @Override
    public Move nextMove(Board board) {
        MctsNode root = new MctsNode(null, null, board);
        for (int i = 0; i < this.epochs; i ++) {
            if (i % 10000 == 0) {
                System.out.printf("Training epoch %d%n", i);
            }
            MctsNode leaf = root.select();
            MctsNode child = leaf.expand();
            double value = child.simulate();
            child.backPropagates(value);
        }
        return root.getBestMove();
    }
}
