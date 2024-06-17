package algo.pv;

import board.Board;
import board.Move;
import board.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class PvNode implements Comparable<PvNode> {
    /** The parent node of this node. */
    private PvNode parent = null;
    /** The move that transitions from the parent node to this node. */
    private final Move move;
    /** The board that this node represents. */
    private final Board board;

    /** A random number generator for this class. */
    private static final Random rng = new Random();
    /** Score/reward for winning a game. */
    private static final double WIN = 1;
    /** MCTS coefficient that balances exploitation and exploration. */
    private static final double C = 1.4;
    /** Number of MCTS visits to this node. */
    private int N = 0;
    /** Total utilities of all MCTS visits to this node. */
    private double U = 0;
    /** Whether this node is the root of an MCTS search, in evaluation. */
    private boolean isMctsRoot = false;

    /** Children node of this node. */
    private PvNode[] children = null;
    /** The child node that contains the current best move. */
    private PvNode bestChild;
    /** Number of training epochs for MCTS evaluation. */
    private static final int epochs = 150;

    /** Size of null-search window, in PV search. */
    private static final double NULL_WINDOW_RATIO = 0.0001;
    /** The bound type of this node in transposition table. */
    private BoundType boundType = BoundType.NONE;
    /** The score of a previous search, in transposition table. */
    private double ttScore = 0;
    /** The depth of a previous search, in transposition table. */
    private int ttDepth = 0;

    /** End time of the search, if there is time limit. */
    private static long endTime = 0;
    /** Whether we are doing a time-limited search. */
    private static boolean isTrackingTime = false;

    /**
     * Constructs a new node.
     * @param parent The parent node of this node.
     * @param move The move that transitions from the parent node to this node.
     * @param board The board that this node represents.
     */
    private PvNode(PvNode parent, Move move, Board board) {
        this.parent = parent;
        this.move = move;
        this.board = board;
    }

    /**
     * Constructs a new root node of a PV search.
     * @param board The board that the root node represents.
     */
    public PvNode(Board board) {
        this.move = null;
        this.board = board;
    }

    /**
     * Selection policy in MCTS.
     * @return The priority level for exploration of this node.
     */
    private double ucb() {
        assert !this.isMctsRoot;
        if (this.N == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return -this.U / this.N + PvNode.C * Math.sqrt(Math.log(this.parent.N) / this.N);
    }

    /**
     * MCTS selection.
     * @return The child node selected, or itself if this node has not been expanded.
     */
    private PvNode select() {
        if (this.children == null) {
            return this;
        }

        PvNode bestChild = null;
        for (PvNode child: this.children) {
            if (bestChild != null && child.ucb() <= bestChild.ucb()) {
                continue;
            }
            bestChild = child;
        }
        assert bestChild != null;
        return bestChild.select();
    }

    /**
     * MCTS expansion. Assumes that this node has not been expanded.
     * @return A random children after expansion, or itself if this is a terminal node.
     */
    private PvNode expand() {
        assert this.children == null;
        if (this.board.winner() != Utils.Side.U) {
            return this;
        }

        this.createChildren();
        int index = PvNode.rng.nextInt(this.children.length);
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

    /**
     * MCTS simulation.
     * @return The evaluation of this board based on the simulation.
     */
    private double simulate() {
        Board board = this.board;
        while (board.winner() == Utils.Side.U) {
            List<Move> actions = board.actions();
            int index = PvNode.rng.nextInt(actions.size());
            board = board.move(actions.get(index));
        }
        if (board.winner() == Utils.Side.D) {
            return 0;
        }
        Utils.Side side = this.board.getTurn() ? Utils.Side.X : Utils.Side.O;
        return board.winner() == side ? PvNode.WIN : -PvNode.WIN;
    }

    /**
     * Updates the utility values of this node based on the result of an MCTS simulation.
     */
    private void backPropagates(double utility) {
        this.U += utility;
        this.N += 1;
        if (!this.isMctsRoot) {
            this.parent.backPropagates(-utility);
        }
    }

    /**
     * Returns the utility value of this node, based on MCTS search.
     */
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

    /**
     * Evaluation routine, when PV search at depth <= 0.
     * Makes use of MCTS.
     */
    public double evaluate() {
        if (this.board.winner() != Utils.Side.U) {
            if (this.board.winner() == Utils.Side.D) {
                return 0;
            } else {
                return -PvNode.WIN;
            }
        }

        this.isMctsRoot = true;
        while (this.N < PvNode.epochs) {
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
     * Called whenever best child is updated.
     * Best child is moved to the front, to be explored first in a later depth.
     */
    private void moveBestChildToFront() {
        if (this.children[0] == this.bestChild) {
            return;
        }
        PvNode temp = this.children[0];
        for (int i = 1; i < this.children.length; i++) {
            if (this.children[i] == this.bestChild) {
                this.children[i] = temp;
                this.children[0] = this.bestChild;
                return;
            }
            PvNode shifted = this.children[i];
            this.children[i] = temp;
            temp = shifted;
        }
        assert false: "Best child not found!";
    }

    /**
     * Searches this subtree and returns the evaluation of this subtree.
     */
    private double search(int depth, double alpha, double beta, NodeType nodeType) throws TimeoutException {
        if (PvNode.isTrackingTime && System.currentTimeMillis() >= PvNode.endTime) {
            throw new TimeoutException();
        }

        if (depth == 0 || this.board.winner() != Utils.Side.U) {
            return this.evaluate();
        }

        if (nodeType == NodeType.NON_PV && this.ttDepth >= depth
                && (this.boundType == BoundType.EXACT
                || (this.boundType == BoundType.UPPER && this.ttScore <= alpha)
                || (this.boundType == BoundType.LOWER && this.ttScore >= beta))) {
            return this.ttScore;
        }

        if (depth == 1 || depth == 2) {
            this.sortChildren();
        }

        // futility pruning
        double staticEval;
        if (this.boundType == BoundType.EXACT) {
            staticEval = this.ttScore;
        } else {
            staticEval = this.evaluate();
        }
        if (this.boundType != BoundType.NONE) {
            if ((this.boundType == BoundType.UPPER && this.ttScore < staticEval)
                    || (this.boundType == BoundType.LOWER && this.ttScore > staticEval)) {
                staticEval = this.ttScore;
            }
        }
        if (nodeType == NodeType.NON_PV
                && depth < 3
                && staticEval >= beta) {
            return staticEval;
        }

        double bestValue = -PvNode.WIN;
        BoundType boundType = BoundType.UPPER;
        boolean nullSearch = false;
        if (this.children == null) {
            this.createChildren();
            this.sortChildren();
        }
        int moveCount = 0;
        for (PvNode child: this.children) {
            moveCount++;
            double value = 0;
            if (nodeType != NodeType.NON_PV) {
                boolean doFullSearch = true;
                if (nullSearch) {
                    // late move reduction
                    int newDepth = depth - 1;
                    if (moveCount > 3 && depth >= 2) {
                        newDepth--;
                    }

                    value = -child.search(newDepth,
                            Math.max(-beta, -alpha - NULL_WINDOW_RATIO), -alpha, NodeType.NON_PV);
                    doFullSearch = alpha < value && value < beta;
                }
                if (doFullSearch) {
                    value = -child.search(depth - 1, -beta, -alpha, NodeType.PV);
                }
            } else {
                value = -child.search(depth - 1, -beta, -alpha, NodeType.NON_PV);
            }
            if (value > bestValue) {
                bestValue = value;
                this.bestChild = child;
                this.moveBestChildToFront();
            }
            if (bestValue > alpha) {
                alpha = bestValue;
                boundType = BoundType.EXACT;
                nullSearch = true;
            }
            if (alpha >= beta) {
                this.updateTt(BoundType.LOWER, bestValue, depth);
                return bestValue;
            }
        }
        this.updateTt(boundType, alpha, depth);
        return bestValue;
    }

    /**
     * Updates the transposition entry of this node.
     * Mutates the respective fields of this node.
     */
    private void updateTt(BoundType boundType, double score, int depth) {
        this.boundType = boundType;
        this.ttScore = score;
        this.ttDepth = depth;
    }

    /**
     * Entry point of the search routine.
     */
    public Move search(int depth) {
        PvNode.isTrackingTime = false;
        try {
            this.search(depth, -PvNode.WIN, PvNode.WIN, NodeType.ROOT);
        } catch (TimeoutException e) {
            // Should not reach here
        }
        return this.bestChild.move;
    }

    /**
     * Entry point of the search routine, with time constraint.
     */
    public Move search(int depth, long endTime) throws TimeoutException {
        PvNode.endTime = endTime;
        PvNode.isTrackingTime = true;
        this.search(depth, -PvNode.WIN, PvNode.WIN, NodeType.ROOT);
        return this.bestChild.move;
    }

    /**
     * Returns the best move. In the case of timeout.
     */
    public Move getBestMove() {
        return this.bestChild.move;
    }

    public String trace() {
        PvNode node = this;
        StringBuilder trace = new StringBuilder();
        trace.append(String.format("Move: %s\n", this.move));
        while (node.bestChild != null) {
            trace.append(node.board.toCompactString());
            trace.append(String.format("; Utility: %,3f, Best move: %s\n", node.utility(), node.bestChild.move));
            node = node.bestChild;
        }
        return trace.toString();
    }

    public PvNode[] getChildren() {
        assert this.children != null;
        return this.children.clone();
    }

    /**
     * Returns the grandchild with the given board
     */
    public PvNode grandchild(Board board) {
        for (PvNode grandchild: this.bestChild.children) {
            if (grandchild.board.equals(board)) {
                return grandchild;
            }
        }
        assert false: "Grandchild with the given board not found";
        return null;
    }

    /**
     * Makes this node a root node.
     */
    public void makeRoot() {
        this.parent = null;
    }
}
