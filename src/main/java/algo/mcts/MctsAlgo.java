package algo.mcts;

import algo.BaseAlgo;
import board.Board;
import board.Move;

import java.util.List;

public class MctsAlgo implements BaseAlgo {
    /** Number of epochs of simulation. */
    private final int epochs = 80000;
    private MctsNode root;
    private final boolean continueLastSearch;

    private boolean hasPonderedAfterSearch = false;
    private boolean isPondering = false;

    public MctsAlgo() {
        this.continueLastSearch = false;
    }

    public MctsAlgo(boolean continueLastSearch) {
        this.continueLastSearch = continueLastSearch;
    }

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
    public List<Move> getMovePredictions() {
        assert this.root != null;
        return this.root.bestMoveSequence();
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        if (this.continueLastSearch) {
            this.setupRoot(board);
        } else {
            this.root = new MctsNode(null, null, board);
        }
        long startTime = System.currentTimeMillis();
        long endTime = startTime + time;
        while (System.currentTimeMillis() < endTime) {
            this.search();
        }
        return this.root.getBestMove();
    }

    private void setupRoot(Board board) {
        if (this.root == null) {
            this.root = new MctsNode(null, null, board);
        } else {
            this.continueSearch(board);
        }
    }

    private void continueSearch(Board board) {
        if (this.hasPonderedAfterSearch) {
            this.root = this.root.child(board);
            this.hasPonderedAfterSearch = false;
        } else {
            this.root = this.root.grandchild(board);
        }
    }

    /**
     * Returns the evaluation of the last run board.
     */
    public double evaluate() {
        return this.root.evaluate();
    }

    @Override
    public void ponder() {
        if (!this.continueLastSearch) {
            // No point pondering if not continuing last search
            return;
        }

        this.setupRootForPondering();
        this.hasPonderedAfterSearch = true;
        this.isPondering = true;
        new Thread(() -> {
            while (this.isPondering) {
                this.search();
            }
        }).start();
    }

    private void setupRootForPondering() {
        if (this.root == null) {
            this.root = new MctsNode(null, null, new Board());
        } else {
            this.root = this.root.getBestChild();
        }
    }

    @Override
    public void stopPondering() {
        this.isPondering = false;
    }
}
