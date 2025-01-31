package algo.sarsa;

import board.Board;
import board.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SarsaNode {
    /** The board that this node encapsulates. */
    private final Board board;
    /** The node that transitions the parent node to this node. */
    private final int move;
    private SarsaNode parent;
    /** The q-value of this node. */
    private double qValue = 0;
    private SarsaNode[] children;
    private int numVisits = 0;

    /** Random number generator, for generating random moves and determining whether to select a random move. */
    private static final Random rng = new Random();
    /** Discount factor. */
    private static final double gamma = 0.98;
    /** Learning rate. */
    private static final double alpha = 0.15;
    /** Utility value for a win. */
    private static final double WIN = 100;

    private SarsaNode(SarsaNode parent, int move, Board board) {
        this.parent = parent;
        this.move = move;
        this.board = board;
        this.earlyQValue();
    }

    public SarsaNode(Board board) {
        this.parent = null;
        this.move = -1;
        this.board = board;
    }

    public void makeRoot() {
        this.parent = null;
    }

    /**
     * Early determination of the q-value of this board.
     * Only for board that has a winner.
     */
    private void earlyQValue() {
        if (this.board.winner() == Utils.Side.X || this.board.winner() == Utils.Side.O) {
            this.qValue = -SarsaNode.WIN;
        }
    }

    /**
     * Set up this node with the children.
     * Not to be called at a terminal node.
     */
    private void setup() {
        if (this.children != null) {
            // Already set up
            return;
        }

        List<Integer> moves = this.board.actions();
        this.children = new SarsaNode[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            int move = moves.get(i);
            Board nextBoard = this.board.move(move);
            this.children[i] = new SarsaNode(this, move, nextBoard);
        }
    }

    /**
     * Performs a training iteration. Recursive calls on the child.
     * If called from outside, not to be called at a terminal node.
     * @param p The probability of choosing a random move.
     */
    public void train(double p) {
        this.numVisits++;
        if (this.board.winner() == Utils.Side.U) {
            this.setup();
            SarsaNode node = this.selectNode(p);
            node.train(p);
        } else {
            this.parent.update(this.qValue);
        }
    }

    /**
     * Selects a child node for training.
     * @param p The probability of choosing a random move.
     */
    private SarsaNode selectNode(double p) {
        double random = SarsaNode.rng.nextDouble();
        if (random < p) {
            return this.selectRandomNode();
        } else {
            return this.selectBestNode();
        }
    }

    private SarsaNode selectRandomNode() {
        int index = SarsaNode.rng.nextInt(this.children.length);
        return this.children[index];
    }

    private SarsaNode selectBestNode() {
        if (this.children == null) {
            return null;
        }
        SarsaNode bestChild = null;
        for (SarsaNode child: this.children) {
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
     * Update the q-value of this node.
     * @param childUtility The q-value of the child node in the game simulation.
     */
    private void update(double childUtility) {
        this.qValue += SarsaNode.alpha * (-SarsaNode.gamma * childUtility - this.qValue);
        if (this.parent != null) {
            this.parent.update(this.qValue);
        }
    }

    /**
     * Returns the best move at this node, in a real game play.
     */
    public int bestMove() {
        SarsaNode node = this.selectBestNode();
        assert node != null;
        return node.move;
    }

    /**
     * Returns the sequence of best move starting from this board.
     */
    public List<Integer> bestMoveSequence() {
        SarsaNode node = this.selectBestNode();
        List<Integer> sequence = new ArrayList<>();
        while (node != null) {
            sequence.add(node.move);
            node = node.selectBestNode();
        }
        return sequence;
    }

    /**
     * Selects the child node with the given move.
     */
    public SarsaNode child(int move) {
        for (SarsaNode child: this.children) {
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
    public SarsaNode child(Board board) {
        for (SarsaNode child: this.children) {
            if (child.board.equals(board)) {
                return child;
            }
        }
        assert false;
        return null;
    }

    public String trace() {
        SarsaNode curr = this;
        StringBuilder trace = new StringBuilder();
        trace.append(String.format("Move: %s, Utility: %.20f; Count: %d\n", this.move, this.qValue, this.numVisits));
        SarsaNode child = curr.selectBestNode();
        while (child != null) {
            trace.append(curr.board.toCompactString());
            trace.append(String.format(
                    "; Utility: %.20f; Count: %d; Best move: %s\n",
                    curr.qValue, curr.numVisits, child.move));
            curr = child;
            child = curr.selectBestNode();
        }
        return trace.toString();
    }

    public double getQValue() {
        return this.qValue;
    }

    public SarsaNode[] getChildren() {
        return this.children.clone();
    }
}
