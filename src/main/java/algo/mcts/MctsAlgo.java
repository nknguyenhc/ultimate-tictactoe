package algo.mcts;

import algo.BaseAlgo;
import board.Board;
import board.Move;

public class MctsAlgo implements BaseAlgo {
    /** Number of epochs of simulation. */
    private final int epochs = 100000;
    private MctsNode root;

    @Override
    public Move nextMove(Board board) {
        this.root = new MctsNode(null, null, board);
        for (int i = 0; i < this.epochs; i ++) {
            if (i % 10000 == 0) {
                System.out.printf("Training epoch %d%n", i);
            }
            this.search();
        }
        return this.root.getBestMove();
    }

    private void search() {
        MctsNode leaf = this.root.select();
        MctsNode child = leaf.expand();
        double value = child.simulate();
        child.backPropagates(value);
    }

    @Override
    public String trace() {
        assert this.root != null;
        StringBuilder stringBuilder = new StringBuilder();
        MctsNode[] children = this.root.getChildren();
        for (int i = 0; i < children.length; i++) {
            stringBuilder.append(String.format("\nChild %d:\n", i));
            stringBuilder.append(children[i].trace());
        }
        return stringBuilder.toString();
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        this.root = new MctsNode(null, null, board);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + time * 1000L;
        while (System.currentTimeMillis() < endTime) {
            this.search();
        }
        return this.root.getBestMove();
    }

    /**
     * Returns the evaluation of the last run board.
     */
    public double evaluate() {
        return this.root.evaluate();
    }
}
