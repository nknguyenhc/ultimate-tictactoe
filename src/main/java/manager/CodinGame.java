package manager;

import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import board.Board;
import board.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CodinGame {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final BaseAlgo algo = new MctsAlgo();
    private boolean isFirstTurn = true;
    private Board board = new Board();

    /**
     * Receives user input and outputs the moves.
     * To be called only once.
     */
    public void run() {
        while (true) {
            int opponentRow;
            int opponentCol;
            try {
                String[] inputs = in.readLine().split(" ");
                opponentRow = Integer.parseInt(inputs[0]);
                opponentCol = Integer.parseInt(inputs[1]);
                int validActionCount = Integer.parseInt(in.readLine());
                in.skip(validActionCount * 5);
            } catch (IOException e) {
                e.printStackTrace();
                break;
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
