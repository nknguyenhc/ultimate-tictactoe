package algo.mcts;

import board.Board;
import board.Move;
import board.Utils;

import java.util.List;
import java.util.Random;

class MctsNode {
    /** The random number generator for this node. */
    private final Random rng = new Random();
    /** The value of winning. */
    private static final double WIN = 10;
    private static final double C = 1.4;
    private int N = 0;
    private double U = 0;
    /** The parent node of this node. Only {@code null} if this is root node. */
    private final MctsNode parent;
    /** The move that led to this state. */
    private final Move move;
    private final Board board;
    /** {@code null} if not expanded, an array otherwise. */
    private MctsNode[] children = null;

    public MctsNode(MctsNode parent, Move move, Board board) {
        this.parent = parent;
        this.move = move;
        this.board = board;
    }

    /**
     * Returns the UCB value of this node.
     * Must not call this at root node.
     */
    private double ucb() {
        if (this.N == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return this.U / this.N + MctsNode.C * Math.sqrt(Math.log(this.parent.N) / this.N);
    }

    public MctsNode select() {
        if (this.children == null) {
            return this;
        }

        double bestValue = 0;
        MctsNode bestChild = null;
        for (MctsNode child: this.children) {
            if (bestChild == null) {
                bestChild = child;
                bestValue = child.ucb();
                continue;
            }
            if (child.ucb() > bestValue) {
                bestChild = child;
                bestValue = child.ucb();
            }
        }
        assert bestChild != null;
        return bestChild.select();
    }

    /**
     * Expands the current node.
     * Assume that the current node has not been previously expanded.
     * @return A random child node.
     */
    public MctsNode expand() {
        assert this.children == null;
        if (this.board.winner() != Utils.Side.U) { // Board is terminal
            return this;
        }

        List<Move> actions = this.board.actions();
        this.children = new MctsNode[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            this.children[i] = new MctsNode(this, actions.get(i), board.move(actions.get(i)));
        }

        int index = this.rng.nextInt(actions.size());
        return this.children[index];
    }

    /**
     * Simulates a random walk.
     */
    public double simulate() {
        Board board = this.board;
        while (board.winner() == Utils.Side.U) {
            List<Move> actions = board.actions();
            int index = this.rng.nextInt(actions.size());
            board = board.move(actions.get(index));
        }
        if (board.winner() == Utils.Side.D) {
            return 0;
        }
        Utils.Side side = this.board.getTurn() ? Utils.Side.X : Utils.Side.O;
        return board.winner() == side ? MctsNode.WIN : -MctsNode.WIN;
    }

    /**
     * Back propagates the utility value, to this node and the parent nodes.
     */
    public void backPropagates(double utility) {
        this.U += utility;
        this.N += 1;
        if (this.parent != null) {
            this.parent.backPropagates(-utility);
        }
    }

    /**
     * Returns the optimal move at this state, based on the exploration.
     * To be called in root node only. Assume that root node is not terminal.
     */
    public Move getBestMove() {
        MctsNode bestChild = null;
        for (MctsNode child: this.children) {
            if (bestChild == null) {
                bestChild = child;
                continue;
            }
            if (bestChild.N < child.N) {
                bestChild = child;
            }
        }
        assert bestChild != null;
        return bestChild.move;
    }
}
