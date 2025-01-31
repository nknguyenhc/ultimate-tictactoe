package manager;

import algo.BaseAlgo;
import board.Board;
import board.Utils;

public class AlgoFight {
    private final BaseAlgo algo1;
    private final BaseAlgo algo2;
    private final boolean includeTrace1;
    private final boolean includeTrace2;
    private final boolean allowPondering;
    private Board game = new Board();

    private final AlgoTimer timer1;
    private final AlgoTimer timer2;

    public AlgoFight(BaseAlgo algo1, BaseAlgo algo2, boolean includeTrace1, boolean includeTrace2,
                     boolean allowPondering) {
        this.algo1 = algo1;
        this.algo2 = algo2;
        this.includeTrace1 = includeTrace1;
        this.includeTrace2 = includeTrace2;
        this.allowPondering = allowPondering;
        this.timer1 = new AlgoTimer(this.algo1, this.includeTrace1, this);
        this.timer2 = new AlgoTimer(this.algo2, this.includeTrace2, this);
    }

    public void run() {
        this.welcome();
        while (this.game.winner() == Utils.Side.U) {
            this.printGame();
            if (this.game.getTurn()) {
                this.timer1.time();
            } else {
                this.timer2.time();
            }
        }
        System.out.println(this.game);
        this.judge();
        this.timeAnalysis();
    }

    public void runWithTime(int time) {
        this.welcome();
        while (this.game.winner() == Utils.Side.U) {
            this.printGame();
            if (this.game.getTurn()) {
                this.algoTurnWithTime(this.algo1, this.algo2, time, this.includeTrace1);
            } else {
                this.algoTurnWithTime(this.algo2, this.algo1, time, this.includeTrace2);
            }
        }
        System.out.println(this.game);
        this.judge();
    }

    private void welcome() {
        System.out.printf("Algo 1: %s%n", this.getAlgoName(this.algo1));
        System.out.printf("Algo 2: %s%n", this.getAlgoName(this.algo2));
    }

    private void printGame() {
        System.out.println(this.game);
        int boardIndex = this.game.getBoardIndexToMove();
        if (boardIndex == 9) {
            System.out.println("Board to move: any board");
        } else {
            int row = boardIndex / 3 + 1;
            int col = boardIndex % 3 + 1;
            System.out.printf("Board to move: (%d, %d)\n", row, col);
        }
        System.out.println(this.game.toCompactString());
    }

    private String getAlgoName(BaseAlgo algo) {
        return algo.getClass().getSimpleName();
    }

    public void algoTurn(BaseAlgo algo, boolean includeTrace) {
        this.announceTurn(algo);
        int move = algo.nextMove(this.game);
        if (includeTrace) {
            System.out.println(algo.trace());
        }
        this.processMove(algo, move);
    }

    private void algoTurnWithTime(BaseAlgo algo, BaseAlgo ponderingAlgo, int time, boolean includeTrace) {
        this.announceTurn(algo);
        if (this.allowPondering) {
            ponderingAlgo.ponder();
        }
        int move = algo.nextMoveWithTime(this.game, time);
        if (this.allowPondering) {
            ponderingAlgo.stopPondering();
        }
        if (includeTrace) {
            System.out.println(algo.trace());
        }
        this.processMove(algo, move);
    }

    private void announceTurn(BaseAlgo algo) {
        System.out.printf("Turn: %s%n", this.getAlgoName(algo));
    }

    private void processMove(BaseAlgo algo, int move) {
        this.game = this.game.move(move);
        System.out.printf("Algo %s chose: %s%n", this.getAlgoName(algo), move);
    }

    private void judge() {
        switch (this.game.winner()) {
            case D:
                System.out.println("Draw!");
                break;
            case X:
                System.out.printf("%s wins!%n", this.getAlgoName(this.algo1));
                break;
            case O:
                System.out.printf("%s wins!%n", this.getAlgoName(this.algo2));
                break;
        }
    }

    private void timeAnalysis() {
        System.out.printf("%s took %.3f on average\n",
                this.getAlgoName(this.algo1),
                this.timer1.getAverageTimeSeconds());
        System.out.printf("%s took %.3f on average\n",
                this.getAlgoName(this.algo2),
                this.timer2.getAverageTimeSeconds());
    }
}
