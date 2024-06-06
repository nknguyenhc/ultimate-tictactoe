package algo.pv;

import algo.BaseAlgo;
import board.Board;
import board.Move;

public class PvAlgo implements BaseAlgo {
    private final int maxDepth = 6;
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
        this.root = new PvNode(board);
        Move move = null;
        long endTime = time * 1000L + System.currentTimeMillis();
        this.root.evaluate();
        int i = 1;
        while (true) {
            System.out.printf("Searching depth %d\n", i);
            try {
                move = this.root.search(i, endTime);
            } catch (TimeoutException e) {
                break;
            }
            i += 1;
            if (i > 80) {
                break;
            }
        }
        return move;
    }

    private void setupRoot(Board board) {
        if (this.root == null) {
            this.root = new PvNode(board);
        } else {
            this.root = this.root.grandchild(board);
            this.root.makeRoot();
        }
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
