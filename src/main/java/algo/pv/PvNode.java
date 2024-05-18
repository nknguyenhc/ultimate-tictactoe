package algo.pv;

import board.Board;
import board.Move;
import board.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class PvNode implements Comparable<PvNode> {
    private PvNode parent = null;
    private final Move move;
    private final Board board;

    private final Random rng = new Random();
    private static final double WIN = 1;
    private static final double C = 1.4;
    private int N = 0;
    private double U = 0;
    private boolean isMctsRoot = false;

    private PvNode[] children = null;
    private PvNode bestChild;
    private static final int epochs = 100;

    private PvNode(PvNode parent, Move move, Board board) {
        this.parent = parent;
        this.move = move;
        this.board = board;
    }

    public PvNode(Board board) {
        this.move = null;
        this.board = board;
    }

    private double ucb() {
        assert !this.isMctsRoot;
        if (this.N == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return -this.U / this.N + PvNode.C * Math.sqrt(Math.log(this.parent.N) / this.N);
    }

    private PvNode select() {
        if (this.children == null) {
            return this;
        }

        double bestValue = 0;
        PvNode bestChild = null;
        for (PvNode child: this.children) {
            if (bestChild != null && child.ucb() <= bestValue) {
                continue;
            }
            bestChild = child;
            bestValue = child.ucb();
        }
        assert bestChild != null;
        return bestChild.select();
    }

    private PvNode expand() {
        assert this.children == null;
        if (this.board.winner() != Utils.Side.U) {
            return this;
        }

        this.createChildren();
        int index = this.rng.nextInt(this.children.length);
        return this.children[index];
    }

    /**
     * Instantiates the children of this node.
     * Only call on non-terminal node,
     * and assuming that the children array is not instantiated.
     */
    private void createChildren() {
        List<Move> actions = this.board.actions();
        this.children = new PvNode[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            Move action = actions.get(i);
            this.children[i] = new PvNode(this, action, this.board.move(action));
        }
    }

    private double simulate() {
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
        return board.winner() == side ? PvNode.WIN : -PvNode.WIN;
    }

    private void backPropagates(double utility) {
        this.U += utility;
        this.N += 1;
        if (!this.isMctsRoot) {
            this.parent.backPropagates(-utility);
        }
    }

    private double utility() {
        if (this.N == 0) {
            return 0;
        }
        return this.U / this.N;
    }

    @Override
    public int compareTo(PvNode node) {
        return Double.compare(this.utility(), node.utility());
    }

    public double evaluate() {
        if (this.board.winner() != Utils.Side.U) {
            if (this.board.winner() == Utils.Side.D) {
                return 0;
            } else {
                return -PvNode.WIN;
            }
        }

        this.isMctsRoot = true;
        for (int i = 0; i < PvNode.epochs; i++) {
            PvNode node = this.select();
            PvNode child = node.expand();
            double value = child.simulate();
            child.backPropagates(value);
        }
        this.isMctsRoot = false;
        return this.utility();
    }

    /**
     * Sort the children of this tree, with most promising child in front.
     * Only call this on a non-terminal node.
     */
    private void sortChildren() {
        if (this.children == null) {
            this.createChildren();
        }
        Arrays.sort(this.children);
    }

    /**
     * Searches this subtree and returns the evaluation of this subtree.
     */
    private double search(int depth, double alpha, double beta) {
        if (depth == 0 || this.board.winner() != Utils.Side.U) {
            return this.evaluate();
        }

        if (depth == 1 || depth == 2) {
            this.sortChildren();
        }

        double bestValue = -PvNode.WIN;
        for (PvNode child: this.children) {
            double value = -child.search(depth - 1, -beta, -alpha);
            if (value > bestValue) {
                bestValue = value;
                this.bestChild = child;
            }
            if (bestValue > alpha) {
                alpha = bestValue;
            }
            if (alpha >= beta) {
                break;
            }
        }
        return bestValue;
    }

    /**
     * Entry point of the search routine.
     */
    public Move search(int depth) {
        this.search(depth, -PvNode.WIN, PvNode.WIN);
        return this.bestChild.move;
    }

    @Override
    public String toString() {
        return Double.toString(this.utility());
    }
}
