package algo.qlearning;

import algo.BaseAlgo;
import board.Board;
import board.Move;

public class QLearningAlgo implements BaseAlgo {
    /** Root node of the last call to {@code nextMove}. */
    private QNode root;
    /** The move that the agent last chose. */
    private Move move;
    private final int epochs = 10000;

    @Override
    public Move nextMove(Board board) {
        this.setupRoot(board);
        for (int i = 0; i < this.epochs; i++) {
            if (i % 1000 == 0) {
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
        return (double) (this.epochs - i) / (2 * this.epochs);
    }

    @Override
    public String trace() {
        return "";
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        return null;
    }
}
