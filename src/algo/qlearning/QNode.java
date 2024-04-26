package algo.qlearning;

import board.Board;
import board.Move;
import board.Utils;

import java.util.List;
import java.util.Random;

class QNode {
    /** The parent of this node. */
    private QNode parent;
    /** The board that this node represents. */
    private final Board board;
    /** The move that transitions the parent board to this board. */
    private final Move move;
    /** The Q-value of this board. In other words, the Q-value of the parent board and the transitioning move. */
    private double qValue = 0;
    private QNode[] children;

    /** Random number generator, for generating random moves and determining whether to select a random move. */
    private static final Random rng = new Random();
    /** Learning coefficient. */
    private static final double alpha = 0.3;
    /** Discount factor. */
    private static final double gamma = 0.9;
    /** Utility value for a win. */
    private static final double WIN = 10;

    /**
     * Constructor for root node.
     * @param board The board that the new node should represent.
     */
    public QNode(Board board) {
        this.parent = null;
        this.board = board;
        this.move = null;
    }

    private QNode(Board board, Move move, QNode parent) {
        this.parent = parent;
        this.board = board;
        this.move = move;
    }

    /**
     * Set up the children of this node.
     */
    private void setup() {
        if (this.children != null) {
            // Already set up
            return;
        }

        List<Move> moves = this.board.actions();
        this.children = new QNode[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            Board nextBoard = this.board.move(move);
            this.children[i] = new QNode(nextBoard, move, this);
        }
    }

    /**
     * Performs a training iteration. Recursively calls on the child.
     * @param p The probability of choosing a random move.
     */
    public void train(double p) {
        if (this.board.winner() == Utils.Side.U) {
            this.setup();
            QNode node = this.selectMove(p);
            node.train(p);
        } else {
            if (this.board.winner() == Utils.Side.D) {
                this.update(0);
            } else {
                Utils.Side turn = this.board.getTurn() ? Utils.Side.X : Utils.Side.O;
                double utility = this.board.winner() == turn ? QNode.WIN : -QNode.WIN;
                this.update(utility);
            }
        }
    }

    /**
     * Selects a move for training.
     * @param p The probability of choosing a random move.
     */
    private QNode selectMove(double p) {
        double random = QNode.rng.nextDouble();
        if (random < p) {
            return this.selectRandomMove();
        } else {
            return this.selectBestMove();
        }
    }

    private QNode selectRandomMove() {
        int index = QNode.rng.nextInt(this.children.length);
        return this.children[index];
    }

    private QNode selectBestMove() {
        if (this.children == null) {
            return null;
        }
        QNode bestChild = null;
        for (QNode child: this.children) {
            if (bestChild == null) {
                bestChild = child;
                continue;
            }
            if (bestChild.qValue < child.qValue) {
                bestChild = child;
            }
        }
        return bestChild;
    }

    /**
     * Updates the q value of this node with the given utility.
     */
    private void update(double utility) {
        this.qValue = this.newQValue(utility);
        if (this.parent != null) {
            this.parent.update(-utility);
        }
    }

    /**
     * Computes the new q-value of this node.
     * @param utility The utility obtained from results of training.
     */
    private double newQValue(double utility) {
        return this.qValue + QNode.alpha * (utility - this.qValue);
    }

    /**
     * Returns the best move at this node, in real game play.
     */
    public Move bestMove() {
        QNode node = this.selectBestMove();
        assert node != null;
        return node.move;
    }

    /**
     * Selects the child node with the given move.
     */
    public QNode child(Move move) {
        for (QNode child: this.children) {
            assert child.move != null;
            if (child.move.equals(move)) {
                return child;
            }
        }
        assert false;
        return null;
    }

    /**
     * Selects the child node with the given board.
     */
    public QNode child(Board board) {
        for (QNode child: this.children) {
            if (child.board.equals(board)) {
                return child;
            }
        }
        assert false;
        return null;
    }

    /**
     * Makes this node a root node.
     * This prevents q-value update up the parents of the past boards.
     */
    public void makeRoot() {
        this.parent = null;
    }

    /**
     * Returns the array of children nodes.
     * Makes a copy to avoid accidental edits.
     */
    public QNode[] getChildren() {
        return this.children.clone();
    }

    /**
     * Returns the trace of this node for debugging.
     */
    public String trace() {
        QNode curr = this;
        StringBuilder trace = new StringBuilder();
        trace.append(String.format("Move: %s; Utility: %.3f\n", this.move, this.qValue));
        QNode child = curr.selectBestMove();
        while (child != null) {
            trace.append(curr.board.toCompactString());
            trace.append(String.format("; Best move: %s; Utility: %.3f\n", child.move, child.qValue));
            curr = child;
            child = curr.selectBestMove();
        }
        return trace.toString();
    }
}
