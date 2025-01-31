package manager;

import algo.BaseAlgo;
import board.Board;
import board.Utils;

import java.util.function.Supplier;

/**
 * Evaluates an algorithm against another one.
 */
public class Evaluator {
    private final Supplier<BaseAlgo> baseAlgoSupplier;
    private final Supplier<BaseAlgo> testAlgoSupplier;

    public Evaluator(Supplier<BaseAlgo> baseAlgoSupplier, Supplier<BaseAlgo> testAlgoSupplier) {
        this.baseAlgoSupplier = baseAlgoSupplier;
        this.testAlgoSupplier = testAlgoSupplier;
    }

    public void run(int timePerStep, int numOfGames) {
        int win = 0;
        int draw = 0;
        int lose = 0;

        for (int i = 0; i < numOfGames; i++) {
            boolean baseGoFirst = i % 2 == 0;
            BaseAlgo baseAlgo = this.baseAlgoSupplier.get();
            BaseAlgo testAlgo = this.testAlgoSupplier.get();
            Utils.Side winner = this.play(baseAlgo, testAlgo, timePerStep, baseGoFirst);
            System.out.printf("Base go first: %b, Winner: %s\n", baseGoFirst, winner);
            switch (winner) {
                case D:
                    draw++;
                    break;
                case X:
                    if (baseGoFirst) {
                        lose++;
                    } else {
                        win++;
                    }
                    break;
                case O:
                    if (baseGoFirst) {
                        win++;
                    } else {
                        lose++;
                    }
                    break;
            }
        }

        System.out.printf("Win: %d, Draw: %d, Lose: %d\n", win, draw, lose);
    }

    /**
     * Simulates a match.
     * @param timePerStep Time allowed for each step.
     * @param baseGoFirst Whether the base engine goes first.
     * @return The winner of the match
     */
    private Utils.Side play(BaseAlgo baseAlgo, BaseAlgo testAlgo, int timePerStep, boolean baseGoFirst) {
        Board board = new Board();
        int moveCount = 0;
        while (board.winner() == Utils.Side.U) {
            if (moveCount % 10 == 0) {
                System.out.print(moveCount + " ");
            }
            byte move;
            if (baseGoFirst ^ (moveCount % 2 == 1)) {
                move = baseAlgo.nextMoveWithTime(board, timePerStep);
            } else {
                move = testAlgo.nextMoveWithTime(board, timePerStep);
            }
            board = board.move(move);
            moveCount++;
        }
        System.out.println();
        return board.winner();
    }
}
