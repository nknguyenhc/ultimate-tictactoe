package algo.sarsa;

import algo.BaseAlgo;
import board.Board;
import board.Move;

import java.util.List;

public class SarsaAlgo implements BaseAlgo {
    /** Root node of the last call to {@code nextMove}. */
    private SarsaNode root;
    /** The move that the agent last chose. */
    private Move move;
    /** Probability of making a random move. */
    private final double p = 0.1;
    /** Number of training epochs. */
    private final int epochs = 500000;

    @Override
    public Move nextMove(Board board) {
        this.setupRoot(board);
        for (int i = 0; i < this.epochs; i++) {
            if (i % 10000 == 0) {
                System.out.printf("Training loop %d%n", i);
            }
            this.root.train(this.p);
        }
        this.move = this.root.bestMove();
        return this.move;
    }

    private void setupRoot(Board board) {
        if (this.root == null) {
            this.root = new SarsaNode(board);
        } else {
            SarsaNode opponentBoard = this.root.child(this.move);
            this.root = opponentBoard.child(board);
            this.root.makeRoot();
        }
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        this.setupRoot(board);
        long endTime = System.currentTimeMillis() + time;
        while (System.currentTimeMillis() < endTime) {
            this.root.train(this.p);
        }
        this.move = this.root.bestMove();
        return this.move;
    }

    @Override
    public String trace() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Root utility: %.20f\n", this.root.getQValue()));
        SarsaNode[] children = this.root.getChildren();
        for (SarsaNode child: children) {
            stringBuilder.append(child.trace());
        }
        return stringBuilder.toString();
    }

    @Override
    public List<Move> getMovePredictions() {
        assert this.root != null;
        return this.root.bestMoveSequence();
    }

    @Override
    public void ponder() {
        
    }
}
