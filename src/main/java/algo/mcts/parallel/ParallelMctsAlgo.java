package algo.mcts.parallel;

import algo.BaseAlgo;
import board.Board;
import board.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelMctsAlgo implements BaseAlgo {
    private ParallelMctsNode root;
    private final boolean continueLastSearch;
    private final boolean multiThreadedPondering;

    private boolean hasPonderedAfterSearch = false;
    private boolean isPondering = false;
    private List<Thread> ponderingThreads = null;

    public ParallelMctsAlgo() {
        this.continueLastSearch = true;
        this.multiThreadedPondering = true;
    }

    public ParallelMctsAlgo(boolean continueLastSearch, boolean multiThreadedPondering) {
        this.continueLastSearch = continueLastSearch;
        this.multiThreadedPondering = multiThreadedPondering;
    }

    @Override
    public Move nextMove(Board board) {
        return null;
    }

    @Override
    public Move nextMoveWithTime(Board board, int time) {
        if (this.continueLastSearch) {
            this.setupRoot(board);
        } else {
            this.root = new ParallelMctsNode(board);
        }

        System.gc();
        final long endTime = System.currentTimeMillis() + time;
        int childrenCount = this.root.setupChildren();
        ParallelMctsNode[] children = this.root.getChildren();

        ExecutorService executorService = Executors.newFixedThreadPool(childrenCount);
        List<Future<Void>> futures = new ArrayList<>();
        for (ParallelMctsNode child: children) {
            Future<Void> future = executorService.submit(() -> {
                child.search(endTime);
                return null;
            });
            futures.add(future);
        }

        for (Future<Void> future: futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return this.root.getBestMove();
    }

    private void setupRoot(Board board) {
        if (this.root == null) {
            this.root = new ParallelMctsNode(board);
        } else if (this.hasPonderedAfterSearch) {
            this.root = this.root.child(board);
            if (this.root == null) {
                this.root = new ParallelMctsNode(board);
            }
            this.hasPonderedAfterSearch = false;
        } else {
            this.root = this.root.grandchild(board);
        }
    }

    @Override
    public List<Move> getMovePredictions() {
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
        if (this.multiThreadedPondering) {
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
            this.root = this.root.getBestUtility();
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