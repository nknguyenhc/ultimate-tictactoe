package algo.mcts;

import board.Board;
import board.Move;
import board.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MctsNode {
    /** The random number generator for this node. */
    private final Random rng = new Random();
    /** The value of winning. */
    private static final double WIN = 1;
    private static final double C = 1.4;
    private int N = 0;
    private double U = 0;
    /** The parent node of this node. Only {@code null} if this is root node. */
    private MctsNode parent;
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
        // To encourage the parent to explore nodes that causes opponent to lose,
        // We invert the utility in UCB.
        return -this.U / this.N + MctsNode.C * Math.sqrt(Math.log(this.parent.N) / this.N);
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
     * Returns the child that contains the best move.
     * If this is a terminal node, returns null.
     * Select based on the number of traversals.
     */
    private MctsNode getBestChild() {
        if (this.children == null) {
            return null;
        }
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
        return bestChild;
    }

    /**
     * Returns the optimal move at this state, based on the exploration.
     * To be called in root node only. Assume that root node is not terminal.
     */
    public Move getBestMove() {
        MctsNode bestChild = this.getBestChild();
        assert bestChild != null;
        return bestChild.move;
    }

    /**
     * Returns the best move sequence from this board,
     * including the move for the current board.
     */
    public List<Move> bestMoveSequence() {
        MctsNode node = this.getBestChild();
        List<Move> sequence = new ArrayList<>();
        while (node != null) {
            sequence.add(node.move);
            node = node.getBestChild();
        }
        return sequence;
    }

    /**
     * Returns the trace of this node, if the agent were to choose a move from here.
     */
    public String trace() {
        MctsNode curr = this;
        StringBuilder trace = new StringBuilder();
        trace.append(String.format("Move: %s\n", this.move));
        MctsNode child = curr.getBestChild();
        while (child != null) {
            trace.append(curr.board.toCompactString());
            trace.append(String.format("; Best move: %s; Utility: %.3f, Count: %d\n", child.move, curr.U, curr.N));
            curr = child;
            child = curr.getBestChild();
        }
        return trace.toString();
    }

    public double evaluate() {
        return this.U / this.N;
    }

    /**
     * Selects the grandchild that has the board.
     * Makes the grandchild the root node.
     */
    public MctsNode grandchild(Board board) {
        MctsNode child = this.getBestChild();
        assert child != null;
        for (MctsNode grandchild: child.children) {
            if (grandchild.board.equals(board)) {
                grandchild.parent = null;
                return grandchild;
            }
        }
        assert false;
        return null;
    }

    /**
     * Returns the children nodes of this node.
     * Use a copy to prevent accidental edit.
     */
    public MctsNode[] getChildren() {
        return this.children.clone();
    }
}
