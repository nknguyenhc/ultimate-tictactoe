package algo.pv;

import algo.BaseAlgo;
import board.Board;
import board.Move;

public class PvAlgo implements BaseAlgo {
    private final int maxDepth = 5;
    private PvNode root;

    @Override
    public Move nextMove(Board board) {
        this.root = new PvNode(board);
        Move move = null;
        this.root.evaluate();
        for (int i = 1; i <= this.maxDepth; i++) {
            System.out.printf("Searching depth %d\n", i);
            move = this.root.search(i);
        }
        return move;
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        return null;
    }

    @Override
    public String trace() {
        assert this.root != null;
        StringBuilder stringBuilder = new StringBuilder();
        PvNode[] children = this.root.getChildren();
        for (int i = 0; i < children.length; i++) {
            stringBuilder.append(String.format("\nChild %d:\n", i));
            stringBuilder.append(children[i].trace());
        }
        return stringBuilder.toString();
    }
}