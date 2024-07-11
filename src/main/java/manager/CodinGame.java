package manager;

import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import board.Board;
import board.Move;

import java.util.Scanner;

public class CodinGame {
    private final Scanner in = new Scanner(System.in);
    private final BaseAlgo algo = new MctsAlgo();
    private boolean isFirstTurn = true;
    private Board board = new Board();

    /**
     * Receives user input and outputs the moves.
     * To be called only once.
     */
    public void run() {
        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            int validActionCount = in.nextInt();
            for (int i = 0; i <= validActionCount; i++) {
                in.nextLine();
            }

            Move action;
            if (this.isFirstTurn) {
                if (opponentRow != -1) {
                    this.board = this.board.move(new Move((byte) opponentRow, (byte) opponentCol));
                }
                action = this.algo.nextMoveWithTime(this.board, 990);
                this.isFirstTurn = false;
            } else {
                this.board = this.board.move(new Move((byte) opponentRow, (byte) opponentCol));
                action = this.algo.nextMoveWithTime(this.board, 90);
            }
            System.out.println(action.sparString());
            this.board = this.board.move(action);
        }
    }
}
