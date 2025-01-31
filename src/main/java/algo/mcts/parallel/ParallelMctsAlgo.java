package algo.mcts.parallel;

import algo.BaseAlgo;
import board.Board;

import java.util.ArrayList;
import java.util.List;

public class ParallelMctsAlgo implements BaseAlgo {
    private ParallelMctsNode root;
    private final boolean continueLastSearch;
    private final boolean multiThreadedPondering;

    private boolean hasPonderedAfterSearch = false;
    private boolean isPondering = false;
    private List<Thread> ponderingThreads = null;

    private final int childrenCountCutoff = 9;
    byte lastMove = -1;

    public ParallelMctsAlgo() {
        this.continueLastSearch = true;
        this.multiThreadedPondering = true;
    }

    public ParallelMctsAlgo(boolean continueLastSearch, boolean multiThreadedPondering) {
        this.continueLastSearch = continueLastSearch;
        this.multiThreadedPondering = multiThreadedPondering;
    }

    @Override
    public byte nextMove(Board board) {
        return -1;
    }

    @Override
    public byte nextMoveWithTime(Board board, int time) {
        final long endTime = System.currentTimeMillis() + time;
        if (this.continueLastSearch) {
            this.setupRoot(board);
        } else {
            this.root = new ParallelMctsNode(board);
        }

        System.gc();
        int childrenCount = this.root.setupChildren();
        if (childrenCount <= this.childrenCountCutoff) {
            this.lastMove = nextMoveMultiThreaded(endTime);
        } else {
            this.lastMove = nextMoveSingleThreaded(endTime);
        }
        return this.lastMove;
    }

    private byte nextMoveMultiThreaded(long endTime) {
        ParallelMctsNode[] children = this.root.getChildren();
        List<Thread> threads = new ArrayList<>();
        for (ParallelMctsNode child: children) {
            Thread newThread = new Thread(() -> {
                child.search(endTime);
            });
            threads.add(newThread);
            newThread.start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.root.getBestMoveByUtility();
    }

    private byte nextMoveSingleThreaded(long endTime) {
        this.root.search(endTime);
        return this.root.getBestMoveByRollout();
    }

    private void setupRoot(Board board) {
        if (this.root == null) {
            this.root = new ParallelMctsNode(board);
        } else if (this.hasPonderedAfterSearch) {
            this.root = this.root.child(board);
            if (this.root == null) {
                this.root = new ParallelMctsNode(board);
            } else {
                this.root.makeRoot();
            }
            this.hasPonderedAfterSearch = false;
        } else {
            ParallelMctsNode child = this.root.child(this.lastMove);
            this.root = child.child(board);
            this.root.makeRoot();
        }
    }

    @Override
    public List<Byte> getMovePredictions() {
        return null;
    }

    @Override
    public String trace() {
        StringBuilder stringBuilder = new StringBuilder();
        ParallelMctsNode[] children = this.root.getChildren();
        for (int i = 0; i < children.length; i++) {
            stringBuilder.append(String.format("\nChild %d:\n", i));
            stringBuilder.append(children[i].trace());
        }
        return stringBuilder.toString();
    }

    @Override
    public void ponder() {
        if (!this.continueLastSearch) {
            return;
        }

        this.setupRootForPondering();
        this.hasPonderedAfterSearch = true;
        this.isPondering = true;
        int childrenCount = this.root.setupChildren();
        if (this.multiThreadedPondering && childrenCount <= this.childrenCountCutoff) {
            this.startMultiThreadPondering();
        } else {
            this.startSingleThreadPondering();
        }
    }

    private void startSingleThreadPondering() {
        this.ponderingThreads = new ArrayList<>();
        Thread ponderingThread = new Thread(() -> {
            while (this.isPondering) {
                this.root.search();
            }
        });
        this.ponderingThreads.add(ponderingThread);
        ponderingThread.start();
    }

    private void startMultiThreadPondering() {
        this.root.setupChildren();
        ParallelMctsNode[] children = this.root.getChildren();
        this.ponderingThreads = new ArrayList<>();
        for (ParallelMctsNode child: children) {
            Thread ponderingThread = new Thread(() -> {
                while (this.isPondering) {
                    child.search();
                }
            });
            this.ponderingThreads.add(ponderingThread);
            ponderingThread.start();
        }
    }

    private void setupRootForPondering() {
        if (this.root == null) {
            this.root = new ParallelMctsNode(new Board());
        } else {
            this.root = this.root.child(this.lastMove);
        }
    }

    @Override
    public void stopPondering() {
        if (!this.continueLastSearch) {
            return;
        }

        this.isPondering = false;
        try {
            for (Thread ponderingThread: this.ponderingThreads) {
                ponderingThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
