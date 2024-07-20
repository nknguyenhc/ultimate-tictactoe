package algo.mcts.parallel;

import board.Board;
import board.Move;
import board.Utils;

import java.util.List;
import java.util.Random;

public class ParallelMctsNode {
    private static final Random rng = new Random();
    private static final int WIN = 1;
    private static final double C = 1.4;
    private int N = 0;
    private int U = 0;

    private ParallelMctsNode parent;
    private final Move move;
    private final Board board;

    private ParallelMctsNode[] children = null;

    private ParallelMctsNode(ParallelMctsNode parent, Move move, Board board) {
        this.parent = parent;
        this.move = move;
        this.board = board;
    }

    public ParallelMctsNode(Board board) {
        this.parent = null;
        this.move = null;
        this.board = board;
    }

    private double ucb() {
        if (this.N == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return - (double) this.U / this.N + ParallelMctsNode.C * Math.sqrt(Math.log(this.parent.N) / this.N);
    }

    private ParallelMctsNode select() {
        if (this.children == null) {
            return this;
        }

        ParallelMctsNode bestChild = null;
        for (ParallelMctsNode child: this.children) {
            if (bestChild == null || child.ucb() > bestChild.ucb()) {
                bestChild = child;
            }
        }
        assert bestChild != null;
        return bestChild.select();
    }

    private ParallelMctsNode expand() {
        assert this.children == null;
        if (this.board.winner() != Utils.Side.U) {
            return this;
        }

        int childrenCount = this.setupChildren();
        int index = ParallelMctsNode.rng.nextInt(childrenCount);
        return this.children[index];
    }

    public int setupChildren() {
        if (this.children != null) {
            return this.children.length;
        }

        List<Move> actions = this.board.actions();
        int childrenCount = actions.size();
        this.children = new ParallelMctsNode[childrenCount];
        for (int i = 0; i < childrenCount; i++) {
            Move move = actions.get(i);
            this.children[i] = new ParallelMctsNode(this, move, this.board.move(move));
        }
        return childrenCount;
    }

    private int simulate() {
        Board board = this.board;
        while (board.winner() == Utils.Side.U) {
            List<Move> actions = board.actions();
            int index = ParallelMctsNode.rng.nextInt(actions.size());
            board = board.move(actions.get(index));
        }

        if (board.winner() == Utils.Side.D) {
            return 0;
        }
        Utils.Side side = this.board.getTurn() ? Utils.Side.X : Utils.Side.O;
        return board.winner() == side ? ParallelMctsNode.WIN : -ParallelMctsNode.WIN;
    }

    private void backProp(int utility) {
        this.U += utility;
        this.N += 1;
        if (this.parent != null) {
            this.parent.backProp(-utility);
        }
    }

    public void search() {
        ParallelMctsNode leaf = this.select();
        ParallelMctsNode child = leaf.expand();
        int value = child.simulate();
        child.backProp(value);
    }

    public void search(long endTime) {
        while (System.currentTimeMillis() < endTime) {
            this.search();
        }
    }

    private ParallelMctsNode getBestRollout() {
        if (this.children == null) {
            return null;
        }

        ParallelMctsNode bestChild = null;
        for (ParallelMctsNode child: this.children) {
            if (bestChild == null || bestChild.N < child.N) {
                bestChild = child;
            }
        }
        return bestChild;
    }

    private ParallelMctsNode getBestUtility() {
        if (this.children == null) {
            return null;
        }

        ParallelMctsNode bestChild = null;
        for (ParallelMctsNode child: this.children) {
            if (bestChild == null || child.utility() < bestChild.utility()) {
                bestChild = child;
            }
        }
        assert bestChild != null;
        return bestChild;
    }

    public Move getBestMoveByUtility() {
        ParallelMctsNode bestChild = this.getBestUtility();
        assert bestChild != null;
        return bestChild.move;
    }

    public Move getBestMoveByRollout() {
        ParallelMctsNode bestChild = this.getBestRollout();
        assert bestChild != null;
        return bestChild.move;
    }

    /**
     * Returns the children, assuming that this array will not be mutated.
     */
    public ParallelMctsNode[] getChildren() {
        return this.children;
    }

    public ParallelMctsNode child(Board board) {
        if (this.children == null) {
            return null;
        }
        for (ParallelMctsNode child: this.children) {
            if (child.board.equals(board)) {
                return child;
            }
        }
        return null;
    }

    public ParallelMctsNode child(Move move) {
        if (this.children == null) {
            return null;
        }
        for (ParallelMctsNode child: this.children) {
            if (child.move.equals(move)) {
                return child;
            }
        }
        return null;
    }

    public void makeRoot() {
        this.parent = null;
    }

    public double utility() {
        return (double) this.U / this.N;
    }

    public String trace() {
        ParallelMctsNode curr = this;
        StringBuilder trace = new StringBuilder();
        trace.append(String.format("Move: %s\n", this.move));
        ParallelMctsNode child = curr.getBestRollout();
        while (child != null) {
            trace.append(curr.board.toCompactString());
            trace.append(String.format("; Best move: %s; Utility: %.3f; Count: %d\n",
                    child.move, curr.utility(), curr.N));
            curr = child;
            child = curr.getBestRollout();
        }
        return trace.toString();
    }
}
