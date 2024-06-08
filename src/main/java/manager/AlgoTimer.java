package manager;

import algo.BaseAlgo;

public class AlgoTimer {
    private long time = 0;
    private int moveCount = 0;
    private final BaseAlgo algo;
    private final boolean includeTrace;
    private final AlgoFight manager;

    public AlgoTimer(BaseAlgo algo, boolean includeTrace, AlgoFight manager) {
        this.algo = algo;
        this.includeTrace = includeTrace;
        this.manager = manager;
    }

    public void time() {
        long startTime = System.currentTimeMillis();
        this.manager.algoTurn(this.algo, this.includeTrace);
        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.printf("Time taken: %d\n", timeTaken);
        this.time += timeTaken;
        this.moveCount += 1;
    }

    public double getAverageTimeSeconds() {
        return (double) this.time / this.moveCount / 1000;
    }
}
