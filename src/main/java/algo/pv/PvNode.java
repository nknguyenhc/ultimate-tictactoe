package algo.pv;

import board.Board;
import board.Move;
import board.Utils;

import java.util.List;
import java.util.Random;

class PvNode {
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
    private static final int epochs = 1000;

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

        List<Move> actions = this.board.actions();
        this.children = new PvNode[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            Move action = actions.get(i);
            this.children[i] = new PvNode(this, action, this.board.move(action));
        }

        int index = this.rng.nextInt(actions.size());
        return this.children[index];
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

    public double evaluate() {
        this.isMctsRoot = true;
        for (int i = 0; i < PvNode.epochs; i++) {
            PvNode node = this.select();
            PvNode child = node.expand();
            double value = child.simulate();
            child.backPropagates(value);
        }
        this.isMctsRoot = false;
        return this.U / this.N;
    }
}
