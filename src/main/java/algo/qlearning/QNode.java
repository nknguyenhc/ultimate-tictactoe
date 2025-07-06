package algo.qlearning;

import board.Board;
import board.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class QNode {
    /** The parent of this node. */
    private QNode parent;
    /** The board that this node represents. */
    private final Board board;
    /** The move that transitions the parent board to this board. */
    private final byte move;
    /** The Q-value of this board. In other words, the Q-value of the parent board and the transitioning move. */
    private double qValue = 0;
    private QNode[] children;
    private int numVisits = 0;

    /** Random number generator, for generating random moves and determining whether to select a random move. */
    private static final Random rng = new Random();
    /** Maximum learning coefficient. */
    private static final double alphaMax = 0.4;
    /** Minimum learning coefficient. */
    private static final double alphaMin = 0.01;
    /** Move count that reaches {@code alphaMin}. */
    private static final int maxMoveCount = 30;
    private static final double A = QNode.alphaMax;
    private static final double k = Math.log(QNode.alphaMax / QNode.alphaMin) / (QNode.maxMoveCount);
    /** Utility value for a win. */
    private static final double WIN = 10;

    /**
     * Constructor for root node.
     * @param board The board that the new node should represent.
     */
    public QNode(Board board) {
        this.parent = null;
        this.board = board;
        this.move = -1;
    }

    private QNode(Board board, byte move, QNode parent) {
        this.parent = parent;
        this.board = board;
        this.move = move;
        this.earlyQValue();
    }

    /**
     * Determines the q-value of this node, if the board is terminal.
     */
    private void earlyQValue() {
        if (this.board.winner == Utils.Side.X || this.board.winner == Utils.Side.O) {
            this.qValue = -QNode.WIN;
        }
    }

    /**
     * Set up the children of this node.
     * Not to be called at a terminal node.
     */
    private void setup() {
        if (this.children != null) {
            // Already set up
            return;
        }

        List<Byte> moves = this.board.actions();
        this.children = new QNode[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            byte move = moves.get(i);
            Board nextBoard = this.board.move(move);
            this.children[i] = new QNode(nextBoard, move, this);
        }
    }

    /**
     * Performs a training iteration. Recursively calls on the child.
     * @param p The probability of choosing a random move.
     */
    public void train(double p) {
        if (this.board.winner == Utils.Side.U) {
            this.setup();
            QNode node = this.selectMove(p);
            node.train(p);
        } else {
            if (this.board.winner == Utils.Side.D) {
                this.update(0, 0);
            } else {
                this.update(-QNode.WIN, 0);
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
            if (child.qValue < bestChild.qValue) {
                bestChild = child;
            }
        }
        return bestChild;
    }

    /**
     * Updates the q value of this node with the given utility.
     */
    private void update(double utility, int moveCountFromEnd) {
        this.numVisits++;
        this.qValue = this.newQValue(utility, moveCountFromEnd);
        if (this.parent != null) {
            this.parent.update(-utility, moveCountFromEnd + 1);
        }
    }

    /**
     * Computes the new q-value of this node.
     * @param utility The utility obtained from results of training.
     */
    private double newQValue(double utility, int moveCountFromEnd) {
        double alpha = Math.max(
                // Bytecoder currently does not support Math.exp
                QNode.A * Math.pow(Math.E, -QNode.k * moveCountFromEnd),
                QNode.alphaMin);
        return this.qValue + alpha * (utility - this.qValue);
    }

    /**
     * Returns the best move at this node, in real game play.
     */
    public byte bestMove() {
        QNode node = this.selectBestMove();
        assert node != null;
        return node.move;
    }

    /**
     * Returns the sequence of best moves.
     */
    public List<Byte> bestMoveSequence() {
        QNode node = this.selectBestMove();
        List<Byte> sequence = new ArrayList<>();
        while (node != null) {
            sequence.add(node.move);
            node = node.selectBestMove();
        }
        return sequence;
    }

    /**
     * Selects the child node with the given move.
     */
    public QNode child(int move) {
        for (QNode child: this.children) {
            assert child.move != -1;
            if (child.move == move) {
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
        trace.append(String.format("Move: %s, Utility: %.3f; Count: %d\n", this.move, this.qValue, this.numVisits));
        QNode child = curr.selectBestMove();
        while (child != null) {
            trace.append(curr.board.toCompactString());
            trace.append(String.format(
                    "; Count: %d; Utility: %.3f; Best move: %s\n",
                    curr.numVisits, curr.qValue, child.move));
            curr = child;
            child = curr.selectBestMove();
        }
        return trace.toString();
    }

    public double getQValue() {
        return this.qValue;
    }
}
