package algo.qlearning;

import algo.BaseAlgo;
import board.Board;

import java.util.List;

public class QLearningAlgo implements BaseAlgo {
    /** Root node of the last call to {@code nextMove}. */
    private QNode root;
    /** The move that the agent last chose. */
    private byte move;
    /** Maximum probability of making a random move. */
    private final double p = 0.1;
    /** Number of training epochs, also used for calculating probability of making a random move. */
    private final int epochs = 80000;

    @Override
    public byte nextMove(Board board) {
        this.setupRoot(board);
        for (int i = 0; i < this.epochs; i++) {
            if (i % 10000 == 0) {
                System.out.printf("Training loop %d%n", i);
            }
            this.root.train(this.calculateRandomProb(i));
        }
        this.move = this.root.bestMove();
        return this.move;
    }

    private void setupRoot(Board board) {
        if (this.root == null) {
            this.root = new QNode(board);
        } else {
            QNode opponentBoard = this.root.child(this.move);
            this.root = opponentBoard.child(board);
            this.root.makeRoot();
        }
    }

    /**
     * Calculates the probability of making a random move during training.
     * @param i The iteration count.
     */
    private double calculateRandomProb(int i) {
        return this.p;
    }

    @Override
    public String trace() {
        assert this.root != null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Root utility: %.3f\n", this.root.getQValue()));
        QNode[] children = this.root.getChildren();
        for (QNode child: children) {
            stringBuilder.append(child.trace());
        }
        return stringBuilder.toString();
    }

    @Override
    public List<Byte> getMovePredictions() {
        assert this.root != null;
        return this.root.bestMoveSequence();
    }

    @Override
    public byte nextMoveWithTime(Board board, int time) {
        this.setupRoot(board);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + time;
        int i = 0;
        while (System.currentTimeMillis() < endTime) {
            this.root.train(this.calculateRandomProb(i));
            i++;
        }
        this.move = this.root.bestMove();
        return this.move;
    }

    @Override
    public void ponder() {

    }

    @Override
    public void stopPondering() {

    }
}
