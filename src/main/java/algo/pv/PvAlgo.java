package algo.pv;

import algo.BaseAlgo;
import board.Board;

import java.util.List;

public class PvAlgo implements BaseAlgo {
    private final int maxDepth = 9;
    private PvNode root;

    @Override
    public byte nextMove(Board board) {
        this.setupRoot(board);
        byte move = -1;
        this.root.evaluate();
        for (int i = 1; i <= this.maxDepth; i++) {
            System.out.printf("Searching depth %d\n", i);
            move = this.root.search(i);
        }
        return move;
    }

    @Override
    public byte nextMoveWithTime(Board board, int time) {
        this.setupRoot(board);
        byte move;
        long endTime = time + System.currentTimeMillis();
        int i = 1;
        while (true) {
            try {
                move = this.root.search(i, endTime);
            } catch (TimeoutException e) {
                move = this.root.getBestMove();
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

    @Override
    public List<Byte> getMovePredictions() {
        assert this.root != null;
        return this.root.bestMoveSequence();
    }

    @Override
    public void ponder() {

    }

    @Override
    public void stopPondering() {

    }
}
