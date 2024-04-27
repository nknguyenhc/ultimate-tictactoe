package manager;

import algo.BaseAlgo;
import board.Board;
import board.Move;
import board.Utils;

public class AlgoFight {
    private final BaseAlgo algo1;
    private final BaseAlgo algo2;
    private final boolean includeTrace1;
    private final boolean includeTrace2;
    private Board game = new Board();

    public AlgoFight(BaseAlgo algo1, BaseAlgo algo2, boolean includeTrace1, boolean includeTrace2) {
        this.algo1 = algo1;
        this.algo2 = algo2;
        this.includeTrace1 = includeTrace1;
        this.includeTrace2 = includeTrace2;
    }

    public void run() {
        this.welcome();
        while (this.game.winner() == Utils.Side.U) {
            this.printGame();
            if (this.game.getTurn()) {
                this.algoTurn(this.algo1, this.includeTrace1);
            } else {
                this.algoTurn(this.algo2, this.includeTrace2);
            }
        }
        System.out.println(this.game);
        this.judge();
    }

    public void runWithTime(int time) {
        this.welcome();
        while (this.game.winner() == Utils.Side.U) {
            this.printGame();
            if (this.game.getTurn()) {
                this.algoTurnWithTime(this.algo1, time, this.includeTrace1);
            } else {
                this.algoTurnWithTime(this.algo2, time, this.includeTrace2);
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

    private void algoTurn(BaseAlgo algo, boolean includeTrace) {
        this.announceTurn(algo);
        Move move = algo.nextMove(this.game);
        if (includeTrace) {
            System.out.println(algo.trace());
        }
        this.processMove(algo, move);
    }

    private void algoTurnWithTime(BaseAlgo algo, int time, boolean includeTrace) {
        this.announceTurn(algo);
        Move move = algo.nextMoveWithTime(this.game, time);
        if (includeTrace) {
            System.out.println(algo.trace());
        }
        this.processMove(algo, move);
    }

    private void announceTurn(BaseAlgo algo) {
        System.out.printf("Turn: %s%n", this.getAlgoName(algo));
    }

    private void processMove(BaseAlgo algo, Move move) {
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
}
